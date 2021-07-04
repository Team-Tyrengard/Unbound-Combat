package net.havengarde.unbound.combat;

import net.havengarde.aureycore.foundation.AManager;
import net.havengarde.unbound.combat.combatant.Combatant;
import net.havengarde.unbound.combat.party.UCParty;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

public final class PartyManager extends AManager<UnboundCombat> {
    private static PartyManager instance;
    private static NamespacedKey UC_PARTY_METADATA_KEY;
    PartyManager(UnboundCombat plugin) {
        super(plugin);
        instance = this;
        UC_PARTY_METADATA_KEY = new NamespacedKey(plugin, "UC_PARTY");
    }

    @Override
    protected void startup() {

    }

    @Override
    protected void cleanup() {

    }

    protected void assignToParty(@NotNull Combatant<?> combatant, @NotNull UCParty party) {
        Entity entity = combatant.getEntity();

    }
}
