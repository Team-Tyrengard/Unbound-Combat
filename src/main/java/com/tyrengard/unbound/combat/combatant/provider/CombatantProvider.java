package com.tyrengard.unbound.combat.combatant.provider;

import com.tyrengard.unbound.combat.combatant.Combatant;
import com.tyrengard.unbound.combat.stats.CombatStat;
import org.bukkit.entity.Entity;

import java.util.Hashtable;

public interface CombatantProvider<E extends Entity, C extends Combatant<E>> {
    default Hashtable<CombatStat, Double> getBaseStats() {
        Hashtable<CombatStat, Double> baseStats = new Hashtable<>();

        for (CombatStat stat : CombatStat.values())
            baseStats.put(stat, stat.baseValue());

        return baseStats;
    }

    default C applyTemplate(C c) {
        Hashtable<CombatStat, Double> baseStats = getBaseStats();

        for (CombatStat stat : baseStats.keySet())
            c.addValueForStat(stat, baseStats.getOrDefault(stat, 0.0));
        return c;
    }

    C getCombatant(E entity);
}
