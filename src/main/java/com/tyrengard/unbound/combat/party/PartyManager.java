package com.tyrengard.unbound.combat.party;

import com.tyrengard.aureycore.foundation.AManager;
import com.tyrengard.unbound.combat.UnboundCombat;
import com.tyrengard.unbound.combat.combatant.Combatant;
import com.tyrengard.unbound.combat.CombatEngine;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class PartyManager extends AManager<UnboundCombat> implements Listener {
    private static final Map<UUID, CombatParty> partyCache = new HashMap<>();
    private static NamespacedKey UC_PARTY_METADATA_KEY;

    private static PartyManager instance;
    public PartyManager(UnboundCombat plugin) {
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

    // region Event handlers
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    private void onPartyMembersDamageEachOther(EntityDamageByEntityEvent e) {
        Combatant<?>
                cDamaged = CombatEngine.obtainCombatant(e.getEntity()),
                cDamager = CombatEngine.getAttackerFromEvent(e);

        if (cDamager != null && combatantsAreInSameParty(cDamaged, cDamager))
            e.setCancelled(true);
    }
    // endregion

    protected static void createParty(@NotNull Combatant<?> owner) {
        CombatParty party = new CombatParty(owner);
        partyCache.put(party.getId(), party);
    }

    protected static void assignToParty(@NotNull Combatant<?> combatant, @NotNull CombatParty party) {
        party.addMember(combatant);
    }

    static @Nullable CombatParty getPartyForCombatant(@NotNull Combatant<?> combatant) {
        return partyCache.values().stream()
                .filter(party -> party.containsCombatant(combatant)).findFirst().orElse(null);
    }

    static boolean combatantsAreInSameParty(@NotNull Combatant<?> @NotNull ... combatants) {
        if (combatants.length > 1) {
            CombatParty party = getPartyForCombatant(combatants[0]);
            if (party != null)
                for (int i = 1; i < combatants.length; i++)
                    if (!party.containsCombatant(combatants[i]))
                        return false;
        }

        return true;
    }
}
