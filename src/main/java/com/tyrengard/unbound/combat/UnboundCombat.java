package com.tyrengard.unbound.combat;

import com.tyrengard.unbound.combat.managers.EquipmentManager;
import com.tyrengard.aureycore.foundation.AManagedPlugin;
import com.tyrengard.unbound.combat.party.PartyManager;
import org.bukkit.configuration.InvalidConfigurationException;

public final class UnboundCombat extends AManagedPlugin {
    private static UnboundCombat instance;
    public static UnboundCombat getInstance() { return instance; }

    @Override
    protected void onPluginEnable() throws InvalidConfigurationException {
        instance = this;

        this.addManager(new CombatEngine(this));
        this.addManager(new EquipmentManager(this));
        this.addManager(new PartyManager(this));

//        DamageIndicatorAPI.start(this, false);

//        this.addCommandExecutor("test", (sender, command, label, args) -> {
//            Player player = (Player) sender;
//            DamageIndicatorAPI.showIndicator(player, "TEST", player.getLocation(), new Vector(0, 0.2, 0), false, player.getHeight());
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
