package com.tyrengard.unbound.combat.skills.resolvables;

import com.tyrengard.unbound.combat.combatant.Combatant;
import com.tyrengard.unbound.combat.effects.CombatEffect;
import com.tyrengard.unbound.combat.effects.CombatEffectType;
import com.tyrengard.unbound.combat.skills.CombatSkill;

/**
 * Represents a {@link CombatSkill} that resolves into a {@link CombatEffect}.
 */
public interface EffectApplyingSkill extends CombatSkill {
    CombatEffect resolve(Combatant<?> source, byte level);
}
