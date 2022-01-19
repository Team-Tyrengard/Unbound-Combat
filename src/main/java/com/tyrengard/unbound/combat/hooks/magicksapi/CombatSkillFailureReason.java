package com.tyrengard.unbound.combat.hooks.magicksapi;

import com.tyrengard.magicksapi.SkillFailureReason;

public enum CombatSkillFailureReason implements SkillFailureReason {
    INVALID_EQUIPPED_ITEM,
    SKILL_DOES_NOT_RESOLVE,
    UNKNOWN;


    @Override
    public String toString() {
        return switch (this) {
            case INVALID_EQUIPPED_ITEM -> "Invalid equipped item";
            case SKILL_DOES_NOT_RESOLVE -> "Skill does not resolve";
            case UNKNOWN -> "Unknown";
        };
    }
}
