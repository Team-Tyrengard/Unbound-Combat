package com.tyrengard.unbound.combat.enums;

public enum CombatOutcome {
    // region Attack outcomes
    // Successful
    SUCCESS_ATTACK,
    /**
     * A successful critical attack. This multiplies a combatant's damage dealt (excluding damage from equipment) by 150%.
     */
    SUCCESS_ATTACK_CRITICAL,

    // Cancelled
    FAILED_ATTACKER_DISARMED,
    FAILED_EVADED,
    FAILED_BLOCKED,
    // endregion

    SUCCESS_EFFECT

    ;

    public boolean isSuccessful() {
        return switch (this) {
            case SUCCESS_ATTACK, SUCCESS_ATTACK_CRITICAL, SUCCESS_EFFECT -> true;
            default -> false;
        };
    }
}
