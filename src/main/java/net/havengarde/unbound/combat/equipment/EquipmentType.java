package net.havengarde.unbound.combat.equipment;

import org.bukkit.entity.Projectile;
import org.bukkit.inventory.ItemStack;

public enum EquipmentType {
    FIST, SWORD, AXE, SPEAR, BOW, CROSSBOW, SHIELD, // weapons
    HELMET, CHESTPLATE, LEGGINGS, BOOTS,            // armor
    NON_EQUIPMENT;

    public static EquipmentType of(ItemStack item) {
        if (item == null) return FIST;
        return switch (item.getType()) {
            case NETHERITE_AXE, DIAMOND_AXE, GOLDEN_AXE, IRON_AXE, STONE_AXE, WOODEN_AXE -> AXE;
            case NETHERITE_SWORD, DIAMOND_SWORD, GOLDEN_SWORD, IRON_SWORD, STONE_SWORD, WOODEN_SWORD -> SWORD;
            case TRIDENT -> SPEAR;
            case BOW -> BOW;
            case CROSSBOW -> CROSSBOW;
            case SHIELD -> SHIELD;
            case AIR -> FIST;
            case NETHERITE_HELMET, DIAMOND_HELMET, GOLDEN_HELMET, IRON_HELMET, LEATHER_HELMET -> HELMET;
            case NETHERITE_CHESTPLATE, DIAMOND_CHESTPLATE, GOLDEN_CHESTPLATE, IRON_CHESTPLATE, LEATHER_CHESTPLATE -> CHESTPLATE;
            case NETHERITE_LEGGINGS, DIAMOND_LEGGINGS, GOLDEN_LEGGINGS, IRON_LEGGINGS, LEATHER_LEGGINGS -> LEGGINGS;
            case NETHERITE_BOOTS, DIAMOND_BOOTS, GOLDEN_BOOTS, IRON_BOOTS, LEATHER_BOOTS -> BOOTS;
            default -> NON_EQUIPMENT;
        };
    }

    public static EquipmentType of(Projectile projectile) {
        return switch (projectile.getType()) {
            case TRIDENT -> SPEAR;
            default -> NON_EQUIPMENT;
        };
    }

    public String toItemLoreString() {
        return "Equipment Type: " + switch (this) {
            case FIST -> "Gauntlet";
            case SWORD -> "Sword";
            case AXE -> "Axe";
            case SPEAR -> "Spear";
            case BOW -> "Bow";
            case CROSSBOW -> "Crossbow";
            case SHIELD -> "Shield";
            case HELMET -> "Helmet";
            case CHESTPLATE -> "Chestplate";
            case LEGGINGS -> "Legging";
            case BOOTS -> "Boots";
            case NON_EQUIPMENT -> "UNSUPPORTED";
        };
    }

    @Override
    public String toString() {
        return switch (this) {
            case FIST -> "Fists/Gauntlets";
            case SWORD -> "Swords";
            case AXE -> "Axes";
            case SPEAR -> "Spears";
            case BOW -> "Bows";
            case CROSSBOW -> "Crossbows";
            case SHIELD -> "Shields";
            case HELMET -> "Helmets";
            case CHESTPLATE -> "Chestplates";
            case LEGGINGS -> "Leggings";
            case BOOTS -> "Boots";
            case NON_EQUIPMENT -> "UNSUPPORTED";
        };
    }
}
