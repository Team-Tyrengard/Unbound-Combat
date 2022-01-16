package com.tyrengard.unbound.combat.enums;

import com.tyrengard.magicksapi.FailureReason;

public enum UCSkillFailureReason implements FailureReason {
    NOT_ACTIVATED,
    INVALID_EQUIPPED_ITEM,
    SKILL_DOES_NOT_RESOLVE,
    UNKNOWN;


    @Override
    public String toString() {
        return switch (this) {
            case NOT_ACTIVATED -> "Skill was not activated yet";
            case INVALID_EQUIPPED_ITEM -> "Invalid equipped item";
            case SKILL_DOES_NOT_RESOLVE -> "Skill does not resolve";
            case UNKNOWN -> "Unknown";
        };
    }
}
