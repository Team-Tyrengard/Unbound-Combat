package com.tyrengard.unbound.combat.equipment;

import com.google.gson.Gson;
import com.tyrengard.aureycore.foundation.common.stringformat.ChatFormat;
import com.tyrengard.unbound.combat.resistance.ResistanceSource;
import com.tyrengard.unbound.combat.damage.DamageSource;
import com.tyrengard.unbound.combat.stats.CombatStat;
import com.tyrengard.unbound.combat.stats.CombatStatHolder;
import org.bukkit.ChatColor;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

public final class UCItemData implements CombatStatHolder, DamageSource, ResistanceSource {
    public static final UCItemDataPersistentDataType PERSISTENT_DATA_TYPE = new UCItemDataPersistentDataType();
    private final EquipmentType type;
    private final Hashtable<CombatStat, Double> combatStats;

    public UCItemData(EquipmentType type, Hashtable<CombatStat, Double> combatStats) {
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
     * @return an immutable copy of this item's combat stats
     */
    public Hashtable<CombatStat, Double> getCombatStats() {
        return new Hashtable<>(combatStats);
    }

    @Override
    public double getValueForStat(CombatStat stat) {
        return combatStats.getOrDefault(stat, 0.0);
    }

    @Override
    public void setValueForStat(CombatStat stat, double value) {
        combatStats.put(stat, value);
    }

    public static final class UCItemDataPersistentDataType implements PersistentDataType<String, UCItemData> {
        private final Gson gson = new Gson();

        @Override
        public Class<String> getPrimitiveType() {
            return String.class;
        }

        @Override
        public Class<UCItemData> getComplexType() {
            return UCItemData.class;
        }

        @Override
        public String toPrimitive(UCItemData ucItemData, PersistentDataAdapterContext persistentDataAdapterContext) {
            return gson.toJson(ucItemData);
        }

        @Override
        public UCItemData fromPrimitive(String s, PersistentDataAdapterContext persistentDataAdapterContext) {
            return gson.fromJson(s, UCItemData.class);
        }
    }
}
