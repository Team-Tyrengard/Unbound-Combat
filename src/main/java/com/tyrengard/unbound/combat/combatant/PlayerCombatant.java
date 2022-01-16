package com.tyrengard.unbound.combat.combatant;

import com.tyrengard.unbound.combat.equipment.UCEquipment;
import com.tyrengard.unbound.combat.equipment.UCEquipmentHolder;
import com.tyrengard.unbound.combat.log.CombatLog;
import com.tyrengard.unbound.combat.log.CombatLogHolder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public abstract class PlayerCombatant extends AttributedCombatant<Player> implements UCEquipmentHolder, CombatLogHolder {
    protected final TreeSet<CombatLog> combatLogs = new TreeSet<>();
    protected transient UCEquipment equipment = null;
    protected PlayerCombatant(UUID id) {
        super(id);
    }

    @Override
    public Player getEntity() {
        return Bukkit.getPlayer(id);
    }

    @Override
    public void setEquipment(UCEquipment equipment) {
        this.equipment = equipment;
    }

    @Override
    public @NotNull UCEquipment getEquipment() {
        if (equipment == null)
            setEquipment(new UCEquipment(getEntity()));
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
