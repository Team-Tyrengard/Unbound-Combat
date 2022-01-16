package com.tyrengard.unbound.combat.effects.debuffs;

import com.tyrengard.unbound.combat.effects.MoveSpeedChangeCombatEffect;
import org.bukkit.entity.Entity;

public class SlowedDebuff extends MoveSpeedChangeCombatEffect {
    public SlowedDebuff(Entity source, int seconds, double multiplier) {
        super("Slowed", source, ExpiryBehavior.FULL, seconds, multiplier);
    }
}
