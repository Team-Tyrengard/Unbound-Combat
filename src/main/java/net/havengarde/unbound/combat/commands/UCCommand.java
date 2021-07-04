package net.havengarde.unbound.combat.commands;

import net.havengarde.aureycore.common.util.StringUtils;
import net.havengarde.aureycore.foundation.ACommandExecutor;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;

public class UCCommand extends ACommandExecutor {
    private static final String SEPARATOR_LEFT = "\u297C", SEPARATOR_RIGHT = "\u297D";
    private static final String UNBOUND_HELP_HEADER = ChatColor.DARK_PURPLE + SEPARATOR_LEFT
            + StringUtils.padString(ChatColor.AQUA + "Unbound Combat - Commands" + ChatColor.DARK_PURPLE, '-',
            StringUtils.MAX_CHAT_SIZE, StringUtils.StringPaddingOptions.CENTER) + SEPARATOR_RIGHT;

    public UCCommand() {
        super();

        Bukkit.getLogger().info("Adding /unbound-combat-admin commands...");
        addAdminCommands();
        Bukkit.getLogger().info("Adding /unbound-combat commands...");
        addRegularCommands();

        createHelpCommands(UNBOUND_HELP_HEADER, ChatColor.DARK_PURPLE, ChatColor.AQUA);
    }

    public void addAdminCommands() {

    }

    public void addRegularCommands() {

    }
}
