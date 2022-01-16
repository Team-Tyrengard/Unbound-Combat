package com.tyrengard.unbound.combat.combatant.skills.resolvables;

import com.tyrengard.magicksapi.SkillUser;
import com.tyrengard.magicksapi.FailureReason;
import com.tyrengard.magicksapi.resolvables.EntityGroupResolvedSkill;
import com.tyrengard.unbound.combat.CombatEngine;
import com.tyrengard.unbound.combat.CombatInstance;
import com.tyrengard.unbound.combat.combatant.Combatant;
import com.tyrengard.unbound.combat.combatant.skills.UCSkill;
import org.bukkit.entity.Entity;

import java.util.List;

public interface UCEntityGroupResolvedSkill extends UCResolvedSkill, EntityGroupResolvedSkill {
    @Override
    default List<Entity> getTargets(SkillUser caster, Entity mainTarget) {
        UCSkill skill = (UCSkill) this;
        Combatant<?> cSource = (Combatant<?>) caster;

        return getTargets(cSource.getEntity(), mainTarget, cSource.getSkillMatrix().getLevel(skill));
    }

    @Override
    default FailureReason resolve(SkillUser source, Entity mainTarget, List<Entity> otherTargets) {
        UCSkill skill = (UCSkill) this;
        Combatant<?> cSource = (Combatant<?>) source, cTarget = CombatEngine.getCombatant(mainTarget);
        CombatInstance ci = CombatEngine.getInstance(cTarget, cSource);

//        if (this instanceof DamagingSkill damagingSkill)
//            ci.addDamageInstance(cSource, skill, damagingSkill.resolve(cSource.getSkillMatrix().getLevel(skill), target));
//        ci.addDamageInstance(combatant, this, resolve( combatant.getSkillMatrix().getLevel(skill), mainTarget, otherTargets);
        return null;
    }

    List<Entity> getTargets(Entity caster, Entity mainTarget, byte level);
}
