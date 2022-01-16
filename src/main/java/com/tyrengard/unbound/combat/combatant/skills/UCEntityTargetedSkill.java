package com.tyrengard.unbound.combat.combatant.skills;

import com.tyrengard.magicksapi.FailureReason;
import com.tyrengard.magicksapi.SkillUser;
import com.tyrengard.magicksapi.castables.EntityTargetedSkill;
import com.tyrengard.unbound.combat.combatant.Combatant;
import com.tyrengard.unbound.combat.enums.UCSkillFailureReason;
import com.tyrengard.unbound.combat.equipment.EquipmentType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

public interface UCEntityTargetedSkill extends EntityTargetedSkill {
    @Override
    default FailureReason cast(SkillUser caster, Entity target) {
        UCSkill skill = (UCSkill) this;
        Combatant<?> combatant = (Combatant<?>) caster;

        if (skill.getType() == SkillType.ACTIVE && !combatant.getActiveSkills().contains(skill))
            return UCSkillFailureReason.NOT_ACTIVATED;

        final ItemStack heldItem;
        if (combatant.getEntity() instanceof Player p)
            heldItem = p.getInventory().getItemInMainHand();
        else if (combatant.getEntity() instanceof LivingEntity le) {
            EntityEquipment entityEquipment = le.getEquipment();
            if (entityEquipment != null)
                heldItem = entityEquipment.getItemInMainHand();
            else heldItem = null;
        } else heldItem = null;

        if (!skill.allowsWeapon(EquipmentType.of(heldItem)))
            return UCSkillFailureReason.INVALID_EQUIPPED_ITEM;

        return null;
    }
}
