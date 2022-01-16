package com.tyrengard.unbound.combat.combatant;

import com.tyrengard.unbound.combat.enums.CombatAttribute;
import org.bukkit.entity.Entity;

import java.util.HashMap;
import java.util.UUID;

public abstract class AttributedCombatant<E extends Entity> extends Combatant<E> {
    protected final HashMap<CombatAttribute, Short> combatAttributes = new HashMap<>();

    protected AttributedCombatant(UUID id) {
        super(id);
    }
}
