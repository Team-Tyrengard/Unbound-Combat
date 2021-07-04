package net.havengarde.unbound.combat.combatant.impl;

import net.havengarde.unbound.combat.combatant.CombatantTemplate;
import net.havengarde.unbound.combat.stats.CombatStat;
import net.havengarde.unbound.combat.combatant.PlayerCombatant;
import org.bukkit.entity.Player;

import java.util.Hashtable;

public class DefaultPlayerCombatantTemplate implements CombatantTemplate<Player, PlayerCombatant> {
    @Override
    public Hashtable<CombatStat, Double> getBaseStats() {
        Hashtable<CombatStat, Double> baseStats = new Hashtable<>();
        for (CombatStat cs : CombatStat.values())
            baseStats.put(cs, switch (cs) {
                case MELEE_DAMAGE -> 1.0;
                case SWING_SPEED -> 4.0;
                case HEALTH -> 20.0;
                case MANA -> 40.0;
                case MANA_REGEN -> 2.0;
                case EVASION -> 0.025;
                case CRITICAL_CHANCE -> 0.05;
                default -> 0.0;
            });

        return baseStats;
    }

    @Override
    public PlayerCombatant construct(Player entity) {
        PlayerCombatant pc = new PlayerCombatant(entity.getUniqueId()) {};
        applyTemplate(pc);
        return pc;
    }
}
