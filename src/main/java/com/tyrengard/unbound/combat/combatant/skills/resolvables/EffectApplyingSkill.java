package com.tyrengard.unbound.combat.combatant.skills.resolvables;

import com.tyrengard.unbound.combat.combatant.Combatant;
import com.tyrengard.unbound.combat.effects.CombatEffect;

interface EffectApplyingSkill {
    CombatEffect resolve(Combatant<?> source, byte level);
}
