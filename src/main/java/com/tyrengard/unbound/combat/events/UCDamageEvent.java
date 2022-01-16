package com.tyrengard.unbound.combat.events;

import com.tyrengard.unbound.combat.enums.CombatOutcome;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public final class UCDamageEvent extends Event implements Cancellable {
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

    private final Damageable entity;
    private final Entity damager;
    private final double damage;
    private final CombatOutcome outcome;

    public UCDamageEvent(Damageable entity, double damage, Entity damager, CombatOutcome outcome) {
        this.entity = entity;
        this.damager = damager;
        this.damage = damage;
        this.outcome = outcome;
    }

    public Damageable getEntity() {
        return entity;
    }

    public Entity getDamager() {
        return damager;
    }

    public double getDamage() {
        return damage;
    }

    public CombatOutcome getOutcome() {
        return outcome;
    }
}
