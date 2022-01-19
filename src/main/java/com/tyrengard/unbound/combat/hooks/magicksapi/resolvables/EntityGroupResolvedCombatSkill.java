package com.tyrengard.unbound.combat.hooks.magicksapi.resolvables;

import com.tyrengard.magicksapi.SkillUser;
import com.tyrengard.magicksapi.SkillFailureReason;
import com.tyrengard.magicksapi.resolvables.EntityGroupResolvedSkill;
import com.tyrengard.magicksapi.resolvables.EntityResolvedSkill;
import com.tyrengard.unbound.combat.CombatEngine;
import com.tyrengard.unbound.combat.CombatInstance;
import com.tyrengard.unbound.combat.combatant.Combatant;
import com.tyrengard.unbound.combat.hooks.magicksapi.CombatSkillFailureReason;
import com.tyrengard.unbound.combat.hooks.magicksapi.MagicksAPICombatSkill;
import com.tyrengard.unbound.combat.skills.CombatSkill;
import com.tyrengard.unbound.combat.skills.CombatSkilled;
import org.bukkit.entity.Entity;

import java.util.Collections;
import java.util.List;

/**
 * Implementation of {@link MagicksAPICombatSkill} that extends {@link EntityGroupResolvedSkill}.
 */
public interface EntityGroupResolvedCombatSkill extends MagicksAPICombatSkill, EntityGroupResolvedSkill {
    @Override
    default List<Entity> getTargets(SkillUser caster, Entity mainTarget) {
        if (caster instanceof CombatSkilled combatSkilled &&
                combatSkilled instanceof Combatant<?> combatantCaster)
            return getTargets(combatantCaster.getEntity(), mainTarget, combatSkilled.getLevelForSkill(this));
        else
            return Collections.emptyList();
    }

    @Override
    default SkillFailureReason resolve(SkillUser caster, Entity mainTarget, List<Entity> otherTargets) {
        if (caster instanceof CombatSkilled combatSkilled &&
                combatSkilled instanceof Combatant<?> combatantCaster) {
            Combatant<?> cTarget = CombatEngine.obtainCombatant(mainTarget);
            CombatInstance ci = CombatEngine.obtainActiveInstance(cTarget, combatantCaster);

            resolve(ci, combatantCaster, combatSkilled.getLevelForSkill(this));
            return null;
        } else return CombatSkillFailureReason.UNKNOWN;
    }

    List<Entity> getTargets(Entity caster, Entity mainTarget, byte level);
}
