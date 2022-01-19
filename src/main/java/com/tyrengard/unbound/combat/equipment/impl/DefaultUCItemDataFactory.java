package com.tyrengard.unbound.combat.equipment.impl;

import com.tyrengard.unbound.combat.equipment.EquipmentType;
import com.tyrengard.unbound.combat.equipment.CombatItemData;
import com.tyrengard.unbound.combat.equipment.UCItemDataFactory;
import com.tyrengard.unbound.combat.stats.CombatStat;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

public class DefaultUCItemDataFactory implements UCItemDataFactory {
    @Override
    public CombatItemData from(ItemStack itemStack) {
        if (itemStack == null)
            return null;

        EquipmentType type = EquipmentType.of(itemStack);
        if (type == EquipmentType.NON_EQUIPMENT)
            return null;

        Material m = itemStack.getType();
        HashMap<CombatStat, Double> combatStats = new HashMap<>();
        addValueForStat(combatStats, CombatStat.MELEE_DAMAGE, switch (m) {
            case WOODEN_SWORD, GOLDEN_SWORD -> 4.0;
            case STONE_SWORD, WOODEN_AXE, GOLDEN_AXE -> 5.0;
            case IRON_SWORD -> 6.0;
            case DIAMOND_SWORD, STONE_AXE, IRON_AXE -> 7.0;
            case NETHERITE_SWORD -> 8.0;
            case TRIDENT, DIAMOND_AXE, NETHERITE_AXE -> 9.0;
//            case WOODEN_SHOVEL, GOLDEN_SHOVEL -> 2.5;
//            case STONE_SHOVEL -> 3.5;
//            case IRON_SHOVEL -> 4.5;
//            case DIAMOND_SHOVEL -> 5.5;
//            case NETHERITE_SHOVEL -> 6.5;
//            case WOODEN_PICKAXE, GOLDEN_PICKAXE -> 2.0;
//            case STONE_PICKAXE -> 3.0;
//            case IRON_PICKAXE -> 4.0;
//            case DIAMOND_PICKAXE -> 5.0;
//            case NETHERITE_PICKAXE -> 6.0;
            default -> null;
        });
        addValueForStat(combatStats, CombatStat.RANGED_DAMAGE, switch (m) {
            case BOW -> 4.0;
            case CROSSBOW -> 7.5;
            case TRIDENT -> 9.0;
            default -> null;
        });
        addValueForStat(combatStats, CombatStat.PHYSICAL_RESISTANCE, switch (m) {
            case LEATHER_HELMET -> 4.0;
            case LEATHER_CHESTPLATE -> 12.0;
            case LEATHER_LEGGINGS -> 11.0;
            case LEATHER_BOOTS -> 3.0;

            case GOLDEN_HELMET -> 9.0;
            case GOLDEN_CHESTPLATE -> 24.0;
            case GOLDEN_LEGGINGS -> 21.0;
            case GOLDEN_BOOTS -> 6.0;

            case CHAINMAIL_HELMET -> 12.0;
            case CHAINMAIL_CHESTPLATE -> 30.0;
            case CHAINMAIL_LEGGINGS -> 26.0;
            case CHAINMAIL_BOOTS -> 7.0;

            case IRON_HELMET -> 15.0;
            case IRON_CHESTPLATE -> 40.0;
            case IRON_LEGGINGS -> 35.0;
            case IRON_BOOTS -> 10.0;

            case DIAMOND_HELMET -> 25.0;
            case DIAMOND_CHESTPLATE -> 70.0;
            case DIAMOND_LEGGINGS -> 62.0;
            case DIAMOND_BOOTS -> 18.0;

            case NETHERITE_HELMET -> 34.0;
            case NETHERITE_CHESTPLATE -> 90.0;
            case NETHERITE_LEGGINGS -> 79.0;
            case NETHERITE_BOOTS -> 22.0;

            case TURTLE_HELMET -> 15.0;
            default -> null;
        });

        if (combatStats.isEmpty())
            return null;
        else {
            for (Enchantment enchantment : itemStack.getEnchantments().keySet()) {
                int level = itemStack.getEnchantmentLevel(enchantment);
                addStatValueForVanillaEnchantment(combatStats, enchantment, level);
            }
            return new CombatItemData(type, combatStats);
        }
    }

    private void addValueForStat(Map<CombatStat, Double> combatStats, CombatStat stat, Double value) {
        if (value != null)
            combatStats.put(stat, value);
    }

    private void addStatValueForVanillaEnchantment(Map<CombatStat, Double> combatStats, Enchantment enchantment, int level) {
        if (Enchantment.DAMAGE_ALL.equals(enchantment)) {
            double meleeDamage = (0.5 * level) + 0.5;
            combatStats.compute(CombatStat.MELEE_DAMAGE, (k, v) -> v == null ? meleeDamage : v + meleeDamage);
        } else if (Enchantment.ARROW_DAMAGE.equals(enchantment)) {
            double rangedDamage = level + 1;
            combatStats.compute(CombatStat.RANGED_DAMAGE, (k, v) -> v == null ? rangedDamage : v + rangedDamage);
        } else if (Enchantment.PROTECTION_ENVIRONMENTAL.equals(enchantment)) {
            double physicalResistanceMultiplier = level * 0.05; // prot 4 = 0.2 or 20% extra PRes
            combatStats.compute(CombatStat.PHYSICAL_RESISTANCE, (k, v) -> v == null ? 0 : v * physicalResistanceMultiplier);
        }
    }
}
