package com.tyrengard.unbound.combat.combatant;

import com.tyrengard.unbound.combat.equipment.Equipment;
import com.tyrengard.unbound.combat.equipment.EquipmentHolder;
import com.tyrengard.unbound.combat.stats.CombatStat;
import org.bukkit.entity.Mob;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Represents a {@link Mob} in combat, with {@link CombatStat}s and {@link Equipment}.<br />
 * All mobs in combat are represented by this class or a subclass of it.
 */
public abstract class MobCombatant extends Combatant<Mob> implements EquipmentHolder {
    protected transient Equipment equipment = null;
    protected MobCombatant(UUID id) {
        super(id);
    }

    @Override
    public void setEquipment(@NotNull Equipment equipment) {
        this.equipment = equipment;
    }

    @Override
    public @NotNull Equipment getEquipment() {
        if (equipment == null)
            equipment = new Equipment(getEntity());
        return equipment;
    }
}
