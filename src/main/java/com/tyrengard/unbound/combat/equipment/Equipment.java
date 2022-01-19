package com.tyrengard.unbound.combat.equipment;

import com.tyrengard.unbound.combat.stats.CombatStat;
import com.tyrengard.unbound.combat.stats.CombatStatProvider;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.*;

import static com.tyrengard.unbound.combat.managers.EquipmentManager.getCombatItemDataFromItemStack;

public final class Equipment implements CombatStatProvider {
    private final Entity owner;
    private final HashMap<EquipmentSlot, CombatItemData> equipmentItemData = new HashMap<>(EquipmentSlot.values().length);

    public Equipment(Entity entity) {
        this.owner = entity;
        this.updateAll();
    }

    public List<CombatItemData> getCombatItemData() {
        return new ArrayList<>(equipmentItemData.values());
    }

    public void updateAll() {
        if (owner instanceof Player pAttacker) {
            PlayerInventory pi = pAttacker.getInventory();
            update(EquipmentSlot.HAND, pi.getItemInMainHand());
            update(EquipmentSlot.OFF_HAND, pi.getItemInOffHand());
            update(EquipmentSlot.HEAD, pi.getHelmet());
            update(EquipmentSlot.CHEST, pi.getChestplate());
            update(EquipmentSlot.LEGS, pi.getLeggings());
            update(EquipmentSlot.FEET, pi.getBoots());
        } else if (owner instanceof LivingEntity leAttacker) {
            EntityEquipment ee = leAttacker.getEquipment();
            if (ee != null) {
                update(EquipmentSlot.HAND, ee.getItemInMainHand());
                update(EquipmentSlot.OFF_HAND, ee.getItemInOffHand());
                update(EquipmentSlot.HEAD, ee.getHelmet());
                update(EquipmentSlot.CHEST, ee.getChestplate());
                update(EquipmentSlot.LEGS, ee.getLeggings());
                update(EquipmentSlot.FEET, ee.getBoots());
            }
        }
    }

    public void update(EquipmentSlot slot, ItemStack itemStack) {
        CombatItemData combatItemData = itemStack == null ? null : getCombatItemDataFromItemStack(itemStack);
        equipmentItemData.remove(slot);
        if (combatItemData != null)
            equipmentItemData.put(slot, combatItemData);
    }

    public double getValueForStat(CombatStat stat) {
        double value = 0;
        for (CombatItemData combatItemData : equipmentItemData.values())
            value += combatItemData.getValueForStat(stat);
        return value;
    }
}
