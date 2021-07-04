package net.havengarde.unbound.combat.resistance;

import net.havengarde.unbound.combat.damage.DamageType;
import net.havengarde.unbound.combat.damage.ModifierType;

public record ResistanceInstance(DamageType type, ModifierType modifierType, double value) {
}
