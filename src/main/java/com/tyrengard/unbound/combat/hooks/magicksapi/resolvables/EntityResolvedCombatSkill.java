package com.tyrengard.unbound.combat.hooks.magicksapi.resolvables;

import com.tyrengard.magicksapi.SkillFailureReason;
import com.tyrengard.magicksapi.SkillUser;
import com.tyrengard.magicksapi.resolvables.EntityResolvedSkill;
import com.tyrengard.unbound.combat.CombatEngine;
import com.tyrengard.unbound.combat.CombatInstance;
import com.tyrengard.unbound.combat.combatant.Combatant;
import com.tyrengard.unbound.combat.hooks.magicksapi.CombatSkillFailureReason;
import com.tyrengard.unbound.combat.hooks.magicksapi.MagicksAPICombatSkill;
import com.tyrengard.unbound.combat.skills.CombatSkill;
import com.tyrengard.unbound.combat.skills.CombatSkilled;
import org.bukkit.entity.Entity;

/**
 * Implementation of {@link MagicksAPICombatSkill} that extends {@link EntityResolvedSkill}.
 */
public interface EntityResolvedCombatSkill extends MagicksAPICombatSkill, EntityResolvedSkill {
    @Override
    default SkillFailureReason resolve(SkillUser caster, Entity target) {
        if (caster instanceof CombatSkilled combatSkilled &&
                combatSkilled instanceof Combatant<?> combatantCaster) {
            Combatant<?> cTarget = CombatEngine.obtainCombatant(target);
            CombatInstance ci = CombatEngine.obtainActiveInstance(cTarget, combatantCaster);

            byte level = combatSkilled.getLevelForSkill(this);
            resolve(ci, combatantCaster, level);
            return null;
        } else return CombatSkillFailureReason.UNKNOWN;
    }
}
