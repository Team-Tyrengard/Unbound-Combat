package com.tyrengard.unbound.combat.damage;

/**
 * Represents a source of damage resistance.
 */
public interface ResistanceSource {
    String getDamageSourceName();
    int getDamageSourceOrder();
    boolean equals(Object o);
    int hashCode();
}
