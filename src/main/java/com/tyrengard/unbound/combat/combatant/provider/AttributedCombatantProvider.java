package com.tyrengard.unbound.combat.combatant.provider;

import com.tyrengard.unbound.combat.attributes.CombatAttribute;
import com.tyrengard.unbound.combat.combatant.AttributedCombatant;
import org.bukkit.entity.Entity;

import java.util.HashMap;

public interface AttributedCombatantProvider<E extends Entity, AC extends AttributedCombatant<E>> extends CombatantProvider<E, AC> {
    HashMap<CombatAttribute, Short> getBaseAttributes();

    @Override
    default AC applyTemplate(AC attributedCombatant) {
        AC ac = CombatantProvider.super.applyTemplate(attributedCombatant);

        final HashMap<CombatAttribute, Short> baseAttributes = getBaseAttributes();
        for (CombatAttribute attribute : baseAttributes.keySet())
            attribute.applyToCombatant(ac, baseAttributes.getOrDefault(attribute, (short) 0));

        return ac;
    }
}
