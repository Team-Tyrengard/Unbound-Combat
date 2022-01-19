package com.tyrengard.unbound.combat.damage;

import com.tyrengard.unbound.combat.damage.DamageType;
import com.tyrengard.unbound.combat.damage.ModifierType;

public record ResistanceInstance(DamageType type, ModifierType modifierType, double value) {
}
