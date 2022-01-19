package com.tyrengard.unbound.combat.hooks.magicksapi.castables;

import com.tyrengard.magicksapi.SkillFailureReason;
import com.tyrengard.magicksapi.SkillUser;
import com.tyrengard.magicksapi.castables.ProjectileAttachedSkill;
import com.tyrengard.unbound.combat.combatant.Combatant;
import com.tyrengard.unbound.combat.equipment.EquipmentType;
import com.tyrengard.unbound.combat.hooks.magicksapi.CombatSkillFailureReason;
import com.tyrengard.unbound.combat.hooks.magicksapi.MagicksAPICombatSkill;
import com.tyrengard.unbound.combat.skills.CombatSkill;
import com.tyrengard.unbound.combat.skills.CombatSkilled;
import com.tyrengard.unbound.combat.skills.SkillType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

public interface ProjectileAttachedCombatSkill extends MagicksAPICombatSkill, ProjectileAttachedSkill {
    @Override
    default SkillFailureReason cast(SkillUser caster, Projectile projectile) {
        if (caster instanceof CombatSkilled combatSkilled &&
                combatSkilled instanceof Combatant<?> combatantCaster) {
            final ItemStack heldItem;
            if (combatantCaster.getEntity() instanceof Player p)
                heldItem = p.getInventory().getItemInMainHand();
            else if (combatantCaster.getEntity() instanceof LivingEntity le) {
                EntityEquipment entityEquipment = le.getEquipment();
                if (entityEquipment != null)
                    heldItem = entityEquipment.getItemInMainHand();
                else heldItem = null;
            } else heldItem = null;

            if (!allowsWeapon(EquipmentType.of(heldItem)))
                return CombatSkillFailureReason.INVALID_EQUIPPED_ITEM;

            return null;
        } else return CombatSkillFailureReason.UNKNOWN;
    }
}
