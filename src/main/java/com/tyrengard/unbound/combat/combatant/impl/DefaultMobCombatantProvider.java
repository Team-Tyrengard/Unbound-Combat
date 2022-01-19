package com.tyrengard.unbound.combat.combatant.impl;

import com.tyrengard.unbound.combat.combatant.MobCombatant;
import com.tyrengard.unbound.combat.combatant.provider.MobCombatantProvider;
import com.tyrengard.unbound.combat.stats.CombatStat;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Mob;

import java.util.Hashtable;

public final class DefaultMobCombatantProvider implements MobCombatantProvider {
    private final Hashtable<EntityType, Hashtable<CombatStat, Double>> baseStatMap = new Hashtable<>();

    public DefaultMobCombatantProvider() {
        // region Zombie base stats
        Hashtable<CombatStat, Double> zombieBaseStats = MobCombatantProvider.super.getBaseStats();
        zombieBaseStats.put(CombatStat.MELEE_DAMAGE, 5.0);
        zombieBaseStats.put(CombatStat.PHYSICAL_RESISTANCE, 12.0);

        baseStatMap.put(EntityType.ZOMBIE, zombieBaseStats);
        baseStatMap.put(EntityType.HUSK, zombieBaseStats);
        baseStatMap.put(EntityType.DROWNED, zombieBaseStats);
        baseStatMap.put(EntityType.ZOMBIE_VILLAGER, zombieBaseStats);
        // endregion
        // region Skeleton base stats
        Hashtable<CombatStat, Double> skeletonBaseStats = MobCombatantProvider.super.getBaseStats();
        skeletonBaseStats.put(CombatStat.MELEE_DAMAGE, 3.0);
        skeletonBaseStats.put(CombatStat.RANGED_DAMAGE, 6.0);

        baseStatMap.put(EntityType.SKELETON, skeletonBaseStats);
        baseStatMap.put(EntityType.STRAY, skeletonBaseStats);
        // endregion
        // region Wither Skeleton base stats
        Hashtable<CombatStat, Double> witherSkeletonBaseStats = MobCombatantProvider.super.getBaseStats();
        witherSkeletonBaseStats.put(CombatStat.MELEE_DAMAGE, 12.0);

        baseStatMap.put(EntityType.WITHER_SKELETON, witherSkeletonBaseStats);
        // endregion
        // region Enderman base stats
        Hashtable<CombatStat, Double> endermanBaseStats = MobCombatantProvider.super.getBaseStats();
        endermanBaseStats.put(CombatStat.MELEE_DAMAGE, 10.5);

        baseStatMap.put(EntityType.ENDERMAN, endermanBaseStats);
        // endregion
        // region Spider base stats
        Hashtable<CombatStat, Double> spiderBaseStats = MobCombatantProvider.super.getBaseStats();
        spiderBaseStats.put(CombatStat.MELEE_DAMAGE, 4.0);

        baseStatMap.put(EntityType.SPIDER, spiderBaseStats);
        // endregion
        // region Cave Spider and Bee base stats
        Hashtable<CombatStat, Double> caveSpiderAndBeeBaseStats = MobCombatantProvider.super.getBaseStats();
        caveSpiderAndBeeBaseStats.put(CombatStat.MELEE_DAMAGE, 3.0);

        baseStatMap.put(EntityType.CAVE_SPIDER, caveSpiderAndBeeBaseStats);
        baseStatMap.put(EntityType.BEE, caveSpiderAndBeeBaseStats);
        // endregion
        // region Dolphin base stats
        Hashtable<CombatStat, Double> dolphinBaseStats = MobCombatantProvider.super.getBaseStats();
        dolphinBaseStats.put(CombatStat.MELEE_DAMAGE, 4.5);

        baseStatMap.put(EntityType.DOLPHIN, dolphinBaseStats);
        // endregion
        // region Iron Golem base stats
        Hashtable<CombatStat, Double> ironGolemBaseStats = MobCombatantProvider.super.getBaseStats();
        ironGolemBaseStats.put(CombatStat.MELEE_DAMAGE, 21.75);

        baseStatMap.put(EntityType.IRON_GOLEM, ironGolemBaseStats);
        // endregion
        // region Piglin base stats
        Hashtable<CombatStat, Double> piglinBaseStats = MobCombatantProvider.super.getBaseStats();
        piglinBaseStats.put(CombatStat.MELEE_DAMAGE, 7.0);
        piglinBaseStats.put(CombatStat.PHYSICAL_RESISTANCE, 20.0);

        baseStatMap.put(EntityType.PIGLIN, piglinBaseStats);

        Hashtable<CombatStat, Double> zombiePiglinBaseStats = MobCombatantProvider.super.getBaseStats();
        zombiePiglinBaseStats.put(CombatStat.MELEE_DAMAGE, 6.0);
        piglinBaseStats.put(CombatStat.PHYSICAL_RESISTANCE, 20.0);

        baseStatMap.put(EntityType.ZOMBIFIED_PIGLIN, zombiePiglinBaseStats);
        // endregion
        // region Panda base stats
        Hashtable<CombatStat, Double> pandaBaseStats = MobCombatantProvider.super.getBaseStats();
        pandaBaseStats.put(CombatStat.MELEE_DAMAGE, 9.0);

        baseStatMap.put(EntityType.PANDA, pandaBaseStats);
        // endregion
        // region Polar Bear base stats
        Hashtable<CombatStat, Double> polarBearBaseStats = MobCombatantProvider.super.getBaseStats();
        polarBearBaseStats.put(CombatStat.MELEE_DAMAGE, 8.0);

        baseStatMap.put(EntityType.POLAR_BEAR, polarBearBaseStats);
        // endregion
        // region Wolf base stats
        Hashtable<CombatStat, Double> wolfBaseStats = MobCombatantProvider.super.getBaseStats();
        wolfBaseStats.put(CombatStat.MELEE_DAMAGE, 7.0);

        baseStatMap.put(EntityType.WOLF, wolfBaseStats);
        // endregion
        // region Blaze base stats
        Hashtable<CombatStat, Double> blazeBaseStats = MobCombatantProvider.super.getBaseStats();
        blazeBaseStats.put(CombatStat.MELEE_DAMAGE, 9.0);
        blazeBaseStats.put(CombatStat.RANGED_DAMAGE, 5.0);

        baseStatMap.put(EntityType.BLAZE, blazeBaseStats);
        // endregion
        // region Creeper base stats
        Hashtable<CombatStat, Double> creeperBaseStats = MobCombatantProvider.super.getBaseStats();
        creeperBaseStats.put(CombatStat.RANGED_DAMAGE, 64.5);

        baseStatMap.put(EntityType.CREEPER, creeperBaseStats);
        // endregion
        // region Elder Guardian base stats
        Hashtable<CombatStat, Double> elderGuardianBaseStats = MobCombatantProvider.super.getBaseStats();
        elderGuardianBaseStats.put(CombatStat.RANGED_DAMAGE, 16.0);

        baseStatMap.put(EntityType.ELDER_GUARDIAN, elderGuardianBaseStats);
        // endregion
        // region Endermite base stats
        Hashtable<CombatStat, Double> endermiteBaseStats = MobCombatantProvider.super.getBaseStats();
        endermiteBaseStats.put(CombatStat.MELEE_DAMAGE, 3.0);

        baseStatMap.put(EntityType.ENDERMITE, endermiteBaseStats);
        // endregion
        // region Evoker base stats
        Hashtable<CombatStat, Double> evokerBaseStats = MobCombatantProvider.super.getBaseStats();
        evokerBaseStats.put(CombatStat.RANGED_DAMAGE, 12.5);

        baseStatMap.put(EntityType.EVOKER, evokerBaseStats);
        // endregion
        // region Ghast base stats
        Hashtable<CombatStat, Double> ghastBaseStats = MobCombatantProvider.super.getBaseStats();
        ghastBaseStats.put(CombatStat.RANGED_DAMAGE, 6.0);

        baseStatMap.put(EntityType.GHAST, ghastBaseStats);
        // endregion
        // region Guardian base stats
        Hashtable<CombatStat, Double> guardianBaseStats = MobCombatantProvider.super.getBaseStats();
        guardianBaseStats.put(CombatStat.RANGED_DAMAGE, 13.5);

        baseStatMap.put(EntityType.GUARDIAN, guardianBaseStats);
        // endregion
        // region Hoglin base stats
        Hashtable<CombatStat, Double> hoglinBaseStats = MobCombatantProvider.super.getBaseStats();
        hoglinBaseStats.put(CombatStat.MELEE_DAMAGE, 8.0);

        baseStatMap.put(EntityType.HOGLIN, hoglinBaseStats);
        // endregion
        // region Phantom base stats
        Hashtable<CombatStat, Double> phantomBaseStats = MobCombatantProvider.super.getBaseStats();
        phantomBaseStats.put(CombatStat.MELEE_DAMAGE, 6.0);

        baseStatMap.put(EntityType.PHANTOM, phantomBaseStats);
        // endregion
        // region Magma Cube base stats
        Hashtable<CombatStat, Double> magmaCubeBaseStats = MobCombatantProvider.super.getBaseStats();
        magmaCubeBaseStats.put(CombatStat.MELEE_DAMAGE, 5.0);

        baseStatMap.put(EntityType.MAGMA_CUBE, magmaCubeBaseStats);
        // endregion
        // region Slime base stats
        Hashtable<CombatStat, Double> slimeBaseStats = MobCombatantProvider.super.getBaseStats();
        slimeBaseStats.put(CombatStat.MELEE_DAMAGE, 4.0);

        baseStatMap.put(EntityType.SLIME, slimeBaseStats);
        // endregion
        // region Vex base stats
        Hashtable<CombatStat, Double> vexBaseStats = MobCombatantProvider.super.getBaseStats();
        vexBaseStats.put(CombatStat.MELEE_DAMAGE, 15.0);

        baseStatMap.put(EntityType.VEX, vexBaseStats);
        // endregion
        // region Ravager base stats
        Hashtable<CombatStat, Double> ravagerBaseStats = MobCombatantProvider.super.getBaseStats();
        ravagerBaseStats.put(CombatStat.MELEE_DAMAGE, 24.0);
        ravagerBaseStats.put(CombatStat.RANGED_DAMAGE, 16.0);

        baseStatMap.put(EntityType.RAVAGER, ravagerBaseStats);
        // endregion
        // region Pillager base stats
        Hashtable<CombatStat, Double> pillagerBaseStats = MobCombatantProvider.super.getBaseStats();
        pillagerBaseStats.put(CombatStat.MELEE_DAMAGE, 5.0);
        pillagerBaseStats.put(CombatStat.RANGED_DAMAGE, 6.0);

        baseStatMap.put(EntityType.PILLAGER, pillagerBaseStats);
        // endregion
        // region Vindicator base stats
        Hashtable<CombatStat, Double> vindicatorBaseStats = MobCombatantProvider.super.getBaseStats();
        vindicatorBaseStats.put(CombatStat.MELEE_DAMAGE, 12.0);

        baseStatMap.put(EntityType.VINDICATOR, vindicatorBaseStats);
        // endregion
    }

    @Override
    public MobCombatant applyTemplate(MobCombatant mobCombatant) {
        Hashtable<CombatStat, Double> baseStats = baseStatMap.getOrDefault(mobCombatant.getEntity().getType(),
                MobCombatantProvider.super.getBaseStats());

        for (CombatStat stat : baseStats.keySet())
            mobCombatant.addValueForStat(stat, baseStats.getOrDefault(stat, 0.0));
        return mobCombatant;
    }

    @Override
    public MobCombatant getCombatant(Mob entity) {
        MobCombatant mc = new MobCombatant(entity.getUniqueId()) {
            @Override
            public Mob getEntity() {
                return entity;
            }
        };

        return applyTemplate(mc);
    }
}
