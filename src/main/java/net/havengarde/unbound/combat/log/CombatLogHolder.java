package net.havengarde.unbound.combat.log;

import java.util.Collection;

public interface CombatLogHolder {
    void storeCombatLog(CombatLog combatLog);
    Collection<CombatLog> getCombatLogs();
}
