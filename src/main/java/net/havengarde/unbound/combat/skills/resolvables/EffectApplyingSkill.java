package net.havengarde.unbound.combat.skills.resolvables;

import net.havengarde.unbound.combat.combatant.Combatant;
import net.havengarde.unbound.combat.effects.CombatEffect;

interface EffectApplyingSkill {
    CombatEffect resolve(Combatant<?> source, byte level);
}
