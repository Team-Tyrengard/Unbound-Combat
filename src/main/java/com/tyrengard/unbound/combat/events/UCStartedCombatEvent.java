package com.tyrengard.unbound.combat.events;

import com.tyrengard.unbound.combat.CombatInstance;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class UCStartedCombatEvent extends Event implements Cancellable {
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

    public UCStartedCombatEvent(CombatInstance combatInstance) {
        this.combatInstance = combatInstance;
    }

    public CombatInstance getCombatInstance() { return combatInstance; }
}
