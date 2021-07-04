package net.havengarde.unbound.combat.skills;

public enum SkillType {
    PASSIVE, // always casts on event
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
