package com.tyrengard.unbound.combat.equipment;

import com.google.gson.Gson;
import com.tyrengard.aureycore.foundation.common.stringformat.ChatFormat;
import com.tyrengard.unbound.combat.damage.ResistanceSource;
import com.tyrengard.unbound.combat.damage.DamageSource;
import com.tyrengard.unbound.combat.stats.CombatStat;
import com.tyrengard.unbound.combat.stats.CombatStatHolder;
import org.bukkit.ChatColor;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public final class CombatItemData implements CombatStatHolder, DamageSource, ResistanceSource {
    public static final UCItemDataPersistentDataType PERSISTENT_DATA_TYPE = new UCItemDataPersistentDataType();
    private final EquipmentType type;
    private final HashMap<CombatStat, Double> combatStats;

    public CombatItemData(EquipmentType type, HashMap<CombatStat, Double> combatStats) {
        this.type = type;
        this.combatStats = combatStats;
    }

    public List<String> asLore() {
        List<String> lore = new ArrayList<>();
        lore.add(ChatFormat.color(ChatColor.WHITE, type.toItemLoreString()));
        lore.add("");
        lore.add(ChatFormat.underline(ChatColor.WHITE, "Stats:"));
        for (CombatStat stat : combatStats.keySet())
            lore.add(ChatFormat.color(ChatColor.WHITE, stat.toUCString() + ": " + combatStats.get(stat)));

        return lore;
    }

    /**
     * @return a {@code Map} of this item's {@code CombatStat}s and corresponding values
     */
    public Map<CombatStat, Double> getCombatStats() {
        return new HashMap<>(combatStats);
    }

    @Override
    public double getValueForStat(CombatStat stat) {
        return combatStats.getOrDefault(stat, 0.0);
    }

    @Override
    public void setValueForStat(CombatStat stat, double value) {
        combatStats.put(stat, value);
    }

    @Override
    public String getDamageSourceName() {
        return null;
    }

    @Override
    public int getDamageSourceOrder() {
        return 0;
    }

    public static final class UCItemDataPersistentDataType implements PersistentDataType<String, CombatItemData> {
        private final Gson gson = new Gson();

        @Override
        public @NotNull Class<String> getPrimitiveType() {
            return String.class;
        }

        @Override
        public @NotNull Class<CombatItemData> getComplexType() {
            return CombatItemData.class;
        }

        @Override
        public @NotNull String toPrimitive(@NotNull CombatItemData combatItemData, @NotNull PersistentDataAdapterContext persistentDataAdapterContext) {
            return gson.toJson(combatItemData);
        }

        @Override
        public @NotNull CombatItemData fromPrimitive(String s, @NotNull PersistentDataAdapterContext persistentDataAdapterContext) {
            return gson.fromJson(s, CombatItemData.class);
        }
    }
}
