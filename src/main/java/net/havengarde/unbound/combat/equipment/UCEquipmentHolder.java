package net.havengarde.unbound.combat.equipment;

import org.jetbrains.annotations.NotNull;

public interface UCEquipmentHolder {
    void setEquipment(UCEquipment equipment);
    @NotNull UCEquipment getEquipment();
}
