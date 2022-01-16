package com.tyrengard.unbound.combat.enums;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public enum ItemType {
	MISC,
	NONE, // Fist
	HELMET, CHESTPLATE, LEGGINGS, BOOTS, ARMOR, // Armor
	PICKAXE, AXE, SHOVEL, HOE, FISHING_ROD, SHEARS, TOOLS, // Tools
	SWORD, SPEAR, BOW, CROSSBOW, SHIELD; // Weapons
	
	@Override
	public String toString() {
		switch (this) {
		case NONE: return "Bare Hands";
		
		//Armor
		case HELMET: return "Helmets";
		case CHESTPLATE: return "Chestplates";
		case LEGGINGS: return "Leggings";
		case BOOTS: return "Boots";
		case ARMOR: return "Armor";
		
		//Tools
		case PICKAXE: return "Pickaxes";
		case AXE: return "Axes";
		case SHOVEL: return "Shovels";
		case HOE: return "Hoes";
		case FISHING_ROD: return "Fishing Rods";
		case SHEARS: return "Shears";
		case TOOLS: return "Tools";
		
		//Weapons (also includes pickaxes and axes)
		case SWORD: return "Swords";
		case SPEAR: return "Spears";
		case BOW: return "Bows";
		case CROSSBOW: return "Crossbows";
		case SHIELD: return "Shields";
		case MISC: return "Items";
		
		default: return null;
		}
	}
	
	public final static ItemType fromItem(ItemStack item) {
		if (item == null) return NONE;
		switch (item.getType()) {
		case CHAINMAIL_HELMET: case DIAMOND_HELMET: case GOLDEN_HELMET: case IRON_HELMET: case LEATHER_HELMET: case TURTLE_HELMET:
			return HELMET;
		case CHAINMAIL_CHESTPLATE: case DIAMOND_CHESTPLATE: case GOLDEN_CHESTPLATE: case IRON_CHESTPLATE: case LEATHER_CHESTPLATE:
			return CHESTPLATE;
		case CHAINMAIL_LEGGINGS: case DIAMOND_LEGGINGS: case GOLDEN_LEGGINGS: case IRON_LEGGINGS: case LEATHER_LEGGINGS:
			return LEGGINGS;
		case CHAINMAIL_BOOTS: case DIAMOND_BOOTS: case GOLDEN_BOOTS: case IRON_BOOTS: case LEATHER_BOOTS:
			return BOOTS;
		case DIAMOND_PICKAXE: case GOLDEN_PICKAXE: case IRON_PICKAXE: case STONE_PICKAXE: case WOODEN_PICKAXE:
			return PICKAXE;
		case DIAMOND_AXE: case GOLDEN_AXE: case IRON_AXE: case STONE_AXE: case WOODEN_AXE:
			return AXE;
		case DIAMOND_SHOVEL: case GOLDEN_SHOVEL: case IRON_SHOVEL: case STONE_SHOVEL: case WOODEN_SHOVEL:
			return SHOVEL;
		case DIAMOND_HOE: case GOLDEN_HOE: case IRON_HOE: case STONE_HOE: case WOODEN_HOE:
			return HOE;
		case FISHING_ROD:
			return FISHING_ROD;
		case SHEARS:
			return SHEARS;
		case DIAMOND_SWORD: case GOLDEN_SWORD: case IRON_SWORD: case STONE_SWORD: case WOODEN_SWORD:
			return SWORD;
		case TRIDENT:
			return SPEAR;
		case BOW:
			return BOW;
		case CROSSBOW:
			return CROSSBOW;
		case SHIELD:
			return SHIELD;
		case AIR:
			return NONE;
		default: return MISC;
		}
	}
	
	public final Material[] toMaterials() {
		ArrayList<Material> l;
		switch (this) {
		case HELMET: return new Material[] { Material.CHAINMAIL_HELMET, Material.DIAMOND_HELMET, Material.GOLDEN_HELMET, 
											 Material.IRON_HELMET, Material.LEATHER_HELMET, Material.TURTLE_HELMET };
		case CHESTPLATE: return new Material[] { Material.CHAINMAIL_CHESTPLATE, Material.DIAMOND_CHESTPLATE, 
												 Material.GOLDEN_CHESTPLATE, Material.IRON_CHESTPLATE, 
												 Material.LEATHER_CHESTPLATE };
		case LEGGINGS: return new Material[] { Material.CHAINMAIL_LEGGINGS, Material.DIAMOND_LEGGINGS, Material.GOLDEN_LEGGINGS, 
											   Material.IRON_LEGGINGS, Material.LEATHER_LEGGINGS };
		case BOOTS: return new Material[] { Material.CHAINMAIL_BOOTS, Material.DIAMOND_BOOTS, Material.GOLDEN_BOOTS, 
				   							Material.IRON_BOOTS, Material.LEATHER_BOOTS };
		case ARMOR: 
			l = new ArrayList<>();
			Collections.addAll(l, ItemType.HELMET.toMaterials());
			Collections.addAll(l, ItemType.CHESTPLATE.toMaterials());
			Collections.addAll(l, ItemType.LEGGINGS.toMaterials());
			Collections.addAll(l, ItemType.BOOTS.toMaterials());
			return l.toArray(new Material[l.size()]);
		case PICKAXE: return new Material[] { Material.DIAMOND_PICKAXE, Material.GOLDEN_PICKAXE, Material.IRON_PICKAXE, 
											  Material.STONE_PICKAXE, Material.WOODEN_PICKAXE };
		case AXE: return new Material[] { Material.DIAMOND_AXE, Material.GOLDEN_AXE, Material.IRON_AXE, Material.STONE_AXE, 
										  Material.WOODEN_AXE };
		case SHOVEL: return new Material[] { Material.DIAMOND_SHOVEL, Material.GOLDEN_SHOVEL, Material.IRON_SHOVEL,
											 Material.STONE_SHOVEL, Material.WOODEN_SHOVEL };
		case HOE: return new Material[] { Material.DIAMOND_HOE, Material.GOLDEN_HOE, Material.IRON_HOE, Material.STONE_HOE, 
										  Material.WOODEN_HOE };
		case FISHING_ROD: return new Material[] { Material.FISHING_ROD };
		case SHEARS: return new Material[] { Material.SHEARS };
		case TOOLS:
			l = new ArrayList<>();
			Collections.addAll(l, ItemType.PICKAXE.toMaterials());
			Collections.addAll(l, ItemType.AXE.toMaterials());
			Collections.addAll(l, ItemType.SHOVEL.toMaterials());
			Collections.addAll(l, ItemType.HOE.toMaterials());
			Collections.addAll(l, ItemType.FISHING_ROD.toMaterials());
			Collections.addAll(l, ItemType.SHEARS.toMaterials());
			return l.toArray(new Material[l.size()]);
		case SWORD: return new Material[] { Material.DIAMOND_SWORD, Material.GOLDEN_SWORD, Material.IRON_SWORD, 
											Material.STONE_SWORD, Material.WOODEN_SWORD };
		case SPEAR: return new Material[] { Material.TRIDENT };
		case BOW: return new Material[] { Material.BOW };
		case CROSSBOW: return new Material[] { Material.CROSSBOW };
		case SHIELD: return new Material[] { Material.SHIELD };
		default: return new Material[0];
		}
	}
	
	public final Material[] breakableMaterials() {
		switch(this) {
		case PICKAXE:
			return new Material[] {
					Material.STONE, Material.COAL_ORE, Material.DIAMOND_ORE, Material.EMERALD_ORE, Material.GOLD_ORE,
					Material.IRON_ORE, Material.LAPIS_ORE, Material.ANDESITE, Material.GRANITE,
					Material.DIORITE
			};
		case SHOVEL:
			return new Material[] {
					Material.CLAY, Material.DIRT, Material.COARSE_DIRT, Material.FARMLAND, Material.GRASS_BLOCK,
					Material.DIRT_PATH, Material.GRAVEL, Material.MYCELIUM, Material.PODZOL, Material.RED_SAND,
					Material.SAND, Material.SOUL_SAND, Material.BLACK_CONCRETE_POWDER, Material.BLUE_CONCRETE_POWDER,
					Material.BROWN_CONCRETE_POWDER, Material.CYAN_CONCRETE_POWDER, Material.GRAY_CONCRETE_POWDER,
					Material.GREEN_CONCRETE_POWDER, Material.LIGHT_BLUE_CONCRETE_POWDER, Material.LIGHT_GRAY_CONCRETE_POWDER,
					Material.LIME_CONCRETE_POWDER, Material.MAGENTA_CONCRETE_POWDER, Material.ORANGE_CONCRETE_POWDER,
					Material.PINK_CONCRETE_POWDER, Material.PURPLE_CONCRETE_POWDER, Material.RED_CONCRETE_POWDER,
					Material.WHITE_CONCRETE_POWDER, Material.YELLOW_CONCRETE_POWDER
			};
		default: return new Material[0];
		}
	}
	
	public final static List<Material> allMaterials() {
		return Arrays.stream(values()).flatMap(it -> Arrays.stream(it.toMaterials())).collect(Collectors.toList());
	}
	
	public final static String asText(ItemType[] types) {
		if (types.length == 0)
			return "None";
		else if (types.length == values().length)
			return "All";
		else
			return String.join(", ", Arrays.asList(types).stream().map(ItemType::toString).toArray(String[]::new));
	}

	public final boolean launchesProjectiles() {
		switch(this) {
		case BOW: case CROSSBOW: case SPEAR: return true;
		default: return false;
		}
	}
}

