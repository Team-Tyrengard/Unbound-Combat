package com.tyrengard.unbound.combat;

import com.tyrengard.unbound.combat.damage.DamageInstance;
import com.tyrengard.unbound.combat.damage.DamageSource;
import com.tyrengard.unbound.combat.resistance.ResistanceInstance;
import com.tyrengard.unbound.combat.resistance.ResistanceSource;
import com.tyrengard.unbound.combat.combatant.Combatant;
import com.tyrengard.unbound.combat.enums.CombatOutcome;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedHashMap;

public final class CombatInstance {
    private final Combatant<?> attacker, defender;
    private final LinkedHashMap<DamageSource, ArrayList<DamageInstance>> damageInstances;
    private final LinkedHashMap<ResistanceSource, ArrayList<ResistanceInstance>> resistanceInstances;
    private CombatOutcome outcome;

    CombatInstance(Combatant<?> attacker, Combatant<?> defender) {
        this.attacker = attacker;
        this.defender = defender;
        this.damageInstances = new LinkedHashMap<>();
        this.resistanceInstances = new LinkedHashMap<>();
    }

    // region Setters
    public void addDamageInstance(DamageSource damageSource, DamageInstance damageInstance) {
        damageInstances.computeIfAbsent(damageSource, key -> new ArrayList<>()).add(damageInstance);
    }
    public void addResistanceInstance(ResistanceSource resistanceSource, ResistanceInstance resistanceInstance) {
        resistanceInstances.computeIfAbsent(resistanceSource, key -> new ArrayList<>()).add(resistanceInstance);
    }
    public void setOutcome(CombatOutcome outcome) {
        this.outcome = outcome;
    }
    // endregion

    // region Getters
    public Combatant<?> getAttacker() { return attacker; }
    public Combatant<?> getDefender() { return defender; }
    public Hashtable<DamageSource, ArrayList<DamageInstance>> getDamageInstances() {
        return new Hashtable<>(damageInstances);
    }
    public Hashtable<ResistanceSource, ArrayList<ResistanceInstance>> getResistanceInstances() {
        return new Hashtable<>(resistanceInstances);
    }
    public CombatOutcome getOutcome() {
        return outcome;
    }
    // endregion
}
