package com.tyrengard.unbound.combat;

import com.tyrengard.unbound.combat.damage.DamageInstance;
import com.tyrengard.unbound.combat.damage.DamageSource;
import com.tyrengard.unbound.combat.damage.ResistanceInstance;
import com.tyrengard.unbound.combat.damage.ResistanceSource;
import com.tyrengard.unbound.combat.combatant.Combatant;
import com.tyrengard.unbound.combat.enums.CombatOutcome;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Represents an instance of combat between two {@link Combatant}s: an attacker and a defender.<br />
 * Note: This represents a one-way instance of combat. For cases where the defender deals damage back to the attacker, a new {@code CombatInstance} must be used.
 */
public final class CombatInstance {
    private final Combatant<?> attacker, defender;
    private final LinkedHashMap<DamageSource, ArrayList<DamageInstance>> damageInstances;
    private final LinkedHashMap<ResistanceSource, ArrayList<ResistanceInstance>> resistanceInstances;
    private CombatOutcome outcome;

    public CombatInstance(Combatant<?> attacker, Combatant<?> defender) {
        this.attacker = attacker;
        this.defender = defender;
        this.damageInstances = new LinkedHashMap<>();
        this.resistanceInstances = new LinkedHashMap<>();
    }

    // region Setters

    /**
     * Adds damage dealt by the attacker.
     * @param damageSource a {@link DamageSource}
     * @param damageInstance a {@link DamageInstance}
     */
    public void addDamageInstance(@NotNull DamageSource damageSource, @NotNull DamageInstance damageInstance) {
        damageInstances.computeIfAbsent(damageSource, key -> new ArrayList<>()).add(damageInstance);
    }

    /**
     * Adds damage resistance from the defender.
     * @param resistanceSource a {@link ResistanceSource}
     * @param resistanceInstance a {@link ResistanceInstance}
     */
    public void addResistanceInstance(@NotNull ResistanceSource resistanceSource, @NotNull ResistanceInstance resistanceInstance) {
        resistanceInstances.computeIfAbsent(resistanceSource, key -> new ArrayList<>()).add(resistanceInstance);
    }

    /**
     * Sets the outcome of this combat instance.
     * @param outcome a {@link CombatOutcome}
     * @see #getOutcome()
     */
    public void setOutcome(CombatOutcome outcome) {
        this.outcome = outcome;
    }
    // endregion

    // region Getters

    /**
     * Gets the attacker for this combat instance.
     * @return a {@link Combatant}
     */
    public Combatant<?> getAttacker() { return attacker; }

    /**
     * Gets the defender for this combat instance.
     * @return a {@link Combatant}
     */
    public Combatant<?> getDefender() { return defender; }

    /**
     * Gets the {@link DamageInstance}s associated with this combat instance, along with their {@link DamageSource}s.<br />
     * These are used in the damage calculation.
     * @return a {@code Map} of {@code DamageSource}s and their associated {@code List} of {@code DamageInstance}s
     */
    public Map<DamageSource, ArrayList<DamageInstance>> getDamageInstances() {
        return new LinkedHashMap<>(damageInstances);
    }

    /**
     * Gets the {@link ResistanceInstance}s associated with this combat instance, along with their {@link ResistanceSource}s.<br />
     * These are used in the damage calculation.
     * @return a {@code Map} of {@code ResistanceSource}s and their associated {@code List} of {@code ResistanceInstance}s
     */
    public Map<ResistanceSource, ArrayList<ResistanceInstance>> getResistanceInstances() {
        return new LinkedHashMap<>(resistanceInstances);
    }

    /**
     * Gets the outcome of this event
     * @return a {@code CombatOutcome}
     * @see #setOutcome(CombatOutcome)
     */
    public CombatOutcome getOutcome() {
        return outcome;
    }
    // endregion
}
