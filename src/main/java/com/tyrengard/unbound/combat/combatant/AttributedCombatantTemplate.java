package com.tyrengard.unbound.combat.combatant;

import com.tyrengard.unbound.combat.enums.CombatAttribute;
import org.bukkit.entity.Entity;

import java.util.HashMap;

public interface AttributedCombatantTemplate<E extends Entity, AC extends AttributedCombatant<E>> extends CombatantTemplate<E, AC> {
    HashMap<CombatAttribute, Short> getBaseAttributes();

    @Override
    default AC applyTemplate(AC attributedCombatant) {
        AC ac = CombatantTemplate.super.applyTemplate(attributedCombatant);

        final HashMap<CombatAttribute, Short> baseAttributes = getBaseAttributes();
        for (CombatAttribute attribute : baseAttributes.keySet())
            attribute.applyToCombatant(ac, baseAttributes.getOrDefault(attribute, (short) 0));

        return ac;
    }
}
