package com.tyrengard.unbound.combat.combatant;

import com.tyrengard.unbound.combat.equipment.Equipment;
import com.tyrengard.unbound.combat.attributes.CombatAttribute;
import com.tyrengard.unbound.combat.equipment.EquipmentHolder;
import com.tyrengard.unbound.combat.log.CombatLog;
import com.tyrengard.unbound.combat.log.CombatLogHolder;
import com.tyrengard.unbound.combat.stats.CombatStat;
import com.tyrengard.unbound.combat.skills.CombatSkill;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Represents a {@link Player} in combat, with {@link CombatStat}s, {@link Equipment}, and {@link CombatAttribute}s.
 */
public abstract class PlayerCombatant extends AttributedCombatant<Player> implements EquipmentHolder, CombatLogHolder {
    protected final TreeSet<CombatLog> combatLogs = new TreeSet<>();
    protected transient Equipment equipment = null;

    protected PlayerCombatant(UUID id) {
        super(id);
    }

    @Override
    public Player getEntity() {
        return Bukkit.getPlayer(id);
    }

    @Override
    public void setEquipment(@NotNull Equipment equipment) {
        this.equipment = equipment;
    }

    @Override
    public @NotNull Equipment getEquipment() {
        if (equipment == null)
            setEquipment(new Equipment(getEntity()));
        return equipment;
    }

    @Override
    public void storeCombatLog(CombatLog combatLog) {
        while (combatLogs.size() >= 21)
            combatLogs.remove(combatLogs.first());
        combatLogs.add(combatLog);
    }

    @Override
    public Collection<CombatLog> getCombatLogs() {
        return combatLogs;
    }
}
