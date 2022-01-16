package com.tyrengard.unbound.combat.combatant.skills.resolvables;

import com.tyrengard.magicksapi.FailureReason;
import com.tyrengard.unbound.combat.CombatInstance;
import com.tyrengard.unbound.combat.combatant.Combatant;
import com.tyrengard.unbound.combat.damage.DamageInstance;
import com.tyrengard.unbound.combat.enums.UCSkillFailureReason;
import com.tyrengard.unbound.combat.combatant.skills.UCSkill;

public interface UCResolvedSkill {
    default FailureReason resolve(CombatInstance ci, Combatant<?> cSource, byte level) {
        UCSkill skill = (UCSkill) this;

        if (this instanceof BuffingSkill buffingSkill)
            ci.getDefender().addBuff(buffingSkill.resolve(cSource, level));
        if (this instanceof DebuffingSkill debuffingSkill)
            ci.getDefender().addDebuff(debuffingSkill.resolve(cSource, level));
        if (this instanceof DamagingSkill damagingSkill) {
            DamageInstance di = damagingSkill.resolve(level, ci.getDefender().getEntity());
            if (di != null)
                ci.addDamageInstance(skill, di);
        }
        else return UCSkillFailureReason.SKILL_DOES_NOT_RESOLVE;

        cSource.clearActiveSkills();
        return null;
    }
}
