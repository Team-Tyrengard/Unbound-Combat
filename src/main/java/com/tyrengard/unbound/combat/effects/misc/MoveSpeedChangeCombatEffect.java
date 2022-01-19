package com.tyrengard.unbound.combat.effects.misc;

import com.tyrengard.unbound.combat.effects.CombatEffect;
import com.tyrengard.unbound.combat.effects.CombatEffectType;
import org.bukkit.attribute.Attributable;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Entity;

public abstract class MoveSpeedChangeCombatEffect extends CombatEffect {
    private final AttributeModifier movementSpeedModifier;

    protected MoveSpeedChangeCombatEffect(String name, Entity source, CombatEffectType type, ExpiryBehavior expiryBehavior, int seconds,
                                          double multiplier) {
        super(name, source, type, expiryBehavior, seconds, 1, 1);
        this.movementSpeedModifier = new AttributeModifier("UC_MSCCE_" + name, multiplier - 1, AttributeModifier.Operation.MULTIPLY_SCALAR_1);
    }

    @Override
    public void onExpire(Entity target) {
        if (target instanceof Attributable a) {
            AttributeInstance msInstance = a.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED);
            if (msInstance != null)
                msInstance.removeModifier(movementSpeedModifier);
        }
    }

    @Override
    public void applyToEntity(Entity target) {
        if (target instanceof Attributable a) {
            AttributeInstance msInstance = a.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED);
            if (msInstance != null)
                msInstance.addModifier(movementSpeedModifier);
        }
    }
}
