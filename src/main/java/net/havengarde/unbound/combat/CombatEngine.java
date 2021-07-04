package net.havengarde.unbound.combat;

import net.havengarde.aureycore.common.stringformat.ChatFormat;
import net.havengarde.aureycore.common.util.TaskUtils;
import net.havengarde.aureycore.damageindicator.DamageIndicator;
import net.havengarde.aureycore.damageindicator.DamageIndicatorAPI;
import net.havengarde.aureycore.foundation.AManager;
import net.havengarde.aureycore.foundation.Configured;
import net.havengarde.magicksapi.MagicksAPI;
import net.havengarde.magicksapi.SkillResource;
import net.havengarde.unbound.combat.combatant.PlayerCombatant;
import net.havengarde.unbound.combat.combatant.impl.DefaultMobCombatantTemplate;
import net.havengarde.unbound.combat.equipment.UCEquipment;
import net.havengarde.unbound.combat.combatant.Combatant;
import net.havengarde.unbound.combat.combatant.impl.DefaultPlayerCombatantTemplate;
import net.havengarde.unbound.combat.damage.*;
import net.havengarde.unbound.combat.equipment.UCItemData;
import net.havengarde.unbound.combat.log.CombatLog;
import net.havengarde.unbound.combat.effects.CombatEffect;
import net.havengarde.unbound.combat.events.UCStartedCombatEvent;
import net.havengarde.unbound.combat.events.UCDamageEvent;
import net.havengarde.unbound.combat.enums.CombatOutcome;
import net.havengarde.unbound.combat.equipment.UCEquipmentHolder;
import net.havengarde.unbound.combat.stats.CombatStat;
import net.havengarde.unbound.combat.resistance.ResistanceInstance;
import net.havengarde.unbound.combat.resistance.ResistanceSource;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;
import java.util.*;

public final class CombatEngine extends AManager<UnboundCombat> implements Listener, Configured {
    public static final SkillResource MANA_RESOURCE = new UCMana();
    public static NamespacedKey UC_COMBAT_ENGINE_KEY;

    private static final DefaultPlayerCombatantTemplate defaultPlayerCombatantTemplate = new DefaultPlayerCombatantTemplate();
    private static final DefaultMobCombatantTemplate defaultMobCombatantTemplate = new DefaultMobCombatantTemplate();
    private static final Hashtable<Combatant<?>, Hashtable<Combatant<?>, CombatInstance>> combatInstances = new Hashtable<>();
    private static final Hashtable<Combatant<?>, ArrayList<CombatInstance>> combatInstanceCache = new Hashtable<>();
    private static final Hashtable<PlayerCombatant, Long> combatExpirationTicks = new Hashtable<>();
    private static final HashMap<UUID, Combatant<?>> combatants = new HashMap<>();
    private static final DecimalFormat df = new DecimalFormat("#.##");
    private static long ticks = 0;
    private static int combatEngineTickerTaskId, combatEffectTickerTaskId, combatTagExpirationTaskId;

    private static CombatEngine instance;

    public CombatEngine(UnboundCombat plugin) {
        super(plugin);
        instance = this;

        UC_COMBAT_ENGINE_KEY = new NamespacedKey(plugin, "UC_COMBAT_ENGINE_KEY");
    }

    // region Manager methods
    @Override
    protected void startup() {
        combatEngineTickerTaskId = TaskUtils.runTaskPeriodically(plugin, 0, 1, () -> CombatEngine.ticks++);

        combatEffectTickerTaskId = TaskUtils.runTaskPeriodically(plugin, 0, 20, () -> {
            for (Combatant<?> c : combatants.values()) {
                for (CombatEffect ce : c.getBuffs())
                    if (ce.tick(c.getEntity()))
                        c.removeBuff(ce);
                for (CombatEffect ce : c.getDebuffs())
                    if (ce.tick(c.getEntity()))
                        c.removeDebuff(ce);
            }
        });

        combatTagExpirationTaskId = TaskUtils.runTaskPeriodically(plugin, 0, 1, () -> {
            for (PlayerCombatant pc : new HashSet<>(combatExpirationTicks.keySet()))
                if (combatExpirationTicks.get(pc) <= ticks)
                    tagOutOfCombat(pc);
        });
    }

    @Override
    protected void cleanup() {
        TaskUtils.cancelTask(combatEngineTickerTaskId);
        TaskUtils.cancelTask(combatEffectTickerTaskId);
        TaskUtils.cancelTask(combatTagExpirationTaskId);
    }

    @Override
    public void setConfigDefaults(FileConfiguration config) {
    }

    @Override
    public void loadSettingsFromConfig(FileConfiguration config) throws InvalidConfigurationException {
        CombatConfig combatConfig = new CombatConfig();
        int maxSecondsInCombat = 10;

        ConfigurationSection combatSection = config.getConfigurationSection("combat");

        if (combatSection != null) {
            maxSecondsInCombat = combatSection.getInt("max-seconds-in-combat", 10);
        }

        combatConfig.setMaxSecondsInCombat(maxSecondsInCombat);
    }
    // endregion

    // region Event handlers
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    private void onEntityDamageRegister(EntityDamageEvent e) {
        Entity defender = e.getEntity();

        if (damageCauseIsInvalid(e.getCause()))
            e.setCancelled(true);
        else if (damageCauseIsPossibleCombat(e.getCause())) {
            logDebug("CombatEngine: Detected possible combat for defender " + defender.getEntityId());

            if (e instanceof EntityDamageByEntityEvent e1) {
                Entity attacker = e1.getDamager();
                CombatStat attackStat = CombatStat.MELEE_DAMAGE;

                if (attacker instanceof Projectile projectile && projectile.getShooter() instanceof Entity shooter) {
                    attackStat = CombatStat.RANGED_DAMAGE;
                    attacker = shooter;
                }

                Combatant<?> cAttacker = getCombatant(attacker), cDefender = getCombatant(defender);

                if (e1.getDamager() instanceof Player && !cAttacker.performAttack(ticks)) { // melee attack speed check
                    e.setCancelled(true);
                } else {
                    // region Blocking check
                    if (defender instanceof HumanEntity he && he.isBlocking()) {
                        Bukkit.getPluginManager().callEvent(new UCDamageEvent((Damageable) defender, 0, attacker,
                                CombatOutcome.FAILED_BLOCKED));
                        e.setCancelled(true);
                        return;
                    }
                    // endregion
                    // region Calculate evasion from stats and equipment
                    double evasionChance = cDefender.getValueForStat(CombatStat.EVASION);
                    if (cDefender instanceof UCEquipmentHolder equipmentHolder) {
                        UCEquipment equipment = equipmentHolder.getEquipment();
                        for (UCItemData ucItemData : equipment.getUCItemData()) {
                            double value = ucItemData.getValueForStat(CombatStat.EVASION);
                            if (value > 0) evasionChance += value;
                        }
                    }
                    // endregion
                    // region Calculate accuracy from stats and equipment
                    double accuracy = cAttacker.getValueForStat(CombatStat.ACCURACY);
                    if (cAttacker instanceof UCEquipmentHolder equipmentHolder) {
                        UCEquipment equipment = equipmentHolder.getEquipment();
                        for (UCItemData ucItemData : equipment.getUCItemData()) {
                            double value = ucItemData.getValueForStat(CombatStat.ACCURACY);
                            if (value > 0) accuracy += value;
                        }
                    }
                    // endregion
                    // region Evasion check
                    if (Utils.passChanceCheck(Math.min(CombatStat.EVASION.maxValue(), evasionChance))) {
                        Bukkit.getPluginManager().callEvent(new UCDamageEvent((Damageable) defender, 0, attacker,
                                CombatOutcome.FAILED_EVADED));
                        e.setCancelled(true);
                        return;
                    }
                    // endregion

                    CombatInstance ci = getInstance(cDefender, cAttacker);
                    CombatOutcome outcome = CombatOutcome.SUCCESS_ATTACK;

                    // region Get critical chance from stats and equipment
                    double critChance = cAttacker.getValueForStat(CombatStat.CRITICAL_CHANCE);
                    if (cAttacker instanceof UCEquipmentHolder equipmentHolder) {
                        UCEquipment equipment = equipmentHolder.getEquipment();
                        for (UCItemData ucItemData : equipment.getUCItemData()) {
                            double value = ucItemData.getValueForStat(CombatStat.CRITICAL_CHANCE);
                            if (value > 0) critChance += value;
                        }
                    }

                    if (Utils.passChanceCheck(Math.min(CombatStat.CRITICAL_CHANCE.maxValue(), critChance))) {
                        outcome = CombatOutcome.SUCCESS_ATTACK_CRITICAL;
                    }
                    // endregion
                    // region Add attack from equipment
                    if (cAttacker instanceof UCEquipmentHolder equipmentHolder) {
                        UCEquipment equipment = equipmentHolder.getEquipment();
                        for (UCItemData ucItemData : equipment.getUCItemData()) {
                            double value = ucItemData.getValueForStat(attackStat);
                            if (value > 0)
                                ci.addDamageInstance(ucItemData,
                                        new DamageInstance(DamageType.PHYSICAL, ModifierType.BASE, value));
                        }
                    }
                    // endregion
                    // region Add attack from stats and crit
                    double attackDamage = cAttacker.getValueForStat(attackStat);
                    if (outcome == CombatOutcome.SUCCESS_ATTACK_CRITICAL)
                        attackDamage *= 0.5;
                    ci.addDamageInstance(cAttacker, new DamageInstance(DamageType.PHYSICAL, ModifierType.BASE, attackDamage));
                    // endregion
                    // region Add defense from equipment
                    if (cDefender instanceof UCEquipmentHolder equipmentHolder) {
                        UCEquipment equipment = equipmentHolder.getEquipment();
                        for (UCItemData ucItemData : equipment.getUCItemData()) {
                            double prValue = ucItemData.getValueForStat(CombatStat.PHYSICAL_RESISTANCE);
                            if (prValue > 0)
                                ci.addResistanceInstance(ucItemData,
                                        new ResistanceInstance(DamageType.PHYSICAL, ModifierType.BASE, prValue));
                            double mrValue = ucItemData.getValueForStat(CombatStat.PHYSICAL_RESISTANCE);
                            if (mrValue > 0)
                                ci.addResistanceInstance(ucItemData,
                                        new ResistanceInstance(DamageType.MAGIC, ModifierType.BASE, mrValue));
                        }
                    }
                    // endregion
                    // region Add defense from stats
                    final double    pRes = cDefender.getValueForStat(CombatStat.PHYSICAL_RESISTANCE),
                            mRes = cDefender.getValueForStat(CombatStat.MAGIC_RESISTANCE);
                    ci.addResistanceInstance(cDefender, new ResistanceInstance(DamageType.PHYSICAL, ModifierType.BASE, pRes));
                    ci.addResistanceInstance(cDefender, new ResistanceInstance(DamageType.MAGIC, ModifierType.BASE, mRes));
                    // endregion

                    ci.setOutcome(outcome);
                    UCStartedCombatEvent startedCombatEvent = new UCStartedCombatEvent(ci);
                    Bukkit.getPluginManager().callEvent(startedCombatEvent);

                    if (startedCombatEvent.isCancelled())
                        e.setCancelled(true);
                }
            } else {
                logWarning("Unhandled possible combat for defender " + defender.getEntityId());
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void onEntityDamageResolve(EntityDamageEvent e) {
        Entity defender = e.getEntity();

        if (damageCauseIsPossibleCombat(e.getCause())) {
            logDebug("CombatEngine: resolving combat instance");

            Combatant<?> cAttacker = getAttackerFromEvent(e);
            if (cAttacker == null) {
                logWarning("No attacker combatant in combat instance for defender " + defender.getEntityId());
                return;
            }

            Combatant<?> cDefender = getCombatant(defender);
            CombatInstance ci = getInstance(cDefender, cAttacker);

            // region Tag attacker and defender in combat
            if (cDefender instanceof PlayerCombatant pc)
                tagInCombat(pc);
            if (cAttacker instanceof PlayerCombatant pc)
                tagInCombat(pc);
            // endregion
            // region Calculate damages from CombatInstance
            final Hashtable<DamageSource, ArrayList<DamageInstance>> damageInstances = ci.getDamageInstances();
            logDebug("Calculating damages");

            double  pdBase = 0,
                    pdFlat = 0,
                    pdBaseMul = 0,
                    mdBase = 0,
                    mdFlat = 0,
                    mdBaseMul = 0;
            for (ArrayList<DamageInstance> dis : damageInstances.values()) {
                for (DamageInstance di : dis) {
                    switch (di.type()) {
                        case PHYSICAL -> {
                            switch (di.modifierType()) {
                                case BASE -> pdBase += di.value();
                                case FLAT -> pdFlat += di.value();
                                case PERCENTAGE_BASE -> pdBaseMul += di.value();
                            }
                        }
                        case MAGIC -> {
                            switch (di.modifierType()) {
                                case BASE -> mdBase += di.value();
                                case FLAT -> mdFlat += di.value();
                                case PERCENTAGE_BASE -> mdBaseMul += di.value();
                            }
                        }
                    }
                }
            }

            final double    pdTotal = pdBase + (pdBase * pdBaseMul) + pdFlat,
                            mdTotal = mdBase + (mdBase * mdBaseMul) + mdFlat;
            // endregion
            // region Calculate resistances from CombatInstance
            final Hashtable<ResistanceSource, ArrayList<ResistanceInstance>> resistanceInstances = ci.getResistanceInstances();
            logDebug("Calculating resistances of defender");

            double  prBase = 0,
                    prFlat = 0,
                    prBaseMul = 0,
                    mrBase = 0,
                    mrFlat = 0,
                    mrBaseMul = 0;
            for (ArrayList<ResistanceInstance> ris : resistanceInstances.values()) {
                for (ResistanceInstance ri : ris) {
                    switch (ri.type()) {
                        case PHYSICAL -> {
                            switch (ri.modifierType()) {
                                case BASE -> prBase += ri.value();
                                case FLAT -> prFlat += ri.value();
                                case PERCENTAGE_BASE -> prBaseMul += ri.value();
                            }
                        }
                        case MAGIC -> {
                            switch (ri.modifierType()) {
                                case BASE -> mrBase += ri.value();
                                case FLAT -> mrFlat += ri.value();
                                case PERCENTAGE_BASE -> mrBaseMul += ri.value();
                            }
                        }
                    }
                }
            }
            final double    prTotal = prBase + (prBase * prBaseMul) + prFlat,
                            mrTotal = mrBase + (mrBase * mrBaseMul) + mrFlat;
            // endregion
            // region Remove vanilla resistances
            logDebug("Removing vanilla resistances");
            // remove vanilla resistances
            if (e.isApplicable(EntityDamageEvent.DamageModifier.ABSORPTION))
                e.setDamage(EntityDamageEvent.DamageModifier.ABSORPTION, 0);    // absorption potion effect
            if (e.isApplicable(EntityDamageEvent.DamageModifier.RESISTANCE))
                e.setDamage(EntityDamageEvent.DamageModifier.RESISTANCE, 0);    // resistance potion effect
            if (e.isApplicable(EntityDamageEvent.DamageModifier.BLOCKING))
                e.setDamage(EntityDamageEvent.DamageModifier.BLOCKING, 0);      // blocked damage with shields
            if (e.isApplicable(EntityDamageEvent.DamageModifier.MAGIC))
                e.setDamage(EntityDamageEvent.DamageModifier.MAGIC, 0);         // armor enchantments
            if (e.isApplicable(EntityDamageEvent.DamageModifier.ARMOR))
                e.setDamage(EntityDamageEvent.DamageModifier.ARMOR, 0);         // armor reduction
            // endregion
            // region Apply damages and cache combat instance
            logDebug("Applying damages");
            // total damage = total physical damage + total magic damage
            final double totalDamage =
                    Math.max(0, calculateDamage(pdTotal, prTotal)) + Math.max(0, calculateDamage(mdTotal, mrTotal));

            e.setDamage(totalDamage);
            cacheInstance(cDefender, cAttacker);
            // endregion
            // region Present outcome
            final CombatOutcome outcome = ci.getOutcome();
            Damageable damagee = (Damageable) defender;

            // Non-indicated outcomes
            if (outcome == CombatOutcome.FAILED_ATTACKER_DISARMED) {
                if (cAttacker.getEntity() instanceof Player p)
                    p.sendMessage(ChatColor.DARK_RED + "You are disarmed and cannot attack!");
                return;
            }

            double damage = e.getDamage();
            final Location loc = ((LivingEntity) damagee).getEyeLocation();
            DamageIndicator di = switch (outcome) {
                case FAILED_BLOCKED -> new DamageIndicator(loc, ChatFormat.color(ChatColor.DARK_GRAY, "BLOCKED!"));
                case FAILED_EVADED -> new DamageIndicator(loc, ChatFormat.color(ChatColor.GRAY, "DODGED!"));
                case SUCCESS_ATTACK_CRITICAL -> new DamageIndicator(loc, ChatFormat.bold(ChatColor.RED, "-" + df.format(damage) + "!"));
                case SUCCESS_ATTACK -> new DamageIndicator(loc, ChatFormat.color(ChatColor.RED, "-" + df.format(damage)));
                default -> null;
            };

            if (di != null)
                DamageIndicatorAPI.showIndicator(di);
            // endregion
        }
    }

    @EventHandler(ignoreCancelled = true)
    private void onEntityDeath(EntityDeathEvent e) {
        Combatant<?> combatant = getCombatant(e.getEntity());

        if (combatant instanceof PlayerCombatant pc)
            tagOutOfCombat(pc);
        else
            combatants.remove(combatant.getId());
    }
    // endregion

    public static void addCombatant(Combatant<?> combatant) {
        combatants.put(combatant.getId(), combatant);
        MagicksAPI.registerSkillUser(combatant);
    }
    public static void removeCombatant(UUID id) {
        Combatant<?> combatant = combatants.remove(id);
        if (combatant != null)
            MagicksAPI.unregisterSkillUser(combatant.getEntity());
    }
    public static <E extends Entity> Combatant<?> getCombatant(E entity) {
        return combatants.computeIfAbsent(entity.getUniqueId(), key -> {
            instance.logDebug("Generating new default combatant for entity " + entity.getEntityId());
            if (entity instanceof Player p)
                return defaultPlayerCombatantTemplate.construct(p);
            else if (entity instanceof Mob m)
                return defaultMobCombatantTemplate.construct(m);
            else return new Combatant<E>(entity.getUniqueId()) {
                @Override
                public E getEntity() {
                    return entity;
                }
            };
        });
    }
    public static @NotNull CombatInstance getInstance(@NotNull Combatant<?> defender, @NotNull Combatant<?> attacker) {
        instance.logDebug("Retrieving combat instance between " + attacker.getEntity().getEntityId() + " and " + defender.getEntity().getEntityId());
        return combatInstances.computeIfAbsent(defender, key -> new Hashtable<>()).computeIfAbsent(attacker, key -> {
            instance.logDebug("Generating new combat instance between " + attacker.getEntity().getEntityId() + " and " + defender.getEntity().getEntityId());
            return new CombatInstance(attacker, defender);
        });
    }

    static void cacheInstance(@NotNull Combatant<?> defender, @NotNull Combatant<?> attacker) {
        CombatInstance ci = null;
        Hashtable<Combatant<?>, CombatInstance> cisByAttacker = combatInstances.get(defender);
        if (cisByAttacker != null) {
            if (!cisByAttacker.isEmpty())
                ci = cisByAttacker.remove(attacker);
            if (cisByAttacker.isEmpty())
                combatInstances.remove(defender);
        }

        if (ci != null) {
            combatInstanceCache.computeIfAbsent(defender, key -> new ArrayList<>()).add(ci);
            combatInstanceCache.computeIfAbsent(attacker, key -> new ArrayList<>()).add(ci);
        }
    }
    static @Nullable ArrayList<CombatInstance> removeInstances(@NotNull Combatant<?> combatant) {
        ArrayList<CombatInstance> cis = combatInstanceCache.remove(combatant);
        if (cis.isEmpty()) return null;
        else return cis;
    }
    static void tagInCombat(PlayerCombatant pc) {
        combatExpirationTicks.put(pc, ticks + (CombatConfig.getMaxSecondsInCombat() * 20L));
    }
    static void tagOutOfCombat(PlayerCombatant pc) {
        combatExpirationTicks.remove(pc);
        CombatLog combatLog = new CombatLog(removeInstances(pc));
        pc.storeCombatLog(combatLog);
    }

    private Combatant<?> getAttackerFromEvent(EntityDamageEvent e) {
        if (e instanceof EntityDamageByEntityEvent e1) {
            Entity attacker = e1.getDamager();

            if (attacker instanceof Projectile projectile && projectile.getShooter() instanceof Entity shooter)
                attacker = shooter;

            return getCombatant(attacker);
        } else return null;
    }
    private boolean damageCauseIsInvalid(EntityDamageEvent.DamageCause cause) {
        return switch (cause) {
            case ENTITY_SWEEP_ATTACK -> true;
            default -> false;
        };
    }
    private boolean damageCauseIsPossibleCombat(EntityDamageEvent.DamageCause cause) {
        return switch (cause) {
            case ENTITY_ATTACK, PROJECTILE -> true;
            default -> false;
        };
    }
    private double calculateDamage(double incomingDamage, double resistance) {
        // damageDealt = incomingDamage * (100 / (100 + resistance))
        return incomingDamage * (100 / (100 + resistance));
    }

    private static final class UCMana implements SkillResource {
        private final String name = "Mana";
        @Override
        public String getName() {
            return name;
        }

        @Override
        public int hashCode() {
            return super.hashCode() + this.getName().hashCode();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            UCMana ucMana = (UCMana) o;
            return getName().equals(ucMana.getName());
        }
    }
}
