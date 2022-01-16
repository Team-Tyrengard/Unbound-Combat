package com.tyrengard.unbound.combat.party;

import com.tyrengard.unbound.combat.combatant.Combatant;
import dev.morphia.annotations.Id;

import java.util.HashSet;
import java.util.UUID;

public final class UCParty {
    @Id
    private final UUID id;
    private Combatant<?> owner;
    private HashSet<Combatant<?>> members;

    UCParty(Combatant<?> owner) {
        this.id = UUID.randomUUID();
        this.owner = owner;
        this.members = new HashSet<>();
        this.members.add(owner);
    }

    public UUID getId() { return id; }

    public Combatant<?> getOwner() { return owner; }

    public HashSet<Combatant<?>> getMembers() { return new HashSet<>(members); }

    void addMember(Combatant<?> member) { members.add(member); }
}
