package com.tyrengard.unbound.combat.damage;

/**
 * Represents an instance of damage.
 */
public record DamageInstance(DamageType type, ModifierType modifierType, double value) {
}
