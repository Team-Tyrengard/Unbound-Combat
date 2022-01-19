package com.tyrengard.unbound.combat.damageindicator;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.Serializer;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.Registry;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.WrappedDataWatcherObject;
import com.tyrengard.aureycore.foundation.common.stringformat.Color;
import com.tyrengard.aureycore.foundation.common.utils.TaskUtils;
import org.bukkit.Bukkit;
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
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.ProtocolLibrary;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public final class DamageIndicatorAPI implements Listener {
	private static final String METADATA_TITLE = "damage-indicator";
	private static final byte METADATA_VALUE = 0;
	private static final double Y_DISTANCE = 0.25;
    private static final HashMap<ArmorStand, Long> armorStands = new HashMap<>();
	private static int indicatorTaskId;
	private static Plugin owningPlugin;
	private static DamageIndicatorAPI instance;
    private static boolean useDefaultDamageIndicators = true;
	private DamageIndicatorAPI() {}

    private static ProtocolManager protocolManager;

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

        byteSerializer = Registry.get(Byte.class);
        chatSerializer = Registry.getChatComponentSerializer(true);
        booleanSerializer = Registry.get(Boolean.class);

        protocolManager = ProtocolLibrary.getProtocolManager();
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

    private static Serializer byteSerializer, chatSerializer, booleanSerializer;

    public static void showIndicator(Player player, String text, Location location, Vector velocity, boolean gravity, double fallHeight) {
        Bukkit.getScheduler().runTaskAsynchronously(owningPlugin, () -> {
            int entityId = getNextEntityId();
            UUID uuid = UUID.randomUUID();
            Location originalLocation = location.clone();

            PacketContainer spawnEntityPacket = protocolManager.createPacket(PacketType.Play.Server.SPAWN_ENTITY_LIVING);
            spawnEntityPacket.getIntegers().write(0, entityId);
            if (spawnEntityPacket.getUUIDs().size() > 0) {
                spawnEntityPacket.getUUIDs().write(0, uuid);
            }
            spawnEntityPacket.getDoubles().write(0, location.getX());
            spawnEntityPacket.getDoubles().write(1, location.getY());
            spawnEntityPacket.getDoubles().write(2, location.getZ());

            // Velocity
            spawnEntityPacket.getIntegers().write(2, (int) (velocity.getX() * 8000));
            spawnEntityPacket.getIntegers().write(3, (int) (velocity.getY() * 8000));
            spawnEntityPacket.getIntegers().write(4, (int) (velocity.getZ() * 8000));

            // Rotation
            spawnEntityPacket.getBytes().write(0, (byte) 0); // Yaw
            spawnEntityPacket.getBytes().write(1, (byte) 0); // Pitch
            spawnEntityPacket.getBytes().write(2, (byte) 0); // Head pitch

            PacketContainer entityMetadataPacket = protocolManager.createPacket(PacketType.Play.Server.ENTITY_METADATA);
            entityMetadataPacket.getIntegers().write(0, entityId);
            WrappedDataWatcher watcher = new WrappedDataWatcher();

            byte bitmask = 0x20;
            watcher.setObject(new WrappedDataWatcherObject(0, byteSerializer), bitmask);

            watcher.setObject(new WrappedDataWatcherObject(2, chatSerializer),
                    Optional.of(WrappedChatComponent.fromText(text)));

            watcher.setObject(new WrappedDataWatcherObject(3, booleanSerializer), true);
            watcher.setObject(new WrappedDataWatcherObject(4, booleanSerializer), true);
            watcher.setObject(new WrappedDataWatcherObject(5, booleanSerializer), !gravity);

//            if (entityNameComponent != null && !entityNameComponent.equals(Component.empty())) {
//                if (HoloMobHealth.version.isOld()) {
//                    watcher.setObject(2, LegacyComponentSerializer.legacySection().serialize(LanguageUtils.convert(entityNameComponent, HoloMobHealth.language)));
//                } else if (HoloMobHealth.version.isLegacy()) {
//                    WrappedDataWatcherObject object = new WrappedDataWatcherObject(2, stringSerializer);
//                    watcher.setObject(object, LegacyComponentSerializer.legacySection().serialize(LanguageUtils.convert(entityNameComponent, HoloMobHealth.language)));
//                } else {
//                    Optional<?> opt = Optional.of(WrappedChatComponent.fromJson(GsonComponentSerializer.gson().serialize(entityNameComponent)).getHandle());
//                    watcher.setObject(new WrappedDataWatcherObject(2, optChatSerializer), opt);
//                }
//            } else {
//                if (HoloMobHealth.version.isOld()) {
//                    watcher.setObject(2, "");
//                } else if (HoloMobHealth.version.isLegacy()) {
//                    WrappedDataWatcherObject object = new WrappedDataWatcherObject(2, stringSerializer);
//                    watcher.setObject(object, "");
//                } else {
//                    Optional<?> opt = Optional.empty();
//                    watcher.setObject(new WrappedDataWatcherObject(2, optChatSerializer), opt);
//                }
//            }

            byte standbitmask = (byte) 0x01 | 0x08 | 0x10;
            watcher.setObject(new WrappedDataWatcherObject(15, byteSerializer), standbitmask); // for 1.17 and up

//            switch (metaversion) {
//                case 0:
//                case 1:
//                    watcher.setObject(new WrappedDataWatcherObject(11, byteSerializer), standbitmask);
//                    break;
//                case 2:
//                    watcher.setObject(new WrappedDataWatcherObject(13, byteSerializer), standbitmask);
//                    break;
//                case 3:
//                    watcher.setObject(new WrappedDataWatcherObject(14, byteSerializer), standbitmask);
//                    break;
//                case 4:
//                    watcher.setObject(new WrappedDataWatcherObject(15, byteSerializer), standbitmask);
//                    break;
//            }

            entityMetadataPacket.getWatchableCollectionModifier().write(0, watcher.getWatchableObjects());

//            int range = HoloMobHealth.damageIndicatorVisibleRange;
//            List<Player> players = location.getWorld().getPlayers().stream().filter(each -> {
//                Location loc = each.getLocation();
//                return loc.getWorld().equals(location.getWorld()) && loc.distance(location) <= range * range && HoloMobHealth.playersEnabled.contains(each);
//            }).collect(Collectors.toList());
            Bukkit.getScheduler().runTask(owningPlugin, () -> {
                try {
                    protocolManager.sendServerPacket(player, spawnEntityPacket);
                    protocolManager.sendServerPacket(player, entityMetadataPacket);
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            });

            Vector downwardAccel = new Vector(0, -0.05, 0);
            new BukkitRunnable() {
                int tick = 0;
                @Override
                public void run() {
                    tick++;
                    if (!velocity.equals(new Vector(0, 0, 0)) &&
                            tick < 5 && originalLocation.getY() - location.getY() < fallHeight) {
                        Vector drag = velocity.clone().normalize().multiply(-0.03);
                        if (gravity) {
                            velocity.add(downwardAccel);
                        }
                        velocity.add(drag);
                        location.add(velocity);

                        PacketContainer entityTeleportPacket = protocolManager.createPacket(PacketType.Play.Server.ENTITY_TELEPORT);
                        entityTeleportPacket.getIntegers().write(0, entityId);
                        entityTeleportPacket.getDoubles().write(0, location.getX());
                        entityTeleportPacket.getDoubles().write(1, location.getY());
                        entityTeleportPacket.getDoubles().write(2, location.getZ());
                        entityTeleportPacket.getBytes().write(0, (byte) 0);
                        entityTeleportPacket.getBytes().write(1, (byte) 0);

                        Bukkit.getScheduler().runTask(owningPlugin, () -> {
                            try {
                                protocolManager.sendServerPacket(player, entityTeleportPacket);
                            } catch (InvocationTargetException e) {
                                e.printStackTrace();
                            }
                        });
                    } else if (tick >= 5) {
                        this.cancel();
                        PacketContainer packet = protocolManager.createPacket(PacketType.Play.Server.ENTITY_DESTROY);
                        packet.getIntegers().write(0, entityId);

                        Bukkit.getScheduler().runTaskLater(owningPlugin, () -> {
                            try {
                                protocolManager.sendServerPacket(player, packet);
                            } catch (InvocationTargetException e) {
                                e.printStackTrace();
                            }
                        }, 3);
                    }
                }
            }.runTaskTimerAsynchronously(owningPlugin, 0, 1);
        });
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

	private static int getNextEntityId() {
        return Integer.MAX_VALUE;
    }
}
