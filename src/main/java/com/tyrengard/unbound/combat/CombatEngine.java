package com.tyrengard.unbound.combat;

import com.tyrengard.aureycore.foundation.common.utils.TaskUtils;
import com.tyrengard.unbound.combat.combatant.MobCombatant;
import com.tyrengard.unbound.combat.combatant.provider.MobCombatantProvider;
import com.tyrengard.unbound.combat.combatant.provider.PlayerCombatantProvider;
import com.tyrengard.aureycore.foundation.AManager;
import com.tyrengard.aureycore.foundation.Configured;
import com.tyrengard.unbound.combat.combatant.Combatant;
import com.tyrengard.unbound.combat.combatant.PlayerCombatant;
import com.tyrengard.unbound.combat.damage.DamageInstance;
import com.tyrengard.unbound.combat.damage.DamageSource;
import com.tyrengard.unbound.combat.damage.DamageType;
import com.tyrengard.unbound.combat.damage.ModifierType;
import com.tyrengard.unbound.combat.equipment.Equipment;
import com.tyrengard.unbound.combat.equipment.EquipmentHolder;
import com.tyrengard.unbound.combat.equipment.CombatItemData;
import com.tyrengard.unbound.combat.events.CombatDamageEvent;
import com.tyrengard.unbound.combat.events.CombatStartedEvent;
import com.tyrengard.unbound.combat.damage.ResistanceInstance;
import com.tyrengard.unbound.combat.damage.ResistanceSource;
import com.tyrengard.unbound.combat.combatant.impl.DefaultMobCombatantProvider;
import com.tyrengard.unbound.combat.combatant.impl.DefaultPlayerCombatantProvider;
import com.tyrengard.unbound.combat.log.CombatLog;
import com.tyrengard.unbound.combat.effects.CombatEffect;
import com.tyrengard.unbound.combat.enums.CombatOutcome;
import com.tyrengard.unbound.combat.skills.CombatSkill;
import com.tyrengard.unbound.combat.skills.CombatSkilled;
import com.tyrengard.unbound.combat.skills.SkillType;
import com.tyrengard.unbound.combat.skills.resolvables.CombatStatProviderSkill;
import com.tyrengard.unbound.combat.stats.CombatStat;
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
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;
import java.util.*;

public final class CombatEngine extends AManager<UnboundCombat> implements Listener, Configured {
    public static NamespacedKey UC_COMBAT_ENGINE_KEY;
    private static final Map<Combatant<?>, HashMap<Combatant<?>, CombatInstance>> activeCombatInstances = new HashMap<>();
    private static final Map<Combatant<?>, ArrayList<CombatInstance>> combatInstanceCache = new HashMap<>();
    private static final Map<PlayerCombatant, Long> combatExpirationTicks = new HashMap<>();
    private static final Map<UUID, Combatant<?>> combatants = new HashMap<>();
    private static final DecimalFormat df = new DecimalFormat("#.##");
    private static long gameTicks = 0;
    private static int combatEngineTickerTaskId, combatEffectTickerTaskId, combatTagExpirationTaskId;

    private static CombatEngine instance;

    private PlayerCombatantProvider playerCombatantTemplate;
    private MobCombatantProvider mobCombatantTemplate;

    CombatEngine(UnboundCombat plugin) {
        super(plugin);
        instance = this;

        UC_COMBAT_ENGINE_KEY = new NamespacedKey(plugin, "UC_COMBAT_ENGINE_KEY");
    }

    // region Configured overrides
    @Override
    public void loadSettingsFromConfig(FileConfiguration config) throws InvalidConfigurationException {
        int maxSecondsInCombat = 10;

        ConfigurationSection combatSection = config.getConfigurationSection("combat");

        if (combatSection != null) {
            maxSecondsInCombat = combatSection.getInt("max-seconds-in-combat", 10);
        }
    }
    // endregion

    // region Manager overrides
    @Override
    protected void startup() {
        combatEngineTickerTaskId = TaskUtils.runTaskPeriodically(plugin, 0, 1, () -> CombatEngine.gameTicks++);

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
                if (combatExpirationTicks.get(pc) <= gameTicks)
                    tagOutOfCombat(pc);
        });

        // Run on first game tick
        TaskUtils.runTaskLater(plugin, 0, () -> {
            RegisteredServiceProvider<PlayerCombatantProvider> pctProvider =
                    Bukkit.getServicesManager().getRegistration(PlayerCombatantProvider.class);

            if (pctProvider == null) {
                playerCombatantTemplate = new DefaultPlayerCombatantProvider();
                logInfo("No registered player combatant provider, using default");
            } else {
                playerCombatantTemplate = pctProvider.getProvider();
                logInfo("Retrieved player combatant provider: " + pctProvider.getPlugin().getName());
            }

            RegisteredServiceProvider<MobCombatantProvider> mctProvider =
                    Bukkit.getServicesManager().getRegistration(MobCombatantProvider.class);

            if (mctProvider == null) {
                mobCombatantTemplate = new DefaultMobCombatantProvider();
                logInfo("No registered mob combatant provider, using default");
            } else {
                mobCombatantTemplate = mctProvider.getProvider();
                logInfo("Retrieved mob combatant provider: " + mctProvider.getPlugin().getName());
            }
        });
    }

    @Override
    protected void cleanup() {
        TaskUtils.cancelTask(combatEngineTickerTaskId);
        TaskUtils.cancelTask(combatEffectTickerTaskId);
        TaskUtils.cancelTask(combatTagExpirationTaskId);
    }
    // endregion

    // region Event handlers
    @EventHandler(priority = EventPriority.MONITOR)
    private void onPlayerQuit(PlayerQuitEvent e) {
        PlayerCombatant pc = (PlayerCombatant) combatants.remove(e.getPlayer().getUniqueId());
        tagOutOfCombat(pc);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    private void onEntityDamageInvalid(EntityDamageEvent e) {
        if (e.getCause() == EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK)
            e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    private void onEntityDamageRegister(EntityDamageEvent e) {
        Entity defender = e.getEntity();

        if (damageCauseIsPossibleCombat(e.getCause())) {
            logDebug("CombatEngine.onEntityDamageRegister: Detected possible combat for defender " + defender.getEntityId());

            if (e instanceof EntityDamageByEntityEvent e1) {
                Entity attacker;
                CombatStat attackStat;

                // region Get attacker from damager
                if (e1.getDamager() instanceof Projectile projectile && projectile.getShooter() instanceof Entity shooter) {
                    attackStat = CombatStat.RANGED_DAMAGE;
                    attacker = shooter;
                } else {
                    attackStat = CombatStat.MELEE_DAMAGE;
                    attacker = e1.getDamager();
                }
                // endregion

                Combatant<?> cAttacker = obtainCombatant(attacker), cDefender = obtainCombatant(defender);

                // region Attack cooldown check
                if (!cAttacker.performAttack(gameTicks)) {
                    e.setCancelled(true);
                    return;
                }
                // endregion
                // region Blocking check
                if (defender instanceof HumanEntity he && he.isBlocking()) {
                    Bukkit.getPluginManager().callEvent(new CombatDamageEvent((Damageable) defender, 0, attacker,
                            CombatOutcome.FAILED_BLOCKED));
                    e.setCancelled(true);
                    return;
                }
                // endregion
                // region Calculate evasion from stats, equipment, and combat passive skills (if present)
                double evasionChance = getTotalValueForStat(cDefender, CombatStat.EVASION, true, true, true);
                // endregion
                // region Calculate accuracy from stats, equipment, and combat passive skills (if present)
                double accuracy = getTotalValueForStat(cAttacker, CombatStat.ACCURACY, true, true, true);
                // endregion
                // region Evasion check
                double accuracyReducedEvasion = Math.max(0, evasionChance - accuracy);
                double finalEvasionChance = Math.min(CombatStat.EVASION.maxValue(), accuracyReducedEvasion);
                if (finalEvasionChance > 0 && Utils.passChanceCheck(finalEvasionChance)) {
                    Bukkit.getPluginManager().callEvent(new CombatDamageEvent((Damageable) defender, 0, attacker,
                            CombatOutcome.FAILED_EVADED));
                    e.setCancelled(true);
                    return;
                }
                // endregion

                CombatInstance ci = obtainActiveInstance(cDefender, cAttacker);
                CombatOutcome outcome = CombatOutcome.SUCCESS_ATTACK;

                // region Get critical chance from stats, equipment, and combat passive skills (if present), then perform critical strike check
                double critChance = getTotalValueForStat(cAttacker, CombatStat.CRITICAL_CHANCE, true, true, true);

                if (Utils.passChanceCheck(Math.min(CombatStat.CRITICAL_CHANCE.maxValue(), critChance)))
                    outcome = CombatOutcome.SUCCESS_ATTACK_CRITICAL;
                // endregion

                // region Add attack from equipment, per item
                if (cAttacker instanceof EquipmentHolder equipmentHolder) {
                    Equipment equipment = equipmentHolder.getEquipment();
                    for (CombatItemData combatItemData : equipment.getCombatItemData()) {
                        double value = combatItemData.getValueForStat(attackStat);
                        if (value > 0)
                            ci.addDamageInstance(combatItemData,
                                    new DamageInstance(DamageType.PHYSICAL, ModifierType.BASE, value));
                    }
                }
                // endregion
                // region Add attack from stats, and combat passive skills (if present, per skill), and crit multiplier (if critical strike)
                // TODO: support passive skills with attack multipliers
                double attackDamage = cAttacker.getValueForStat(attackStat);
                if (cAttacker instanceof CombatSkilled skilledCombatant) {
                    for (CombatSkill skill : skilledCombatant.getCombatSkills(SkillType.PASSIVE_COMBAT)) {
                        if (skill instanceof CombatStatProviderSkill combatStatProviderSkill) {
                            double attackDamageFromCombatPassive = combatStatProviderSkill.getValueForStat(attackStat, skilledCombatant.getLevelForSkill(skill));
                            if (attackDamageFromCombatPassive > 0 && skill instanceof DamageSource damageSourceSkill)
                                ci.addDamageInstance(damageSourceSkill,
                                        new DamageInstance(DamageType.PHYSICAL, ModifierType.BASE, attackDamageFromCombatPassive));
                        }
                    }
                }
                if (outcome == CombatOutcome.SUCCESS_ATTACK_CRITICAL)
                    attackDamage *= 0.5;
                ci.addDamageInstance(cAttacker, new DamageInstance(DamageType.PHYSICAL, ModifierType.BASE, attackDamage));
                // endregion

                // region Add defense from equipment, per item
                if (cDefender instanceof EquipmentHolder equipmentHolder) {
                    Equipment equipment = equipmentHolder.getEquipment();
                    for (CombatItemData combatItemData : equipment.getCombatItemData()) {
                        double prValue = combatItemData.getValueForStat(CombatStat.PHYSICAL_RESISTANCE);
                        if (prValue > 0)
                            ci.addResistanceInstance(combatItemData,
                                    new ResistanceInstance(DamageType.PHYSICAL, ModifierType.BASE, prValue));
                        double mrValue = combatItemData.getValueForStat(CombatStat.PHYSICAL_RESISTANCE);
                        if (mrValue > 0)
                            ci.addResistanceInstance(combatItemData,
                                    new ResistanceInstance(DamageType.MAGIC, ModifierType.BASE, mrValue));
                    }
                }
                // endregion
                // region Add defense from stats and combat passive skills (if present, per skill)
                // TODO: support passive skills with resistance multipliers
                final double
                        pRes = cDefender.getValueForStat(CombatStat.PHYSICAL_RESISTANCE),
                        mRes = cDefender.getValueForStat(CombatStat.MAGIC_RESISTANCE);
                ci.addResistanceInstance(cDefender, new ResistanceInstance(DamageType.PHYSICAL, ModifierType.BASE, pRes));
                ci.addResistanceInstance(cDefender, new ResistanceInstance(DamageType.MAGIC, ModifierType.BASE, mRes));

                if (cDefender instanceof CombatSkilled skilledCombatant) {
                    for (CombatSkill skill : skilledCombatant.getCombatSkills(SkillType.PASSIVE_COMBAT)) {
                        if (skill instanceof CombatStatProviderSkill combatStatProviderSkill) {
                            double pResItemFromSkill = combatStatProviderSkill.getValueForStat(CombatStat.PHYSICAL_RESISTANCE, skilledCombatant.getLevelForSkill(skill));
                            double mResItemFromSkill = combatStatProviderSkill.getValueForStat(CombatStat.MAGIC_RESISTANCE, skilledCombatant.getLevelForSkill(skill));
                            if (skill instanceof ResistanceSource resistanceSourceSkill) {
                                if (pResItemFromSkill > 0)
                                    ci.addResistanceInstance(resistanceSourceSkill,
                                            new ResistanceInstance(DamageType.PHYSICAL, ModifierType.BASE, pResItemFromSkill));
                                if (mResItemFromSkill > 0)
                                    ci.addResistanceInstance(resistanceSourceSkill,
                                            new ResistanceInstance(DamageType.MAGIC, ModifierType.BASE, mResItemFromSkill));
                            }
                        }
                    }
                }
                // endregion

                ci.setOutcome(outcome);
                CombatStartedEvent combatStartedEvent = new CombatStartedEvent(ci);
                Bukkit.getPluginManager().callEvent(combatStartedEvent);

                if (combatStartedEvent.isCancelled())
                    e.setCancelled(true);
            } else {
                logWarning("CombatEngine.onEntityDamageRegister: Unhandled possible combat for defender " + defender.getEntityId());
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void onEntityDamageResolve(EntityDamageEvent e) {
        Entity defender = e.getEntity();

        if (damageCauseIsPossibleCombat(e.getCause())) {
            logDebug("CombatEngine.onEntityDamageResolve: Resolving combat instance");

            // region Get combatants
            Combatant<?> cAttacker = getAttackerFromEvent(e);
            if (cAttacker == null) {
                logWarning("CombatEngine.onEntityDamageResolve: No attacker combatant in combat instance for defender " + defender.getEntityId());
                return;
            }
            Combatant<?> cDefender = obtainCombatant(defender);
            // endregion
            CombatInstance ci = obtainActiveInstance(cDefender, cAttacker);

            logDebug("CombatEngine.onEntityDamageResolve: Tagging combatants");
            // region Tag attacker and defender in combat (if they're players)
            if (cDefender instanceof PlayerCombatant pc)
                tagInCombat(pc);
            if (cAttacker instanceof PlayerCombatant pc)
                tagInCombat(pc);
            // endregion

            logDebug("CombatEngine.onEntityDamageResolve: Calculating damages from combat instance");
            // region Calculate damages from CombatInstance
            final Map<DamageSource, ArrayList<DamageInstance>> damageInstances = ci.getDamageInstances();

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

            logDebug("CombatEngine.onEntityDamageResolve: Calculating resistances of defender");
            // region Calculate resistances from CombatInstance
            final Map<ResistanceSource, ArrayList<ResistanceInstance>> resistanceInstances = ci.getResistanceInstances();

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

            logDebug("CombatEngine.onEntityDamageResolve: Removing vanilla resistances");
            // region Remove vanilla resistances
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

            logDebug("CombatEngine.onEntityDamageResolve: Applying damages");
            // region Apply damages
            // total damage = total physical damage + total magic damage
            final double
                    resistedPhysicalDamage = calculateDamage(pdTotal, prTotal),
                    resistedMagicDamage = calculateDamage(mdTotal, mrTotal),
                    totalDamage = Math.max(0, resistedPhysicalDamage) + Math.max(0, resistedMagicDamage);

            e.setDamage(totalDamage);
            // endregion

            logDebug("CombatEngine.onEntityDamageResolve: Caching combat instance");
            cacheInstance(cDefender, cAttacker);

            logDebug("CombatEngine.onEntityDamageResolve: Presenting combat outcome");
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
            // TODO: reimplement damage indicator either by using a 3rd party plugin or making our own
//            DamageIndicator di = switch (outcome) {
//                case FAILED_BLOCKED -> new DamageIndicator(loc, ChatFormat.color(ChatColor.DARK_GRAY, "BLOCKED!"));
//                case FAILED_EVADED -> new DamageIndicator(loc, ChatFormat.color(ChatColor.GRAY, "DODGED!"));
//                case SUCCESS_ATTACK_CRITICAL -> new DamageIndicator(loc, ChatFormat.bold(ChatColor.RED, "-" + df.format(damage) + "!"));
//                case SUCCESS_ATTACK -> new DamageIndicator(loc, ChatFormat.color(ChatColor.RED, "-" + df.format(damage)));
//                default -> null;
//            };
//
//            if (di != null)
//                DamageIndicatorAPI.showIndicator(di);
            // endregion
        }
    }

    @EventHandler(ignoreCancelled = true)
    private void onEntityDeath(EntityDeathEvent e) {
        Combatant<?> combatant = obtainCombatant(e.getEntity());

        if (combatant instanceof PlayerCombatant pc)
            tagOutOfCombat(pc);
        else if (combatant instanceof MobCombatant mc)
            combatants.remove(mc.getId());
    }
    // endregion

    // region Static methods

    /**
     * Gets the existing {@link Combatant} for the given {@link Entity}.<br />
     * If one doesn't exist, it will create and register a new one, then return the newly-created {@code Combatant}.<br />
     * @param entity an {@code Entity}
     * @param <E> a type of {@code Entity}
     * @return a {@code Combatant}
     * @throws IllegalArgumentException if the given {@code Entity} is not a {@code Player} or {@code Mob}
     */
    public static <E extends Entity> @NotNull Combatant<?> obtainCombatant(@NotNull E entity) throws IllegalArgumentException {
        if (entity instanceof Player || entity instanceof Mob)
            return combatants.computeIfAbsent(entity.getUniqueId(), key -> {
                instance.logDebug("CombatEngine.obtainCombatant: Generating new combatant for entity " + entity.getEntityId());
                Combatant<?> combatant;
                if (entity instanceof Player p)
                    combatant = instance.playerCombatantTemplate.getCombatant(p);
                else {
                    Mob m = (Mob) entity;
                    combatant = instance.mobCombatantTemplate.getCombatant(m);
                }
                return combatant;
            });
        else
            throw new IllegalArgumentException("Entities that are not of type Mob or Player are not supported.");

    }

    /**
     * Gets the existing {@link CombatInstance} between a defender and an attacker, both of type {@link Combatant}.<br />
     * If one doesn't exist, it will create and register a new one, then return the newly-created {@code CombatInstance}.<br />
     * @param defender the target {@code Combatant}
     * @param attacker the attacking {@code Combatant}
     * @return a {@code CombatInstance}
     */
    public static @NotNull CombatInstance obtainActiveInstance(@NotNull Combatant<?> defender, @NotNull Combatant<?> attacker) {
        instance.logDebug("Retrieving combat instance between " + attacker.getEntity().getEntityId() + " and " + defender.getEntity().getEntityId());
        return activeCombatInstances.computeIfAbsent(defender, key -> new HashMap<>()).computeIfAbsent(attacker, key -> {
            instance.logDebug("Generating new combat instance between " + attacker.getEntity().getEntityId() + " and " + defender.getEntity().getEntityId());
            return new CombatInstance(attacker, defender);
        });
    }

    static void cacheInstance(Combatant<?> defender, Combatant<?> attacker) {
        CombatInstance ci = null;
        HashMap<Combatant<?>, CombatInstance> cisByAttacker = activeCombatInstances.get(defender);
        if (cisByAttacker != null) {
            if (!cisByAttacker.isEmpty())
                ci = cisByAttacker.remove(attacker);
            if (cisByAttacker.isEmpty())
                activeCombatInstances.remove(defender);
        }

        if (ci != null) {
            combatInstanceCache.computeIfAbsent(defender, key -> new ArrayList<>()).add(ci);
            combatInstanceCache.computeIfAbsent(attacker, key -> new ArrayList<>()).add(ci);
        }
    }
    public static @Nullable Combatant<?> getAttackerFromEvent(@NotNull EntityDamageEvent e) {
        if (e instanceof EntityDamageByEntityEvent e1) {
            Entity attacker = e1.getDamager();

            if (attacker instanceof Projectile projectile && projectile.getShooter() instanceof Entity shooter)
                attacker = shooter;

            return obtainCombatant(attacker);
        } else return null;
    }
    static ArrayList<CombatInstance> removeActiveInstancesForCombatant(Combatant<?> combatant) {
        ArrayList<CombatInstance> cis = combatInstanceCache.remove(combatant);
        if (cis == null || cis.isEmpty()) return null;
        else return cis;
    }
    static void tagInCombat(PlayerCombatant pc) {
        combatExpirationTicks.put(pc, gameTicks + (CombatConfig.getMaxSecondsInCombat() * 20L));
    }
    static void tagOutOfCombat(PlayerCombatant pc) {
        combatExpirationTicks.remove(pc);
        CombatLog combatLog = new CombatLog(removeActiveInstancesForCombatant(pc));
        pc.storeCombatLog(combatLog);
    }
    // endregion

    // region Private methods
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
    private double getTotalValueForStat(Combatant<?> combatant, CombatStat combatStat,
                                        boolean includeStats, boolean includeEquipment, boolean includeCombatPassives) {
        double value = 0;

        if (includeStats)
            combatant.getValueForStat(combatStat);

        if (includeEquipment && combatant instanceof EquipmentHolder equipmentHolder) {
            Equipment equipment = equipmentHolder.getEquipment();
            value += equipment.getValueForStat(combatStat);
        }

        if (includeCombatPassives && combatant instanceof CombatSkilled skilledCombatant) {
            for (CombatSkill skill : skilledCombatant.getCombatSkills(SkillType.PASSIVE_COMBAT)) {
                if (skill instanceof CombatStatProviderSkill combatStatProviderSkill)
                    value += combatStatProviderSkill.getValueForStat(combatStat, skilledCombatant.getLevelForSkill(skill));
            }
        }

        return value;
    }
    // endregion
}
