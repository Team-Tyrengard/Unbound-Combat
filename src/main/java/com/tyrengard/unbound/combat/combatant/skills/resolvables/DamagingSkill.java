package com.tyrengard.unbound.combat.combatant.skills.resolvables;

import com.tyrengard.unbound.combat.damage.DamageInstance;
import org.bukkit.entity.Entity;

public interface DamagingSkill {
    DamageInstance resolve(byte level, Entity target);
}
