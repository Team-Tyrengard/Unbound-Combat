package com.tyrengard.unbound.combat.events;

import com.tyrengard.unbound.combat.combatant.Combatant;
import com.tyrengard.unbound.combat.combatant.skills.UCSkill;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.List;

public final class UCActivateSkillEvent extends Event {
    // region Base event components
    private static final HandlerList handlers = new HandlerList();
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
    public static HandlerList getHandlerList() {
        return handlers;
    }
    // endregion

    private final Combatant combatant;
    private final List<UCSkill> activatedSkills;
    private final boolean charged;

    public UCActivateSkillEvent(Combatant combatant, List<UCSkill> activatedSkills, boolean charged) {
        this.combatant = combatant;
        this.activatedSkills = activatedSkills;
        this.charged = charged;
    }

    public Combatant getCombatant() { return combatant; }
    public List<UCSkill> getActivatedSkills() { return activatedSkills; }
    public boolean isCharged() { return charged; }
}
