package net.havengarde.unbound.combat;

import net.havengarde.unbound.combat.combatant.Combatant;
import net.havengarde.unbound.combat.damage.*;
import net.havengarde.unbound.combat.enums.CombatOutcome;
import net.havengarde.unbound.combat.resistance.ResistanceInstance;
import net.havengarde.unbound.combat.resistance.ResistanceSource;
import org.jetbrains.annotations.NotNull;

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
    public void addDamageInstance(@NotNull DamageSource damageSource, @NotNull DamageInstance damageInstance) {
        damageInstances.computeIfAbsent(damageSource, key -> new ArrayList<>()).add(damageInstance);
    }
    public void addResistanceInstance(@NotNull ResistanceSource resistanceSource, @NotNull ResistanceInstance resistanceInstance) {
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
