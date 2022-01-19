package com.tyrengard.unbound.combat.skills.resolvables;

import com.tyrengard.unbound.combat.damage.DamageInstance;
import com.tyrengard.unbound.combat.damage.DamageSource;
import com.tyrengard.unbound.combat.skills.CombatSkill;
import org.bukkit.entity.Entity;

/**
 * Represents a {@link CombatSkill} that resolves into a {@link DamageInstance} with itself as a source.
 */
public interface DamagingSkill extends CombatSkill, DamageSource {
    DamageInstance resolve(Entity target, byte level);
}
