package net.havengarde.unbound.combat.combatant;

import net.havengarde.unbound.combat.equipment.UCEquipment;
import net.havengarde.unbound.combat.equipment.UCEquipmentHolder;
import org.bukkit.entity.Mob;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public abstract class MobCombatant extends Combatant<Mob> implements UCEquipmentHolder {
    protected transient UCEquipment equipment = null;
    protected MobCombatant(UUID id) {
        super(id);
    }

    @Override
    public void setEquipment(UCEquipment equipment) {
        this.equipment = equipment;
    }

    @Override
    public @NotNull UCEquipment getEquipment() {
        if (equipment == null)
            equipment = new UCEquipment(getEntity());
        return equipment;
    }
}
