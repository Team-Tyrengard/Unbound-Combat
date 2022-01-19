package com.tyrengard.unbound.combat.equipment;

import org.jetbrains.annotations.NotNull;

public interface EquipmentHolder {
    void setEquipment(@NotNull Equipment equipment);
    @NotNull Equipment getEquipment();
}
