package com.tyrengard.unbound.combat.party;

import com.tyrengard.unbound.combat.combatant.Combatant;
import dev.morphia.annotations.Id;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public final class CombatParty {
    @Id
    private final UUID id;
    private Combatant<?> owner;
    private Set<Combatant<?>> members;

    CombatParty(Combatant<?> owner) {
        this.id = UUID.randomUUID();
        this.owner = owner;
        this.members = new HashSet<>();
        this.members.add(owner);
    }

    public UUID getId() { return id; }

    public @NotNull Combatant<?> getOwner() { return owner; }

    public @NotNull Set<Combatant<?>> getMembers() { return new HashSet<>(members); }

    void addMember(@NotNull Combatant<?> member) { members.add(member); }

    public boolean containsCombatant(@NotNull Combatant<?> combatant) {
        return owner == combatant || members.contains(combatant);
    }
}
