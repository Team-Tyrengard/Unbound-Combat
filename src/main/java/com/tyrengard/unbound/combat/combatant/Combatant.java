package com.tyrengard.unbound.combat.combatant;

import com.tyrengard.unbound.combat.resistance.ResistanceSource;
import dev.morphia.annotations.Id;
import com.tyrengard.magicksapi.SkillUser;
import com.tyrengard.unbound.combat.damage.DamageSource;
import com.tyrengard.unbound.combat.stats.CombatStat;
import com.tyrengard.unbound.combat.effects.CombatEffect;
import com.tyrengard.unbound.combat.combatant.skills.UCSkill;
import com.tyrengard.unbound.combat.stats.CombatStatHolder;
import org.bukkit.attribute.Attributable;
import org.bukkit.entity.Entity;

import java.util.*;

@dev.morphia.annotations.Entity
public abstract class Combatant<E extends Entity> extends SkillUser implements CombatStatHolder, DamageSource, ResistanceSource {
    @Id
    protected final UUID id;

    protected final transient Hashtable<CombatStat, Double> combatStats = new Hashtable<>();

    protected final SkillMatrix skillMatrix = new SkillMatrix();
    protected final transient HashSet<UCSkill> activeSkills = new HashSet<>();
    protected transient boolean charged = false;

    protected transient long lastAttackTicks = 0;

    // region Buffs/debuffs
    protected final transient Hashtable<String, CombatEffect>
            buffs = new Hashtable<>(),
            debuffs = new Hashtable<>();
    // endregion

    protected Combatant(UUID id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return id.toString();
    }

    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Combatant<?> combatant)) return false;
        return getId().equals(combatant.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    // region Getters
    @Override
    public double getValueForStat(CombatStat stat) {
        return combatStats.getOrDefault(stat, 0.0);
    }
    public SkillMatrix getSkillMatrix() { return skillMatrix; }
    public Collection<UCSkill> getSkills() { return new HashSet<>(skillMatrix.keySet()); }
    public double getCooldownForSkill(UCSkill skill) {
        return getCooldownTicksForSkill(skill) / 20.0;
    }
    public HashSet<UCSkill> getActiveSkills() { return new HashSet<>(activeSkills); }
    public boolean isCharged() {
        return charged;
    }
    // endregion

    // region Setters
    @Override
    public void setValueForStat(CombatStat stat, double value) {
        combatStats.put(stat, value);
        if (getEntity() instanceof Attributable attributable)
            stat.applyToAttributable(attributable, value);
    }

    public void addValueForStat(CombatStat stat, double value) {
        combatStats.compute(stat, (k, v) -> {
            if (v != null) return v + value;
            else return value;
        });
    }
    public void activateSkill(UCSkill skill) {
        activeSkills.add(skill);
        assignSkill(skill, skill.getCastEvents());
    }
    public void clearActiveSkills() {
        for (UCSkill skill : activeSkills)
            unassignSkill(skill);
        activeSkills.clear();
        charged = false;
    }
    public void setCharged(boolean charged) {
        this.charged = charged;
    }
    // endregion

    // region Buffs/debuffs getters/setters
    public Collection<CombatEffect> getBuffs() { return new ArrayList<>(buffs.values()); }
    public void addBuff(CombatEffect ce) {
        if (ce != null)
            buffs.compute(ce.getName(), (name, oldCE) -> merge(ce, oldCE));
    }
    public void removeBuff(CombatEffect ce) {
        if (ce != null) {
            ce.onExpire(getEntity());
            buffs.remove(ce.getName());
        }
    }
    public Collection<CombatEffect> getDebuffs() { return new ArrayList<>(debuffs.values()); }
    public void addDebuff(CombatEffect ce) {
        if (ce != null)
            debuffs.compute(ce.getName(), (name, oldCE) -> merge(ce, oldCE));
    }
    public void removeDebuff(CombatEffect ce) {
        if (ce != null) {
            ce.onExpire(getEntity());
            debuffs.remove(ce.getName());
        }
    }

    private CombatEffect merge(CombatEffect newCE, CombatEffect oldCE) {
        Entity entity = getEntity();
        if (oldCE != null) {
            if (newCE.getMaximumStacks() > oldCE.getStacks())
                newCE.setStacks(Math.min(oldCE.getStacks() + newCE.getStacks(), newCE.getMaximumStacks())); // merge stacks
            else
                newCE.setStacks(newCE.getMaximumStacks()); // keep at maximum
            oldCE.onExpire(entity);
        }

        newCE.applyToEntity(entity);
        return newCE;
    }
    // endregion

    public boolean performAttack(long ticks) {
        if (ticks >= lastAttackTicks + (20 / getValueForStat(CombatStat.SWING_SPEED))) {
            lastAttackTicks = ticks;
            return true;
        } else return false;
    }

    // region Abstract methods
    public abstract E getEntity();
    // endregion
}
