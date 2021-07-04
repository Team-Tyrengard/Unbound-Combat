package net.havengarde.unbound.combat.skills.resolvables;

import net.havengarde.magicksapi.FailureReason;
import net.havengarde.unbound.combat.CombatInstance;
import net.havengarde.unbound.combat.enums.UCSkillFailureReason;
import net.havengarde.unbound.combat.combatant.Combatant;
import net.havengarde.unbound.combat.damage.DamageInstance;
import net.havengarde.unbound.combat.skills.UCSkill;

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
