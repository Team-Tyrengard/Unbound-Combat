package com.tyrengard.unbound.combat.equipment;

import org.bukkit.inventory.ItemStack;

public interface UCItemDataFactory {
    CombatItemData from(ItemStack itemStack);
}
