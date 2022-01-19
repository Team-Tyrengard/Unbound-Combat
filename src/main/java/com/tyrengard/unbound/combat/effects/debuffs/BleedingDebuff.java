package com.tyrengard.unbound.combat.effects.debuffs;

import com.tyrengard.unbound.combat.effects.CombatEffectType;
import com.tyrengard.unbound.combat.enums.CombatOutcome;
import com.tyrengard.unbound.combat.events.CombatDamageEvent;
import com.tyrengard.unbound.combat.effects.CombatEffect;
import org.bukkit.Bukkit;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;

import java.util.Objects;

public class BleedingDebuff extends CombatEffect {
    private final double dps;
    public BleedingDebuff(Entity source, double dps, int seconds) {
        super("Bleeding", source, CombatEffectType.DEBUFF, ExpiryBehavior.FULL, seconds, 1, 1);
        this.dps = dps;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        BleedingDebuff that = (BleedingDebuff) o;
        return dps == that.dps && super.equals(o);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), dps);
    }

    @Override
    public void onTick(Entity target) {
        if (target instanceof Damageable damageable)
            Bukkit.getPluginManager().callEvent(new CombatDamageEvent(damageable, dps, source, CombatOutcome.SUCCESS_EFFECT));
    }
}
