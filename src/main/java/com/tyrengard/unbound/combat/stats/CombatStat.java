package com.tyrengard.unbound.combat.stats;

import org.bukkit.attribute.Attributable;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;

public enum CombatStat {
    MELEE_DAMAGE, WEAPON_WEIGHT,                // Strength
    SWING_SPEED, MOVEMENT_SPEED, EVASION,       // Agility
    WEAPON_COMPLEXITY, CAST_SPEED, ACCURACY,    // Dexterity
    HEALTH, ARMOR_WEIGHT, PHYSICAL_RESISTANCE,  // Constitution
    CRITICAL_CHANCE, RANGED_DAMAGE,             // Perception
    SPELL_POWER, SPELL_COMPLEXITY,              // Intellect
    MANA, MANA_REGEN, MAGIC_RESISTANCE,         // Wisdom
    ;

    private static final String MAX_HEALTH_ATTRIBUTE_MODIFIER_NAME = "UC_MAX_HEALTH";

    public void applyToAttributable(Attributable attributable, double value) {
        switch (this) {
            case HEALTH -> {
                AttributeInstance maxHealthAI = attributable.getAttribute(Attribute.GENERIC_MAX_HEALTH);
                if (maxHealthAI != null) {
                    double maxHealth = maxHealthAI.getBaseValue() + value;

                    for (AttributeModifier mod : maxHealthAI.getModifiers()) {
                        if (mod.getName().equalsIgnoreCase(MAX_HEALTH_ATTRIBUTE_MODIFIER_NAME)) {
                            maxHealthAI.removeModifier(mod);
                            break;
                        }
                    }

                    maxHealthAI.addModifier(new AttributeModifier(MAX_HEALTH_ATTRIBUTE_MODIFIER_NAME,
                            maxHealth - maxHealthAI.getBaseValue(), AttributeModifier.Operation.ADD_NUMBER));

                    if (attributable instanceof Player player)
                        player.setHealthScale(20);
                }
            }
        }
    }

    public double baseValue() {
        return switch (this) {
            case CRITICAL_CHANCE -> 0.05;
            case EVASION -> 0.025;
            default -> 0.0;
        };
    }

    public double maxValue() {
        return switch (this) {
            case CRITICAL_CHANCE -> 0.95;
            case EVASION -> 0.4;
            default -> 0.0;
        };
    }

    public String toUCString() {
        return switch (this) {
            case MELEE_DAMAGE -> "Damage (melee)";
            case WEAPON_WEIGHT -> "Weapon Weight";
            case SWING_SPEED -> "Swing Speed";
            case MOVEMENT_SPEED -> "Movement Speed";
            case EVASION -> "Evasion";
            case CAST_SPEED -> "Cast Speed";
            case ACCURACY -> "Accuracy";
            case WEAPON_COMPLEXITY -> "Weapon Complexity";
            case HEALTH -> "Health";
            case ARMOR_WEIGHT -> "Armor Weight";
            case PHYSICAL_RESISTANCE -> "Resistance (physical)";
            case CRITICAL_CHANCE -> "Critical Chance";
            case RANGED_DAMAGE -> "Damage (ranged)";
            case SPELL_POWER -> "Spell Power";
            case SPELL_COMPLEXITY -> "Spell Complexity";
            case MANA -> "Mana";
            case MANA_REGEN -> "Mana Regen";
            case MAGIC_RESISTANCE -> "Resistance (magic)";
        };
    }
}
