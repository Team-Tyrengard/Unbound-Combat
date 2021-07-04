package net.havengarde.unbound.combat.equipment;

import com.google.gson.Gson;
import net.havengarde.unbound.combat.damage.DamageSource;
import net.havengarde.unbound.combat.stats.CombatStat;
import net.havengarde.unbound.combat.stats.CombatStatHolder;
import net.havengarde.unbound.combat.resistance.ResistanceSource;
import net.havengarde.unbound.combat.stats.StatModifier;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public final class UCItemData implements CombatStatHolder, DamageSource, ResistanceSource {
    public static final UCItemDataPersistentDataType PERSISTENT_DATA_TYPE = new UCItemDataPersistentDataType();
    private final EquipmentType type;
    private final Hashtable<CombatStat, Double> combatStats;

    public UCItemData(EquipmentType type, Hashtable<CombatStat, Double> combatStats) {
        this.type = type;
        this.combatStats = combatStats;
    }

    public List<Component> asLore() {
        List<Component> lore = new ArrayList<>();
        lore.add(Component.text(type.toItemLoreString(), NamedTextColor.WHITE));
        lore.add(Component.text(""));
        lore.add(Component.text("Stats:", NamedTextColor.WHITE).decorate(TextDecoration.UNDERLINED));
        for (CombatStat stat : combatStats.keySet())
            lore.add(Component.text(stat.toUCString() + ": " + combatStats.get(stat), NamedTextColor.WHITE));

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
        public @NotNull Class<String> getPrimitiveType() {
            return String.class;
        }

        @Override
        public @NotNull Class<UCItemData> getComplexType() {
            return UCItemData.class;
        }

        @Override
        public @NotNull String toPrimitive(@NotNull UCItemData ucItemData, @NotNull PersistentDataAdapterContext persistentDataAdapterContext) {
            return gson.toJson(ucItemData);
        }

        @Override
        public @NotNull UCItemData fromPrimitive(@NotNull String s, @NotNull PersistentDataAdapterContext persistentDataAdapterContext) {
            return gson.fromJson(s, UCItemData.class);
        }
    }
}
