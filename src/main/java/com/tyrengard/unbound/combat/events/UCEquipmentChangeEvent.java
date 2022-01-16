package com.tyrengard.unbound.combat.events;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class UCEquipmentChangeEvent extends Event implements Cancellable {
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

    private final LivingEntity entity;
    private final EquipmentSlot slot;
    private final ItemStack newItem;

    public UCEquipmentChangeEvent(LivingEntity entity, EquipmentSlot slot, ItemStack newItem) {
        this.entity = entity;
        this.slot = slot;
        this.newItem = newItem;
    }

    public LivingEntity getEntity() {
        return entity;
    }

    public EquipmentSlot getSlot() {
        return slot;
    }

    public ItemStack getNewItem() {
        return newItem;
    }
}
