package net.havengarde.unbound.combat;

import net.havengarde.magicksapi.events.SkillCastEvent;
import net.havengarde.unbound.combat.events.UCActivateSkillEvent;
import net.havengarde.unbound.combat.combatant.PlayerCombatant;
import net.havengarde.unbound.combat.skills.UCSkill;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

class UCEventListener implements Listener {
    @EventHandler
    private void onActivateSkill(UCActivateSkillEvent e) {
        if (e.getCombatant() instanceof PlayerCombatant pc) {
            Player p = pc.getEntity();

            p.sendMessage(ChatColor.GOLD + "ACTIVE" + ChatColor.WHITE + " skills are ready.");
        }
    }
    @EventHandler
    private void onSkillCast(SkillCastEvent e) {
        if (e.getSkillUser() instanceof PlayerCombatant pc && e.getSkill() instanceof UCSkill skill) {
            Player p = pc.getEntity();
            if (skill.getCooldown() > 0) switch (e.getOutcome()) {
                case SUCCESS -> p.sendMessage(ChatColor.GRAY + "Casted skill " + ChatColor.GOLD + skill.getName() + ChatColor.GRAY +  "!");
                case FAILURE_ON_COOLDOWN -> p.sendMessage(ChatColor.DARK_RED + skill.getName() + " is on cooldown (" + pc.getCooldownForSkill(skill) + "s)!");
                case FAILURE_INSUFFICIENT_RESOURCE -> p.sendMessage(ChatColor.DARK_RED + "Insufficient mana to cast " + skill.getName() + " (costs " + skill.getResourceCost() + " mana)!");
            }
        }
    }
}
