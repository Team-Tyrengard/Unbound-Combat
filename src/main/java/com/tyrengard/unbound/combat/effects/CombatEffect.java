package com.tyrengard.unbound.combat.effects;

import com.tyrengard.unbound.combat.CombatInstance;
import org.bukkit.entity.Entity;

import java.util.Objects;

/**
 * Represents an effect applied in combat.<br />
 * {@code CombatEffect}s can stack, tick down, expire, and/or resolve an effect on combat.
 */
public abstract class CombatEffect {
    protected final String name;
    protected final Entity source;
    protected final CombatEffectType type;
    protected final ExpiryBehavior expiryBehavior;
    protected final long seconds;
    protected long currentSeconds = 0;
    protected final int maximumStacks;
    protected int stacks;

    protected CombatEffect(String name, Entity source, CombatEffectType type, ExpiryBehavior expiryBehavior, int seconds, int maximumStacks, int initialStacks) {
        this.name = name;
        this.source = source;
        this.type = type;
        this.expiryBehavior = expiryBehavior;
        this.seconds = seconds;
        this.maximumStacks = maximumStacks;
        this.stacks = initialStacks;
    }

    public String getName() {
        return name;
    }

    public Entity getSource() {
        return source;
    }

    public ExpiryBehavior getExpiryBehavior() {
        return expiryBehavior;
    }

    public CombatEffectType getType() { return type; }

    public int getMaximumStacks() {
        return maximumStacks;
    }

    public int getStacks() {
        return stacks;
    }

    public void setStacks(int stacks) {
        this.stacks = stacks;
    }

    public final boolean tick(Entity target) {
        boolean expired = false;
        this.currentSeconds++;
        this.onTick(target);
        if (this.seconds == this.currentSeconds) {
            if (expiryBehavior == ExpiryBehavior.FULL)
                expired = true;
            else {
                this.stacks--;
                if (stacks == 0) expired = true;
                else this.currentSeconds = 0;
            }
        }

        if (expired)
            onExpire(target);

        return expired;
    }

    public void onTick(Entity target) {}
    public void onExpire(Entity target) {}
    public void applyToEntity(Entity target) {}
    public void resolve(CombatInstance combatInstance) {}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CombatEffect that = (CombatEffect) o;
        return getName().equals(that.getName()) && getSource().equals(that.getSource());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getSource());
    }

    public enum ExpiryBehavior {
        /**
         * Retains its full stack count until it expires.
         */
        FULL,

        /**
         * Stacks reduce by 1 per tick until it expires.
         */
        DIMINISHING
    }
}
