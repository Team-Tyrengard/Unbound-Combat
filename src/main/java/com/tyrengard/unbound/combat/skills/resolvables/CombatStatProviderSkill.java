package com.tyrengard.unbound.combat.skills.resolvables;

import com.tyrengard.unbound.combat.skills.CombatSkill;
import com.tyrengard.unbound.combat.stats.CombatStat;
import com.tyrengard.unbound.combat.damage.DamageSource;
import com.tyrengard.unbound.combat.damage.ResistanceSource;

/**
 * <p>Represents a {@link CombatSkill} that provides flat {@link CombatStat}s to its owner in combat.</p>
 *
 * Types that implement this must also implement {@link DamageSource} if they support damage combat stats,
 * such as {@link CombatStat#MELEE_DAMAGE} and {@link CombatStat#RANGED_DAMAGE}.<br />
 * Likewise, if they support resistance combat stats, such as {@link CombatStat#PHYSICAL_RESISTANCE}
 * and {@link CombatStat#MAGIC_RESISTANCE}, they must also implement {@link ResistanceSource}.
 */
public interface CombatStatProviderSkill extends CombatSkill {
    double getValueForStat(CombatStat combatStat, byte level);
}
