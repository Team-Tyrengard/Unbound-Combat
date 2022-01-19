package com.tyrengard.unbound.combat.damage;

/**
 * Represents a source of damage.
 */
public interface DamageSource {
    String getDamageSourceName();
    int getDamageSourceOrder();
    boolean equals(Object o);
    int hashCode();
}
