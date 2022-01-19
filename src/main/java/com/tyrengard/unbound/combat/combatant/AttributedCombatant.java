package com.tyrengard.unbound.combat.combatant;

import com.tyrengard.unbound.combat.attributes.CombatAttribute;
import com.tyrengard.unbound.combat.attributes.CombatAttributeHolder;
import com.tyrengard.unbound.combat.skills.CombatSkilled;
import org.bukkit.entity.Entity;

import java.util.HashMap;
import java.util.UUID;

/**
 * Represents any {@link Entity} with {@link CombatAttribute}s in combat.
 * @param <E> an {@code Entity}
 */
public abstract class AttributedCombatant<E extends Entity> extends Combatant<E> implements CombatAttributeHolder {
    protected final HashMap<CombatAttribute, Short> combatAttributes = new HashMap<>();

    protected AttributedCombatant(UUID id) {
        super(id);
    }

    public short getValueForCombatAttribute(CombatAttribute combatAttribute) {
        return combatAttributes.getOrDefault(combatAttribute, (short) 0);
    }
}
