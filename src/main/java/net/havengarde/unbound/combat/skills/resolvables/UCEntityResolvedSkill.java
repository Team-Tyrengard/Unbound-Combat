package net.havengarde.unbound.combat.skills.resolvables;

import net.havengarde.magicksapi.FailureReason;
import net.havengarde.magicksapi.SkillUser;
import net.havengarde.magicksapi.resolvables.EntityResolvedSkill;
import net.havengarde.unbound.combat.CombatEngine;
import net.havengarde.unbound.combat.CombatInstance;
import net.havengarde.unbound.combat.combatant.Combatant;
import net.havengarde.unbound.combat.skills.UCSkill;
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
