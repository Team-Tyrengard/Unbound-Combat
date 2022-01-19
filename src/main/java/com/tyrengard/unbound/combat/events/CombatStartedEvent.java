package com.tyrengard.unbound.combat.events;

import com.tyrengard.unbound.combat.CombatInstance;
import com.tyrengard.unbound.combat.combatant.Combatant;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Fires whenever combat starts between two {@link Combatant}s.<br />
 * If this event is cancelled, the {@link CombatInstance} associated with it will be invalidated, and no damage will be dealt.
 */
public class CombatStartedEvent extends Event implements Cancellable {
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

    public CombatStartedEvent(CombatInstance combatInstance) {
        this.combatInstance = combatInstance;
    }

    public CombatInstance getCombatInstance() { return combatInstance; }
}
