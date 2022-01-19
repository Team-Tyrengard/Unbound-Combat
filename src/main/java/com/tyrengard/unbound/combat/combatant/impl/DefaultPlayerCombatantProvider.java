package com.tyrengard.unbound.combat.combatant.impl;

import com.tyrengard.unbound.combat.combatant.PlayerCombatant;
import com.tyrengard.unbound.combat.combatant.provider.PlayerCombatantProvider;
import com.tyrengard.unbound.combat.skills.CombatSkill;
import com.tyrengard.unbound.combat.stats.CombatStat;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Hashtable;

public class DefaultPlayerCombatantProvider implements PlayerCombatantProvider {

    @Override
    public Hashtable<CombatStat, Double> getBaseStats() {
        Hashtable<CombatStat, Double> baseStats = new Hashtable<>();
        for (CombatStat cs : CombatStat.values())
            baseStats.put(cs, switch (cs) {
                case MELEE_DAMAGE -> 1.0;
                case SWING_SPEED -> 4.0;
                case HEALTH -> 20.0;
                case MANA -> 40.0;
                case MANA_REGEN -> 2.0;
                case EVASION -> 0.025;
                case CRITICAL_CHANCE -> 0.05;
                default -> 0.0;
            });

        return baseStats;
    }

    @Override
    public PlayerCombatant getCombatant(Player entity) {
        PlayerCombatant pc = new PlayerCombatant(entity.getUniqueId()) {};

        return applyTemplate(pc);
    }
}
