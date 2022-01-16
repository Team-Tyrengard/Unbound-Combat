package com.tyrengard.unbound.combat;

import com.tyrengard.aureycore.foundation.common.armorevents.ArmorEquipEvent;
import com.tyrengard.aureycore.foundation.AManager;
import com.tyrengard.unbound.combat.combatant.Combatant;
import com.tyrengard.unbound.combat.combatant.PlayerCombatant;
import com.tyrengard.unbound.combat.equipment.EquipmentType;
import com.tyrengard.unbound.combat.equipment.UCEquipment;
import com.tyrengard.unbound.combat.equipment.UCEquipmentHolder;
import com.tyrengard.unbound.combat.equipment.UCItemData;
import com.tyrengard.unbound.combat.equipment.impl.DefaultUCItemDataFactory;
import com.tyrengard.unbound.combat.events.UCEquipmentChangeEvent;
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

    EquipmentManager(UnboundCombat plugin) {
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
        PlayerCombatant pc = (PlayerCombatant) CombatEngine.getCombatant(p);
        pc.setEquipment(new UCEquipment(p));

        // Prep items
        for (ItemStack itemStack : p.getInventory())
            if (itemShouldHaveUCItemData(itemStack) && !itemHasUCItemData(itemStack))
                attachUCItemDataToItem(getDefaultUCItemDataForItem(itemStack), itemStack);

        p.updateInventory();
    }

    @EventHandler(ignoreCancelled = true)
    private void onEntityPickupItem(EntityPickupItemEvent e) {
        ItemStack itemStack = e.getItem().getItemStack();

        if (itemShouldHaveUCItemData(itemStack) && !itemHasUCItemData(itemStack))
            attachUCItemDataToItem(getDefaultUCItemDataForItem(itemStack), itemStack);
    }

    // region Main hand and off hand
    @EventHandler(ignoreCancelled = true)
    private void onPlayerItemHeld(PlayerItemHeldEvent e) {
        Player p = e.getPlayer();
        ItemStack itemStack = p.getInventory().getItem(e.getNewSlot());

        UCEquipmentChangeEvent event = new UCEquipmentChangeEvent(p, EquipmentSlot.HAND, itemStack);
        Bukkit.getPluginManager().callEvent(event);

        e.setCancelled(event.isCancelled());
    }

    @EventHandler(ignoreCancelled = true)
    private void onPlayerSwapHandItems(PlayerSwapHandItemsEvent e) {
        Player p = e.getPlayer();

        UCEquipmentChangeEvent  mainHandEvent = new UCEquipmentChangeEvent(p, EquipmentSlot.HAND, e.getMainHandItem()),
                                offHandEvent = new UCEquipmentChangeEvent(p, EquipmentSlot.OFF_HAND, e.getOffHandItem());

        Bukkit.getPluginManager().callEvent(mainHandEvent);
        if (!mainHandEvent.isCancelled())
            Bukkit.getPluginManager().callEvent(offHandEvent);

        e.setCancelled(mainHandEvent.isCancelled() || offHandEvent.isCancelled());
    }

    @EventHandler(ignoreCancelled = true)
    private void onInventoryClick(InventoryClickEvent e) {
        switch (e.getSlotType()) {
            case ARMOR, CONTAINER, QUICKBAR -> {
                Combatant<?> c = CombatEngine.getCombatant(e.getWhoClicked());

                if (c instanceof UCEquipmentHolder equipmentHolder)
                    equipmentHolder.getEquipment().updateAll(); // TODO: this is lazy and inefficient, fix this
            }
        }
    }
    // endregion

    @EventHandler(ignoreCancelled = true)
    private void onPlayerItemBreak(PlayerItemBreakEvent e) {
        PlayerCombatant pc = (PlayerCombatant) CombatEngine.getCombatant(e.getPlayer());
        pc.getEquipment().updateAll(); // TODO: this is lazy and inefficient, fix it
    }

    @EventHandler(ignoreCancelled = true)
    private void onPlayerArmorChange(ArmorEquipEvent e) {
        Player p = e.getPlayer();
        ItemStack itemStack = e.getNewArmorPiece();

        UCEquipmentChangeEvent event = new UCEquipmentChangeEvent(p, switch (e.getType()) {
            case HELMET -> EquipmentSlot.HEAD;
            case CHESTPLATE -> EquipmentSlot.CHEST;
            case LEGGINGS -> EquipmentSlot.LEGS;
            case BOOTS -> EquipmentSlot.FEET;
        }, itemStack);
        Bukkit.getPluginManager().callEvent(event);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private void onUCEquipmentChange(UCEquipmentChangeEvent e) {
        Combatant<?> c = CombatEngine.getCombatant(e.getEntity());
        if (c instanceof UCEquipmentHolder equipmentHolder) {
            ItemStack itemStack = e.getNewItem();

            if (itemStack != null && itemShouldHaveUCItemData(itemStack) && !itemHasUCItemData(itemStack))
                attachUCItemDataToItem(getDefaultUCItemDataForItem(itemStack), itemStack);

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
        return pdc.has(UC_COMBAT_ENGINE_KEY, UCItemData.PERSISTENT_DATA_TYPE);
    }

    public static UCItemData getDefaultUCItemDataForItem(ItemStack itemStack) {
        UCItemData ucItemData = defaultUCItemDataFactory.from(itemStack);

        if (ucItemData == null)
            instance.logDebug("EquipmentManager: no default UCItemData generated for item " + itemStack.getType());

        return ucItemData;
    }

    public static void attachUCItemDataToItem(UCItemData ucItemData, ItemStack itemStack) {
        if (ucItemData != null) {
            ItemMeta meta = itemStack.getItemMeta();
            PersistentDataContainer pdc = Objects.requireNonNull(meta).getPersistentDataContainer();
            instance.logDebug("EquipmentManager: setting default UCItemData for item " + itemStack.getType());
            pdc.set(UC_COMBAT_ENGINE_KEY, UCItemData.PERSISTENT_DATA_TYPE, ucItemData);
            List<String> lore = ucItemData.asLore();
            meta.setLore(lore);
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            itemStack.setItemMeta(meta);
        }
    }

    public static UCItemData getUCItemDataFromItemStack(ItemStack itemStack) {
        if (itemStack == null || itemStack.getType() == Material.AIR)
            return null;

        instance.logDebug("EquipmentManager: getting UCItemData from item " + itemStack.getType());

        ItemMeta meta = itemStack.getItemMeta();
        PersistentDataContainer pdc = Objects.requireNonNull(meta).getPersistentDataContainer();
        UCItemData ucItemData = pdc.get(UC_COMBAT_ENGINE_KEY, UCItemData.PERSISTENT_DATA_TYPE);
        if (ucItemData == null) {
            ucItemData = getDefaultUCItemDataForItem(itemStack);
            attachUCItemDataToItem(ucItemData, itemStack);
        }

        return ucItemData;
    }
}
