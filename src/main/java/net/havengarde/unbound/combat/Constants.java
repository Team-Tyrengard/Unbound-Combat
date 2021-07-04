package net.havengarde.unbound.combat;

import net.havengarde.aureycore.common.util.NumberUtils;
import net.havengarde.magicksapi.SkillResource;
import net.havengarde.magicksapi.SkillUser;
import org.bukkit.attribute.AttributeModifier;

import java.util.Objects;

public final class Constants {
    public static final AttributeModifier CHARGE_MOVEMENT_SPEED_MODIFIER = new AttributeModifier("UC_CHARGING_MOVEMENT_SPEED", -0.5, AttributeModifier.Operation.MULTIPLY_SCALAR_1);
    public static final long REQUIRED_SKILL_CHARGE_TIME = 3000, SKILL_CHARGE_CLEAR_TIME = 500, SKILL_CHARGE_SLOWED_TIME = 500;
}
