package com.tyrengard.unbound.combat.skills;

import com.tyrengard.unbound.combat.stats.CombatStat;

public enum SkillType {
    /**
     * Applied to combatant upon learning.
     */
    PASSIVE,

    /**
     * Applied to combat instance whenever the combatant with this skill enters combat.<br />
     * Used for skills that provide {@link CombatStat}s.
     */
    PASSIVE_COMBAT,
    ACTIVE, // casts on event after activation
    CHARGED, // casts after charging TODO: IMPLEMENT THIS
    CHARGED_EVENT, // casts on event after charging
    CHANNELED, // casts after channeling TODO: IMPLEMENT THIS
    CHANNELING // casts while channeling TODO: IMPLEMENT THIS
    ;

    public boolean isCharged() {
        return switch (this) {
            case CHARGED, CHARGED_EVENT -> true;
            default -> false;
        };
    }
}
