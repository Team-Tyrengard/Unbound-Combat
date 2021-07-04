package net.havengarde.unbound.combat.events;

import net.havengarde.unbound.combat.combatant.Combatant;
import net.havengarde.unbound.combat.skills.UCSkill;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class UCActivateSkillEvent extends Event {
    // region Base event components
    private static final HandlerList handlers = new HandlerList();
    @Override
    public @NotNull HandlerList getHandlers() {
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
