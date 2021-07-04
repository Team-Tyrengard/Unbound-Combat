package net.havengarde.unbound.combat.skills;

import net.havengarde.magicksapi.FailureReason;
import net.havengarde.magicksapi.SkillUser;
import net.havengarde.magicksapi.castables.EntityTargetedSkill;
import net.havengarde.unbound.combat.enums.UCSkillFailureReason;
import net.havengarde.unbound.combat.equipment.EquipmentType;
import net.havengarde.unbound.combat.combatant.Combatant;
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
