package net.havengarde.unbound.combat.skills.resolvables;

import net.havengarde.unbound.combat.damage.DamageInstance;
import org.bukkit.entity.Entity;

public interface DamagingSkill {
    DamageInstance resolve(byte level, Entity target);
}
