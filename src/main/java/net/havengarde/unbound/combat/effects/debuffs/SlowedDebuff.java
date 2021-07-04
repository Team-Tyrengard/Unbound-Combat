package net.havengarde.unbound.combat.effects.debuffs;

import net.havengarde.unbound.combat.effects.MoveSpeedChangeCombatEffect;
import org.bukkit.entity.Entity;

public class SlowedDebuff extends MoveSpeedChangeCombatEffect {
    public SlowedDebuff(Entity source, int seconds, double multiplier) {
        super("Slowed", source, ExpiryBehavior.FULL, seconds, multiplier);
    }
}
