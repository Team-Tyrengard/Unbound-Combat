package com.tyrengard.unbound.combat.hooks.magicksapi;

import com.tyrengard.magicksapi.SkillUser;
import com.tyrengard.unbound.combat.skills.CombatSkilled;

/**
 * Implementation of {@link CombatSkilled} that uses MagicksAPI's {@link SkillUser} for skill casting, and
 * tracking skill resource usage and cooldowns.
 */
public interface MagicksAPICombatSkilled extends CombatSkilled, SkillUser {}
