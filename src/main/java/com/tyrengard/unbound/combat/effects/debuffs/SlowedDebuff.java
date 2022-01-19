package com.tyrengard.unbound.combat.effects.debuffs;

import com.tyrengard.unbound.combat.effects.CombatEffectType;
import com.tyrengard.unbound.combat.effects.misc.MoveSpeedChangeCombatEffect;
import org.bukkit.entity.Entity;

public class SlowedDebuff extends MoveSpeedChangeCombatEffect {
    public SlowedDebuff(Entity source, int seconds, double multiplier) {
        super("Slowed", source, CombatEffectType.DEBUFF, ExpiryBehavior.FULL, seconds, multiplier);
    }
}
