package com.tyrengard.unbound.combat.damageindicator;

import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;

import com.tyrengard.aureycore.foundation.common.stringformat.Color;
import com.tyrengard.aureycore.foundation.common.utils.TaskUtils;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.metadata.FixedMetadataValue;

import org.bukkit.plugin.Plugin;

public final class DamageIndicatorAPI implements Listener {
	private static final String METADATA_TITLE = "aureycore-damage-indicator";
	private static final byte METADATA_VALUE = 0;
	private static final double Y_DISTANCE = 0.25;
    private static final HashMap<ArmorStand, Long> armorStands = new HashMap<>();
	private static int indicatorTaskId;
	private static Plugin owningPlugin;
	private static DamageIndicatorAPI instance;
    private static boolean useDefaultDamageIndicators = true;
	private DamageIndicatorAPI() {}

    public static void start(Plugin plugin) {
        start(plugin, true);
    }

    public static void start(Plugin plugin, boolean useDefaultDamageIndicators) {
        if (instance != null) return;

        instance = new DamageIndicatorAPI();
        owningPlugin = plugin;
        DamageIndicatorAPI.useDefaultDamageIndicators = useDefaultDamageIndicators;
        plugin.getServer().getPluginManager().registerEvents(instance, owningPlugin);

        indicatorTaskId = TaskUtils.runTaskPeriodically(owningPlugin, 0, 1, () -> {
            Iterator<Map.Entry<ArmorStand, Long>> it = armorStands.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<ArmorStand, Long> ent = it.next();
                if (ent.getValue() + 1500 <= System.currentTimeMillis()) {
                    ent.getKey().remove();
                    it.remove();
                } else {
                    ent.getKey().teleport(ent.getKey().getLocation().clone().add(0.0, 0.07, 0.0));
                }
            }
        });

    }

    public static void stop() {
        TaskUtils.cancelTask(indicatorTaskId);
        armorStands.keySet().forEach(Entity::remove);
        EntitySpawnEvent.getHandlerList().unregister(instance);
        ChunkUnloadEvent.getHandlerList().unregister(instance);
        ChunkLoadEvent.getHandlerList().unregister(instance);
        EntityRegainHealthEvent.getHandlerList().unregister(instance);
        EntityDamageEvent.getHandlerList().unregister(instance);
        instance = null;
        owningPlugin = null;
    }

	public static void useDefaultDamageIndicators(boolean val) { useDefaultDamageIndicators = val; }
    public static void showIndicator(Location loc, double value) {
        showIndicator(new DamageIndicator(loc, value, null));
    }
	public static void showIndicator(Location loc, double value, Color customColor) {
		showIndicator(new DamageIndicator(loc, value, customColor));
	}
	public static void showIndicator(DamageIndicator indicator) {
	    Location loc = indicator.location;
	    World world = loc.getWorld();
	    if (world != null) {
            ArmorStand as = world.spawn(loc.clone().add(0, loc.getWorld().getMaxHeight() - loc.getY(), 0), ArmorStand.class, stand -> {
                stand.setVisible(false);
                stand.setGravity(false);
                stand.setMarker(true);
                stand.setSmall(true);
                stand.setMetadata(METADATA_TITLE, new FixedMetadataValue(owningPlugin, METADATA_VALUE));
                stand.setCollidable(false);
                stand.setInvulnerable(true);
                stand.teleport(loc.clone().add(0, Y_DISTANCE, 0));
                stand.setRemoveWhenFarAway(true);
            });
            as.setCustomName(indicator.label);
            as.setCustomNameVisible(true);
            armorStands.put(as, System.currentTimeMillis());
        }
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
    public void oneEntitySpawn(EntitySpawnEvent e) {
        if (e.isCancelled() && e.getEntity() instanceof ArmorStand)
            if (isIndicator(e.getEntity()))
                e.setCancelled(false);
    }
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onChunkUnload(ChunkUnloadEvent event) {
        for (Entity entity : event.getChunk().getEntities()) {
            if (entity.getType().equals(EntityType.ARMOR_STAND)) {
                ArmorStand as = (ArmorStand) entity;
                if (isIndicator(as)) {
                    armorStands.remove(as);
                    as.remove();
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onChunkLoad(ChunkLoadEvent event) {
        for (Entity entity : event.getChunk().getEntities()) {
            if (entity.getType().equals(EntityType.ARMOR_STAND)) {
                ArmorStand as = (ArmorStand) entity;
                if (isIndicator(as)) {
                    armorStands.remove(as);
                    as.remove();
                }
            }
        }
    }
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityRegainHealth(EntityRegainHealthEvent e) {
        if (!(e.getEntity() instanceof LivingEntity) || 
        		(e.getEntity() instanceof Player && ((Player) e.getEntity()).isSneaking()))
            return;
        showIndicator(e.getEntity().getLocation(), e.getAmount());
    }
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent e) {
        if (!useDefaultDamageIndicators || !(e.getEntity() instanceof LivingEntity))
            return;
        showIndicator(e.getEntity().getLocation(), e.getFinalDamage());
    }
    
    private static boolean isIndicator(Entity entity) {
		if (!(entity instanceof ArmorStand as)) return false;

        return as.hasMetadata(METADATA_TITLE);
	}
}
