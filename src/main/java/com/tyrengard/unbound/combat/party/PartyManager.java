package com.tyrengard.unbound.combat.party;

import com.tyrengard.aureycore.foundation.AManager;
import com.tyrengard.unbound.combat.UnboundCombat;
import com.tyrengard.unbound.combat.combatant.Combatant;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;

import java.util.Hashtable;
import java.util.UUID;

public final class PartyManager extends AManager<UnboundCombat> {
    private static final Hashtable<UUID, UCParty> partyCache = new Hashtable<>();
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

    protected static void createParty(@NotNull Combatant<?> owner) {
        UCParty party = new UCParty(owner);
        partyCache.put(party.getId(), party);
    }

    protected static void assignToParty(@NotNull Combatant<?> combatant, @NotNull UCParty party) {
        party.addMember(combatant);
    }
}
