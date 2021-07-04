package net.havengarde.unbound.combat.enums;

public enum SkillDisplayType {
    NONE,
    CHAT_CASTER,
    CHAT_ALLIES_AREA,
    CHAT_AREA;

    public String toDisplayString() {
        switch (this) {
            case CHAT_AREA: return "Chat all players nearby";
            case CHAT_ALLIES_AREA: return "Chat all allies nearby";
            case CHAT_CASTER: return "Chat caster";
            case NONE: return "Don't display";
        }
        return null;
    }
}
