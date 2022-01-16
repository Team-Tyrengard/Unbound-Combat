package com.tyrengard.unbound.combat.log;

import com.tyrengard.unbound.combat.CombatInstance;

import java.util.ArrayList;
import java.util.Date;

public final class CombatLog implements Comparable<CombatLog> {
    private final ArrayList<CombatInstance> combatInstances;
    private final Date combatDate;

    public CombatLog(ArrayList<CombatInstance> combatInstances) {
        this.combatInstances = combatInstances;
        this.combatDate = new Date();
    }

    public ArrayList<CombatInstance> getCombatInstances() {
        return combatInstances;
    }

    public Date getCombatDate() {
        return combatDate;
    }

    @Override
    public int compareTo(CombatLog o) {
        return combatDate.compareTo(o.combatDate);
    }
}
