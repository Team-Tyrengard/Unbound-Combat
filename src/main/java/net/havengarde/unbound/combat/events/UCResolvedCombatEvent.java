package net.havengarde.unbound.combat.events;

import net.havengarde.unbound.combat.CombatInstance;
import net.havengarde.unbound.combat.enums.CombatOutcome;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class UCResolvedCombatEvent extends Event implements Cancellable {
    // region Base event components
    private static final HandlerList handlers = new HandlerList();
    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }
    public static HandlerList getHandlerList() {
        return handlers;
    }
    private boolean cancelled = false;
    public boolean isCancelled() { return cancelled; }
    public void setCancelled(boolean cancelled) { this.cancelled = cancelled; }
    // endregion

    private final CombatInstance combatInstance;
    private CombatOutcome outcome;

    public UCResolvedCombatEvent(CombatInstance combatInstance) {
        this.combatInstance = combatInstance;
    }

    public CombatInstance getCombatInstance() { return combatInstance; }

    public CombatOutcome getOutcome() {
        return outcome;
    }

    public void setOutcome(CombatOutcome outcome) {
        this.outcome = outcome;
    }
}
