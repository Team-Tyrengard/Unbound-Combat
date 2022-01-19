package com.tyrengard.unbound.combat.managers;

import com.tyrengard.aureycore.foundation.common.armorevents.ArmorEquipEvent;
import com.tyrengard.aureycore.foundation.AManager;
import com.tyrengard.unbound.combat.CombatEngine;
import com.tyrengard.unbound.combat.UnboundCombat;
import com.tyrengard.unbound.combat.combatant.Combatant;
import com.tyrengard.unbound.combat.combatant.PlayerCombatant;
import com.tyrengard.unbound.combat.equipment.CombatItemData;
import com.tyrengard.unbound.combat.equipment.Equipment;
import com.tyrengard.unbound.combat.equipment.EquipmentHolder;
import com.tyrengard.unbound.combat.equipment.EquipmentType;
import com.tyrengard.unbound.combat.equipment.impl.DefaultUCItemDataFactory;
import com.tyrengard.unbound.combat.events.EquipmentChangeEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;

import java.util.List;
import java.util.Objects;

import static com.tyrengard.unbound.combat.CombatEngine.UC_COMBAT_ENGINE_KEY;

public class EquipmentManager extends AManager<UnboundCombat> implements Listener {
    private static final DefaultUCItemDataFactory defaultUCItemDataFactory = new DefaultUCItemDataFactory();
    private static EquipmentManager instance;

    public EquipmentManager(UnboundCombat plugin) {
        super(plugin);
        instance = this;
    }

    // region Manager overrides
    @Override
    protected void startup() {

    }

    @Override
    protected void cleanup() {

    }
    // endregion

    // region Event handlers
    @EventHandler(ignoreCancelled = true)
    private void onPlayerJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        logDebug("EquipmentManager: Setting new equipment for player " +p.getName());
        PlayerCombatant pc = (PlayerCombatant) CombatEngine.obtainCombatant(p);
        pc.setEquipment(new Equipment(p));

        // Prep items
        for (ItemStack itemStack : p.getInventory())
            if (itemShouldHaveUCItemData(itemStack) && !itemHasUCItemData(itemStack))
                attachCombatItemDataToItemStack(getDefaultCombatItemDataForItemStack(itemStack), itemStack);

        p.updateInventory();
    }

    @EventHandler(ignoreCancelled = true)
    private void onEntityPickupItem(EntityPickupItemEvent e) {
        ItemStack itemStack = e.getItem().getItemStack();

        if (itemShouldHaveUCItemData(itemStack) && !itemHasUCItemData(itemStack))
            attachCombatItemDataToItemStack(getDefaultCombatItemDataForItemStack(itemStack), itemStack);
    }

    // region Main hand and off hand
    @EventHandler(ignoreCancelled = true)
    private void onPlayerItemHeld(PlayerItemHeldEvent e) {
        Player p = e.getPlayer();
        ItemStack itemStack = p.getInventory().getItem(e.getNewSlot());

        EquipmentChangeEvent event = new EquipmentChangeEvent(p, EquipmentSlot.HAND, itemStack);
        Bukkit.getPluginManager().callEvent(event);

        e.setCancelled(event.isCancelled());
    }

    @EventHandler(ignoreCancelled = true)
    private void onPlayerSwapHandItems(PlayerSwapHandItemsEvent e) {
        Player p = e.getPlayer();

        EquipmentChangeEvent mainHandEvent = new EquipmentChangeEvent(p, EquipmentSlot.HAND, e.getMainHandItem()),
                                offHandEvent = new EquipmentChangeEvent(p, EquipmentSlot.OFF_HAND, e.getOffHandItem());

        Bukkit.getPluginManager().callEvent(mainHandEvent);
        if (!mainHandEvent.isCancelled())
            Bukkit.getPluginManager().callEvent(offHandEvent);

        e.setCancelled(mainHandEvent.isCancelled() || offHandEvent.isCancelled());
    }

    @EventHandler(ignoreCancelled = true)
    private void onInventoryClick(InventoryClickEvent e) {
        switch (e.getSlotType()) {
            case ARMOR, CONTAINER, QUICKBAR -> {
                Combatant<?> c = CombatEngine.obtainCombatant(e.getWhoClicked());

                if (c instanceof EquipmentHolder equipmentHolder)
                    equipmentHolder.getEquipment().updateAll(); // TODO: this is lazy and inefficient, fix this
            }
        }
    }
    // endregion

    @EventHandler(ignoreCancelled = true)
    private void onPlayerItemBreak(PlayerItemBreakEvent e) {
        PlayerCombatant pc = (PlayerCombatant) CombatEngine.obtainCombatant(e.getPlayer());
        pc.getEquipment().updateAll(); // TODO: this is lazy and inefficient, fix it
    }

    @EventHandler(ignoreCancelled = true)
    private void onPlayerArmorChange(ArmorEquipEvent e) {
        Player p = e.getPlayer();
        ItemStack itemStack = e.getNewArmorPiece();

        EquipmentChangeEvent event = new EquipmentChangeEvent(p, switch (e.getType()) {
            case HELMET -> EquipmentSlot.HEAD;
            case CHESTPLATE -> EquipmentSlot.CHEST;
            case LEGGINGS -> EquipmentSlot.LEGS;
            case BOOTS -> EquipmentSlot.FEET;
        }, itemStack);
        Bukkit.getPluginManager().callEvent(event);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private void onUCEquipmentChange(EquipmentChangeEvent e) {
        Combatant<?> c = CombatEngine.obtainCombatant(e.getEntity());
        if (c instanceof EquipmentHolder equipmentHolder) {
            ItemStack itemStack = e.getNewItem();

            if (itemStack != null && itemShouldHaveUCItemData(itemStack) && !itemHasUCItemData(itemStack))
                attachCombatItemDataToItemStack(getDefaultCombatItemDataForItemStack(itemStack), itemStack);

            equipmentHolder.getEquipment().update(e.getSlot(), itemStack);
        }
    }
    // endregion

    public static boolean itemShouldHaveUCItemData(ItemStack itemStack) {
        return EquipmentType.of(itemStack) != EquipmentType.NON_EQUIPMENT;
    }

    public static boolean itemHasUCItemData(ItemStack itemStack) {
        if (itemStack == null)
            return true;

        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null)
            return false;

        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        return pdc.has(UC_COMBAT_ENGINE_KEY, CombatItemData.PERSISTENT_DATA_TYPE);
    }

    public static CombatItemData getDefaultCombatItemDataForItemStack(ItemStack itemStack) {
        CombatItemData combatItemData = defaultUCItemDataFactory.from(itemStack);

        if (combatItemData == null)
            instance.logDebug("EquipmentManager: no default UCItemData generated for item " + itemStack.getType());

        return combatItemData;
    }

    public static void attachCombatItemDataToItemStack(CombatItemData combatItemData, ItemStack itemStack) {
        if (combatItemData != null) {
            ItemMeta meta = itemStack.getItemMeta();
            PersistentDataContainer pdc = Objects.requireNonNull(meta).getPersistentDataContainer();
            instance.logDebug("EquipmentManager: setting default UCItemData for item " + itemStack.getType());
            pdc.set(UC_COMBAT_ENGINE_KEY, CombatItemData.PERSISTENT_DATA_TYPE, combatItemData);
            List<String> lore = combatItemData.asLore();
            meta.setLore(lore);
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            itemStack.setItemMeta(meta);
        }
    }

    public static CombatItemData getCombatItemDataFromItemStack(ItemStack itemStack) {
        if (itemStack == null || itemStack.getType() == Material.AIR)
            return null;

        instance.logDebug("EquipmentManager: getting UCItemData from item " + itemStack.getType());

        ItemMeta meta = itemStack.getItemMeta();
        PersistentDataContainer pdc = Objects.requireNonNull(meta).getPersistentDataContainer();
        CombatItemData combatItemData = pdc.get(UC_COMBAT_ENGINE_KEY, CombatItemData.PERSISTENT_DATA_TYPE);
        if (combatItemData == null) {
            combatItemData = getDefaultCombatItemDataForItemStack(itemStack);
            attachCombatItemDataToItemStack(combatItemData, itemStack);
        }

        return combatItemData;
    }
}
