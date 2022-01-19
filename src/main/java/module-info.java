module com.tyrengard.unbound.combat {
    requires org.bukkit;
    requires org.jetbrains.annotations;
    requires com.tyrengard.aureycore.foundation;
    requires morphia.core;
    requires static com.tyrengard.magicksapi;
    requires com.google.gson;
    requires com.tyrengard.aureycore.guis;
    requires ProtocolLib;

    exports com.tyrengard.unbound.combat;
    exports com.tyrengard.unbound.combat.log;
    exports com.tyrengard.unbound.combat.enums;
    exports com.tyrengard.unbound.combat.stats;
    exports com.tyrengard.unbound.combat.skills;
    exports com.tyrengard.unbound.combat.skills.resolvables;
    exports com.tyrengard.unbound.combat.events;
    exports com.tyrengard.unbound.combat.damage;
    exports com.tyrengard.unbound.combat.effects;
    exports com.tyrengard.unbound.combat.effects.misc;
    exports com.tyrengard.unbound.combat.equipment;
    exports com.tyrengard.unbound.combat.combatant;
    exports com.tyrengard.unbound.combat.combatant.provider;
    exports com.tyrengard.unbound.combat.attributes;
    exports com.tyrengard.unbound.combat.hooks.magicksapi;
    exports com.tyrengard.unbound.combat.managers;
    exports com.tyrengard.unbound.combat.hooks.magicksapi.castables;
    exports com.tyrengard.unbound.combat.hooks.magicksapi.resolvables;
}