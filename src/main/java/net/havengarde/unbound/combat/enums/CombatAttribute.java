package net.havengarde.unbound.combat.enums;

import net.havengarde.aureycore.common.interfaces.TriConsumer;
import net.havengarde.aureycore.common.stringformat.ChatFormat;
import net.havengarde.aureycore.common.stringformat.Color;
import net.havengarde.aureycore.common.util.NumberUtils;
import net.havengarde.aureycore.customguis.ACustomGUI;
import net.havengarde.aureycore.customguis.Button;
import net.havengarde.aureycore.customguis.Information;
import net.havengarde.unbound.combat.*;
import net.havengarde.unbound.combat.combatant.Combatant;
import net.havengarde.unbound.combat.stats.CombatStat;
import org.bukkit.ChatColor;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum CombatAttribute {
    STRENGTH("Strength", Color.DARK_RED,
            Arrays.asList(ChatFormat.color(Color.DARK_RED, "Strength") +
                ChatColor.GRAY + " affects melee attack damage",
                ChatColor.GRAY + "and allowed weapon weight.")),
    AGILITY("Agility", Color.WHITE,
            Arrays.asList(ChatFormat.color(Color.WHITE, "Agility") +
                ChatColor.GRAY + " affects melee attack speed,",
                ChatColor.GRAY + "movement speed, and dodge chance.")),
    DEXTERITY("Dexterity", Color.GREEN,
            Arrays.asList(ChatFormat.color(Color.GREEN, "Dexterity") +
                ChatColor.GRAY + " affects cast speed, accuracy,",
                ChatColor.GRAY + "and allowed weapon complexity.")),
    CONSTITUTION("Constitution", Color.ORANGE,
            Arrays.asList(ChatFormat.color(Color.ORANGE, "Constitution") +
                ChatColor.GRAY + " affects health, stamina",
                ChatColor.GRAY + "and physical damage resistance.")),
    PERCEPTION("Perception", Color.PURPLE,
            Arrays.asList(ChatFormat.color(Color.PURPLE, "Perception") +
                ChatColor.GRAY + " affects critical chance, and",
                ChatColor.GRAY + "ranged attack damage.")),
    INTELLECT("Intellect", Color.BLUE,
            Arrays.asList(ChatFormat.color(Color.BLUE, "Intellect") +
                ChatColor.GRAY + " affects spell potency, and",
                ChatColor.GRAY + "allowed spell complexity.")),
    WISDOM("Wisdom", Color.YELLOW,
            Arrays.asList(ChatFormat.color(Color.YELLOW, "Wisdom") +
                ChatColor.GRAY + " affects mana, mana regen, and",
                ChatColor.GRAY + "magic damage mitigation."));

    private static final int DEFAULT_PLAYER_HEALTH = 40;
    private static final double STRENGTH_MELEE_DAMAGE_MULTIPLIER = 0.076,
            AGILITY_MELEE_ATTACK_SPEED_MULTIPLIER = 0.016,
            AGILITY_MOVEMENT_SPEED_MULTIPLIER = 0.0003,
            AGILITY_DODGE_CHANCE_MULTIPLIER = 0.0006,
            CONSTITUTION_HEALTH_MULTIPLIER = 3.84,
            PERCEPTION_RANGED_DAMAGE_MULTIPLIER = 0.052,
            PERCEPTION_CRITICAL_CHANCE_MULTIPLIER = 0.001,
            WISDOM_MANA_MULTIPLIER = 11.8,
            WISDOM_MANA_REGEN_MULTIPLIER = 0.472;

    private final String name;
    private final Color color;
    private final List<String> description;

    CombatAttribute(String name, Color color, List<String> description) {
        this.name = name;
        this.color = color;
        this.description = description;
    }

    public String getName() {
        return name;
    }
    public Color getColor() {
        return color;
    }
    public List<String> getDescription() {
        return description;
    }

    public List<String> getStatsItemMeta(short points, short pointsToAdd) {
        switch(this) {
            case STRENGTH:
                double meleeDamage = 1 + NumberUtils.roundToTwoDecimalPlaces((points - 5) * STRENGTH_MELEE_DAMAGE_MULTIPLIER);
                double meleeDamageToAdd = NumberUtils.roundToTwoDecimalPlaces(pointsToAdd * STRENGTH_MELEE_DAMAGE_MULTIPLIER);
                return Arrays.asList(
                        statLine("MELEE ATTACK DAMAGE", meleeDamage, pointsToAdd > 0 ? meleeDamageToAdd : null),
					ChatFormat.underline(ChatColor.WHITE, "ALLOWED WEAPON WEIGHT:") + " " + ChatColor.GRAY + "to be added");
            case AGILITY:
                double as = CombatConstants.BASE_PLAYER_MELEE_ATTACK_SPEED + NumberUtils.roundToTwoDecimalPlaces((points - 5) * AGILITY_MELEE_ATTACK_SPEED_MULTIPLIER);
                double asToAdd = NumberUtils.roundToTwoDecimalPlaces(pointsToAdd * AGILITY_MELEE_ATTACK_SPEED_MULTIPLIER);
                double ms = (CombatConstants.BASE_PLAYER_MOVEMENT_SPEED +
                        NumberUtils.roundToTwoDecimalPlaces((points - 5) * AGILITY_MOVEMENT_SPEED_MULTIPLIER)) * 100;
                double msToAdd = NumberUtils.roundToTwoDecimalPlaces(pointsToAdd * AGILITY_MOVEMENT_SPEED_MULTIPLIER) * 100;
                double dodgeChance = NumberUtils.roundToTwoDecimalPlaces((points - 5) * AGILITY_DODGE_CHANCE_MULTIPLIER * 100);
                double dodgeChanceToAdd = NumberUtils.roundToTwoDecimalPlaces(pointsToAdd * AGILITY_DODGE_CHANCE_MULTIPLIER * 100);
                return Arrays.asList(
                        statLine("MELEE ATTACK SPEED", as, pointsToAdd > 0 ? asToAdd : null),
                        statLine("MOVEMENT SPEED", ms, pointsToAdd > 0 ? msToAdd : null),
                        statLine("DODGE CHANCE", dodgeChance + "%", pointsToAdd > 0 ? dodgeChanceToAdd + "%" : null));
            case DEXTERITY:
                double accuracy = NumberUtils.roundToTwoDecimalPlaces((points - 5) * 0.14 * 100);
                double accuracyToAdd = NumberUtils.roundToTwoDecimalPlaces(pointsToAdd * 0.14 * 100);
                return Arrays.asList(
					ChatFormat.underline(ChatColor.WHITE, "PHYSICAL CAST SPEED:") + " " + ChatColor.GRAY + "to be added",
                        statLine("ACCURACY", accuracy + "%", pointsToAdd > 0 ? accuracyToAdd + "%" : null));
            case CONSTITUTION:
                double health = DEFAULT_PLAYER_HEALTH + Math.round((points - 5) * CONSTITUTION_HEALTH_MULTIPLIER);
                double healthToAdd = Math.round((pointsToAdd - 5) * CONSTITUTION_HEALTH_MULTIPLIER);
                return Arrays.asList(
                        statLine("HEALTH", health, pointsToAdd > 0 ? healthToAdd : null),
					ChatFormat.underline(ChatColor.WHITE, "HEALTH REGEN:") + " " +
						ChatColor.GRAY + "to be added",
					ChatFormat.underline(ChatColor.WHITE, "PHYSICAL DAMAGE MITIGATION:") + " " +
						ChatColor.GRAY + "to be added");
            case PERCEPTION:
                double rangedDamageMin = NumberUtils.roundToTwoDecimalPlaces(4 + ((points - 5) * PERCEPTION_RANGED_DAMAGE_MULTIPLIER)),
                        rangedDamageMax = NumberUtils.roundToTwoDecimalPlaces(rangedDamageMin * 2.5),
                        toAddMin = NumberUtils.roundToTwoDecimalPlaces(pointsToAdd * PERCEPTION_RANGED_DAMAGE_MULTIPLIER),
                        toAddMax = NumberUtils.roundToTwoDecimalPlaces(toAddMin * 2.5),
                        criticalChance = NumberUtils.roundToTwoDecimalPlaces((points - 5) * PERCEPTION_CRITICAL_CHANCE_MULTIPLIER * 100),
                        criticalChanceToAdd = NumberUtils.roundToTwoDecimalPlaces((pointsToAdd - 5) * PERCEPTION_CRITICAL_CHANCE_MULTIPLIER * 100);
                return Arrays.asList(
                        statLine("RANGED ATTACK DAMAGE", rangedDamageMin + "~" + rangedDamageMax,
                                pointsToAdd > 0 ? toAddMin + "~" + toAddMax : null),
                        statLine("CRITICAL CHANCE", criticalChance, pointsToAdd > 0 ? criticalChanceToAdd : null));
            case INTELLECT:
                return Arrays.asList(
                        ChatFormat.underline(ChatColor.WHITE, "MAGIC DAMAGE:") + " " +
                                ChatColor.GRAY + "to be added",
                        ChatFormat.underline(ChatColor.WHITE, "MAGIC CAST SPEED:") + " " +
                                ChatColor.GRAY + "to be added");
            case WISDOM:
                double mana = 50 + NumberUtils.roundToTwoDecimalPlaces((points - 5) * WISDOM_MANA_MULTIPLIER);
                double manaToAdd = NumberUtils.roundToTwoDecimalPlaces(pointsToAdd * WISDOM_MANA_MULTIPLIER);
                double manaRegen = 2 + NumberUtils.roundToTwoDecimalPlaces((points - 5) * WISDOM_MANA_REGEN_MULTIPLIER);
                double manaRegenToAdd = NumberUtils.roundToTwoDecimalPlaces(pointsToAdd * WISDOM_MANA_REGEN_MULTIPLIER);
                return Arrays.asList(
                        statLine("MANA", mana, pointsToAdd > 0 ? manaToAdd : null),
                        statLine("MANA REGEN", manaRegen, pointsToAdd > 0 ? manaRegenToAdd : null),
					ChatFormat.underline(ChatColor.WHITE, "MAGIC DAMAGE MITIGATION:") + " " +
						ChatColor.GRAY + "to be added");
            default:
                return Collections.emptyList();
        }
    }

    public Information asInfoItem(short points) {
        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.addAll(this.getDescription());
        lore.addAll(Arrays.asList("", ChatFormat.bold(ChatColor.AQUA, "ADDED STATS:")));
        lore.addAll(getStatsItemMeta(points, (short) 0));
        return new Information(this.getColor().toWool(), false,
                ChatFormat.color(this.getColor().toChatColor(), this.toString() + " - " + points), lore);
    }

    public <G extends ACustomGUI> Button<G> asButton(G gui, short points, short pointsToAdd,
                                                     TriConsumer<G, InventoryClickEvent, Button<G>> clickHandler) {
        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.addAll(this.getDescription());
        lore.addAll(Arrays.asList("", ChatFormat.bold(ChatColor.AQUA, "ADDED STATS:")));
        lore.addAll(getStatsItemMeta(points, pointsToAdd));
        lore.addAll(Arrays.asList(
                "",
                ChatColor.YELLOW + "LEFT-CLICK" + ChatColor.GRAY + " to add 1",
                ChatColor.YELLOW + "RIGHT-CLICK" + ChatColor.GRAY + " to add 1",
                ChatColor.YELLOW + "SHIFT-LEFT-CLICK" + ChatColor.GRAY + " to add maximum",
                ChatColor.YELLOW + "SHIFT-RIGHT-CLICK" + ChatColor.GRAY + " to remove maximum"
        ));
        lore.addAll(Arrays.asList(
                "",
                ChatColor.YELLOW + "LEFT-CLICK" + ChatColor.GRAY + " to add 1",
                ChatColor.YELLOW + "RIGHT-CLICK" + ChatColor.GRAY + " to add 1",
                ChatColor.YELLOW + "SHIFT-LEFT-CLICK" + ChatColor.GRAY + " to add maximum",
                ChatColor.YELLOW + "SHIFT-RIGHT-CLICK" + ChatColor.GRAY + " to remove maximum"
        ));
        return new Button<G>(this.getColor().toWool(),
                ChatFormat.color(this.getColor().toChatColor(), this.toString() + " - " + points) +
                        (pointsToAdd > 0 ? ChatFormat.color(ChatColor.AQUA, " + " + pointsToAdd) : ""),
                lore, clickHandler);
    }

    public void applyToCombatant(Combatant<?> c, short points) {
        switch (this) {
            case STRENGTH -> {
                c.addValueForStat(CombatStat.MELEE_DAMAGE, NumberUtils.roundToTwoDecimalPlaces((points - 5) * STRENGTH_MELEE_DAMAGE_MULTIPLIER));
                // TODO: set allowed weapon weight
            }
            case AGILITY -> {
                c.addValueForStat(CombatStat.SWING_SPEED, NumberUtils.roundToTwoDecimalPlaces((points - 5) * AGILITY_MELEE_ATTACK_SPEED_MULTIPLIER));
                c.addValueForStat(CombatStat.MOVEMENT_SPEED, NumberUtils.roundToTwoDecimalPlaces((points - 5) * AGILITY_MOVEMENT_SPEED_MULTIPLIER));
                c.addValueForStat(CombatStat.EVASION, NumberUtils.roundToTwoDecimalPlaces((points - 5) * AGILITY_DODGE_CHANCE_MULTIPLIER));
            }
            case DEXTERITY -> {
                // TODO: set cast speed
                c.addValueForStat(CombatStat.ACCURACY, NumberUtils.roundToTwoDecimalPlaces((points - 5) * 0.14 * 100));
                // TODO: set allowed weapon complexity
            }
            case CONSTITUTION -> {
                c.addValueForStat(CombatStat.HEALTH, Math.round((points - 5) * CONSTITUTION_HEALTH_MULTIPLIER));
                // TODO: set stamina
                // TODO: set physical resistance
            }
            case PERCEPTION -> {
                c.addValueForStat(CombatStat.RANGED_DAMAGE, NumberUtils.roundToTwoDecimalPlaces((points - 5) * PERCEPTION_RANGED_DAMAGE_MULTIPLIER));
                c.addValueForStat(CombatStat.CRITICAL_CHANCE, NumberUtils.roundToTwoDecimalPlaces((points - 5) * PERCEPTION_CRITICAL_CHANCE_MULTIPLIER));
            }
            case INTELLECT -> {
                // TODO: set spell power
                // TODO: set allowed spell complexity
            }
            case WISDOM -> {
                c.addValueForStat(CombatStat.MANA, 50 + NumberUtils.roundToTwoDecimalPlaces((points - 5) * WISDOM_MANA_MULTIPLIER));
                c.addValueForStat(CombatStat.MANA_REGEN, 2 + NumberUtils.roundToTwoDecimalPlaces((points - 5) * WISDOM_MANA_REGEN_MULTIPLIER));
                // TODO: set magical resistance
            }
        }
    }

    private String statLine(String attributeLabel, Object currentValue, Object addedValue) {
        return ChatFormat.underline(ChatColor.WHITE, attributeLabel + ":") + " " +
                ChatColor.GRAY + currentValue + (addedValue == null ? "" : ChatColor.AQUA + " + " + addedValue);
    }
}

