package com.tyrengard.unbound.combat.stats;

public interface CombatStatModifierProvider {
    double getModifierForStat(CombatStat stat);
}
