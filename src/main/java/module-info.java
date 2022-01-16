module com.tyrengard.unbound.combat {
    requires org.bukkit;
    requires org.jetbrains.annotations;
    requires com.tyrengard.aureycore.foundation;
    requires morphia.core;
    requires MagicksAPI;
    requires com.google.gson;
    requires com.tyrengard.aureycore.guis;

    exports com.tyrengard.unbound.combat;
    exports com.tyrengard.unbound.combat.log;
    exports com.tyrengard.unbound.combat.enums;
    exports com.tyrengard.unbound.combat.stats;
    exports com.tyrengard.unbound.combat.events;
    exports com.tyrengard.unbound.combat.damage;
    exports com.tyrengard.unbound.combat.effects;
    exports com.tyrengard.unbound.combat.equipment;
    exports com.tyrengard.unbound.combat.combatant;
    exports com.tyrengard.unbound.combat.combatant.skills;
    exports com.tyrengard.unbound.combat.combatant.skills.resolvables;
    exports com.tyrengard.unbound.combat.resistance;
}