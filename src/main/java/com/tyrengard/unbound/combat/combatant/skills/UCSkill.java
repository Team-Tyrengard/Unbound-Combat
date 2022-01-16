package com.tyrengard.unbound.combat.combatant.skills;

import com.tyrengard.magicksapi.SkillResource;
import com.tyrengard.magicksapi.enums.CastEvent;
import com.tyrengard.magicksapi.skills.Skill;
import com.tyrengard.unbound.combat.CombatEngine;
import com.tyrengard.unbound.combat.damage.DamageSource;
import com.tyrengard.unbound.combat.equipment.EquipmentType;
import org.bukkit.ChatColor;

import java.util.List;
import java.util.Objects;

import static com.tyrengard.aureycore.foundation.common.stringformat.ChatFormat.color;

public abstract class UCSkill implements Skill, DamageSource {
    private SkillType type;
    private final String name;
    private final byte maxLevel;
    private final CastEvent[] castEvents;
    private final int manaCost;
    private final double cooldown;
    private final double castTime;

    /**
     * Passive event declaration
     * @param name
     * @param maxLevel
     * @param castEvents
     */
    protected UCSkill(String name, int maxLevel, CastEvent... castEvents) {
        this(SkillType.PASSIVE, name, maxLevel, 0, 0, 0, castEvents);
    }

    protected UCSkill(SkillType type, String name, int maxLevel, double cooldown, int manaCost, double castTime, CastEvent... castEvents) {
        this.type = type;
        this.name = name;
        this.maxLevel = (byte) maxLevel;
        this.castEvents = castEvents;
        this.cooldown = cooldown;
        this.manaCost = manaCost;
        this.castTime = castTime;
    }
    public SkillType getType() { return type; }
    public CastEvent[] getCastEvents() {
        return castEvents;
    }
    public byte getMaxLevel() {
        return maxLevel;
    }

    @Override
    public String getName() { return name; }

    @Override
    public SkillResource getResource() {
        return CombatEngine.MANA_RESOURCE;
    }

    @Override
    public int getResourceCost() {
        return manaCost;
    }

    @Override
    public double getCooldown() {
        return cooldown;
    }

    @Override
    public long getCastTicks() {
        return Math.round(castTime * 20);
    }

    public String getClassIconLine() {
        return ChatColor.WHITE + this.getName() + " - " + ChatColor.GRAY + this.getClassIconDescription();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UCSkill ucSkill = (UCSkill) o;
        return getName().equals(ucSkill.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName());
    }

    public abstract boolean allowsWeapon(EquipmentType weaponType);
    public abstract List<String> getDescription(byte level, byte levelsToAdd);
    public abstract String getClassIconDescription();

    protected String highlightCombatValue(String valueName) {
        return color(ChatColor.YELLOW, valueName);
    }
    protected String highlightEffect(String effectName) {
        return color(ChatColor.LIGHT_PURPLE, effectName);
    }
}
