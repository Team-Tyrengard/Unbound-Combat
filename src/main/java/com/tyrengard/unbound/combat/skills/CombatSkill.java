package com.tyrengard.unbound.combat.skills;

import com.tyrengard.unbound.combat.equipment.EquipmentType;

import java.util.List;

/**
 * Represents a learnable skill that is used in combat.<br />
 * This interface will not allow skills to resolve their effects.
 */
public interface CombatSkill {
    /**
     * Get the id of this combat skill.
     * @return an {@code int}
     */
    int getId();

    /**
     * Get the {@link SkillType} of this combat skill.
     * @return a {@code SkillType}
     */
    SkillType getType();

    /**
     * Returns true if this combat skills allows the given {@link EquipmentType}.
     * @return an {@code EquipmentType}
     */
    boolean allowsWeapon(EquipmentType weaponType);

    /**
     * Gets the maximum level of this skill.
     * @return a {@code byte}
     */
    byte getMaxLevel();

    /**
     * Get the short description of this combat skill, used in various GUIs.
     * @return a {@code String}
     */
    String getShortDescription();

    /**
     * <p>
     *     Get the full description of this combat skill, used in various GUIs.
     *     This returns a complete description of the skill, with concrete values adjusted by this skill's current level,
     *     and optionally, the levels to add to this skill.
     * </p>
     * @param level the current level
     * @param levelsToAdd levels to add
     * @return a {@code List<String>}
     */
    List<String> getFullDescription(byte level, byte levelsToAdd);
}
