package com.tyrengard.unbound.combat.skills;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public interface CombatSkilled {
    @NotNull Collection<@NotNull CombatSkill> getCombatSkills(@NotNull SkillType... skillTypes);
    @NotNull Collection<@NotNull CombatSkill> getActiveCombatSkills();
    double getCooldownForSkill(@NotNull CombatSkill skill);
    byte getLevelForSkill(@NotNull CombatSkill skill);
}
