package com.tyrengard.unbound.combat.hooks.magicksapi;

import com.tyrengard.magicksapi.SkillFailureReason;
import com.tyrengard.magicksapi.skills.Skill;
import com.tyrengard.unbound.combat.CombatInstance;
import com.tyrengard.unbound.combat.combatant.Combatant;
import com.tyrengard.unbound.combat.damage.DamageInstance;
import com.tyrengard.unbound.combat.effects.CombatEffect;
import com.tyrengard.unbound.combat.skills.CombatSkill;
import com.tyrengard.unbound.combat.skills.resolvables.DamagingSkill;
import com.tyrengard.unbound.combat.skills.resolvables.EffectApplyingSkill;

/**
 * Implementation of {@link CombatSkill} that is for use with MagicksAPI.
 */
public interface MagicksAPICombatSkill extends CombatSkill, Skill {
    default SkillFailureReason resolve(CombatInstance ci, Combatant<?> cSource, byte level) {
        if (this instanceof EffectApplyingSkill effectApplyingSkill) {
            CombatEffect ce = effectApplyingSkill.resolve(cSource, level);
            switch (ce.getType()) {
                case BUFF -> ci.getDefender().addBuff(ce);
                case DEBUFF -> ci.getDefender().addDebuff(ce);
            }
        }
        else if (this instanceof DamagingSkill damagingSkill) {
            DamageInstance di = damagingSkill.resolve(ci.getDefender().getEntity(), level);
            if (di != null)
                ci.addDamageInstance(damagingSkill, di);
        }
        else return CombatSkillFailureReason.SKILL_DOES_NOT_RESOLVE;
        return null;
    }
}
