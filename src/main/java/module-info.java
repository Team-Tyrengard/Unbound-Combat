module unbound.combat {
//    requires sentry;
    requires net.kyori.adventure;
    requires org.jetbrains.annotations;
    requires aureycore.commons;
    requires aureycore.damageindicator;
    requires aureycore.foundation;
    requires aureycore.customguis;
    requires conduit.annotations;
    requires org.bukkit;
    requires bungeecord.chat;
    requires java.logging;
    requires MagicksAPI;
    requires com.google.gson;
    exports net.havengarde.unbound.combat;
    exports net.havengarde.unbound.combat.enums;
    exports net.havengarde.unbound.combat.events;
    exports net.havengarde.unbound.combat.damage;
    exports net.havengarde.unbound.combat.effects;
    exports net.havengarde.unbound.combat.effects.debuffs;
    exports net.havengarde.unbound.combat.skills;
    exports net.havengarde.unbound.combat.skills.resolvables;
    exports net.havengarde.unbound.combat.combatant;
    exports net.havengarde.unbound.combat.stats;
    exports net.havengarde.unbound.combat.equipment;
    exports net.havengarde.unbound.combat.resistance;
    exports net.havengarde.unbound.combat.commands;
    exports net.havengarde.unbound.combat.log;
}