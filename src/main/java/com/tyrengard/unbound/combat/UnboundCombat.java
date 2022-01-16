package com.tyrengard.unbound.combat;

import com.tyrengard.unbound.combat.damageindicator.DamageIndicatorAPI;
import com.tyrengard.aureycore.foundation.AManagedPlugin;
import org.bukkit.configuration.InvalidConfigurationException;

public final class UnboundCombat extends AManagedPlugin {
    private static UnboundCombat instance;
    public static UnboundCombat getInstance() { return instance; }

    @Override
    protected void onPluginEnable() throws InvalidConfigurationException {
        instance = this;

        this.addManager(new CombatEngine(this));
        this.addManager(new EquipmentManager(this));
        this.registerListener(new UCEventListener());

        DamageIndicatorAPI.useDefaultDamageIndicators(false);

//        this.addCommandExecutor("attackspeed", (sender, command, label, args) -> {
//            // TODO: "/attackspeed help" should explain how attackspeed works and what the number means
//            if (sender instanceof Player p) {
//                p.sendMessage("Attack speed (melee): " + CombatEngine.getCombatant(p).getValueForStat(CombatStat.SWING_SPEED));
//            } else sender.sendMessage("This command is only for players.");
//
//            return true;
//        });
//        this.addCommandExecutor("movespeed", (sender, command, label, args) -> {
//            // TODO: "/movespeed help" should explain how movement speed works and what the number means
//            if (sender instanceof Player p) {
//                p.sendMessage("Movement speed: " + CombatEngine.getCombatant(p).getValueForStat(CombatStat.MOVEMENT_SPEED));
//            } else sender.sendMessage("This command is only for players.");
//
//            return true;
//        });
    }

    @Override
    protected void onPostEnable() {
        setDebugLogging(false);
    }

    @Override
    protected void onPluginDisable() {

    }
}
