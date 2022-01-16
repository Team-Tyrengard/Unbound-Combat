package com.tyrengard.unbound.combat.stats;

public interface CombatStatHolder extends CombatStatProvider {
    void setValueForStat(CombatStat stat, double value);
}
