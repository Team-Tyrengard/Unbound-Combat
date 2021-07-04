package net.havengarde.unbound.combat.equipment;

import net.havengarde.unbound.combat.stats.CombatStat;
import net.havengarde.unbound.combat.stats.CombatStatProvider;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.*;

import static net.havengarde.unbound.combat.EquipmentManager.getUCItemDataFromItemStack;

public final class UCEquipment implements CombatStatProvider {
    private final Entity owner;
    private final Hashtable<EquipmentSlot, UCItemData> equipmentItemData = new Hashtable<>(EquipmentSlot.values().length);

    public UCEquipment(Entity entity) {
        this.owner = entity;
        this.updateAll();
    }

    public List<UCItemData> getUCItemData() {
        return new ArrayList<>(equipmentItemData.values());
    }

    public void updateAll() {
        update(EquipmentSlot.HAND);
        update(EquipmentSlot.OFF_HAND);
        update(EquipmentSlot.HEAD);
        update(EquipmentSlot.CHEST);
        update(EquipmentSlot.LEGS);
        update(EquipmentSlot.FEET);
    }

    public void update(EquipmentSlot slot) {
        ItemStack itemStack = null;

        if (owner instanceof Player pAttacker) {
            PlayerInventory pi = pAttacker.getInventory();
            itemStack = switch (slot) {
                case HAND -> pi.getItemInMainHand();
                case OFF_HAND -> pi.getItemInOffHand();
                case FEET -> pi.getBoots();
                case LEGS -> pi.getLeggings();
                case CHEST -> pi.getChestplate();
                case HEAD -> pi.getHelmet();
            };
        } else if (owner instanceof LivingEntity leAttacker) {
            EntityEquipment ee = leAttacker.getEquipment();
            if (ee != null) itemStack = switch (slot) {
                case HAND -> ee.getItemInMainHand();
                case OFF_HAND -> ee.getItemInOffHand();
                case FEET -> ee.getBoots();
                case LEGS -> ee.getLeggings();
                case CHEST -> ee.getChestplate();
                case HEAD -> ee.getHelmet();
            };
        }

        UCItemData ucItemData = itemStack == null ? null : getUCItemDataFromItemStack(itemStack);
        equipmentItemData.remove(slot);
        if (ucItemData != null)
            equipmentItemData.put(slot, ucItemData);
    }

    @Override
    public double getValueForStat(CombatStat stat) {
        double value = 0;
        for (UCItemData ucItemData : equipmentItemData.values())
            value += ucItemData.getValueForStat(stat);
        return value;
    }
}
