package com.tyrengard.unbound.combat.combatant.skills.resolvables;

import com.tyrengard.magicksapi.FailureReason;
import com.tyrengard.magicksapi.SkillUser;
import com.tyrengard.magicksapi.resolvables.EntityResolvedSkill;
import com.tyrengard.unbound.combat.CombatEngine;
import com.tyrengard.unbound.combat.CombatInstance;
import com.tyrengard.unbound.combat.combatant.Combatant;
import com.tyrengard.unbound.combat.combatant.skills.UCSkill;
import org.bukkit.entity.Entity;

public interface UCEntityResolvedSkill extends UCResolvedSkill, EntityResolvedSkill {
    @Override
    default FailureReason resolve(SkillUser source, Entity target) {
        UCSkill skill = (UCSkill) this;
        Combatant<?> cSource = (Combatant<?>) source, cTarget = CombatEngine.getCombatant(target);
        CombatInstance ci = CombatEngine.getInstance(cTarget, cSource);

        byte level = cSource.getSkillMatrix().getLevel(skill);
        resolve(ci, cSource, level);
        return null;
    }
}
