module com.tyrengard.unbound.combat {
    requires org.bukkit;
    requires org.jetbrains.annotations;
    requires com.tyrengard.aureycore.foundation;
    requires morphia.core;
    requires MagicksAPI;
    requires com.google.gson;
    requires com.tyrengard.aureycore.guis;

    exports com.tyrengard.unbound.combat;
    exports com.tyrengard.unbound.combat.stats;
    exports com.tyrengard.unbound.combat.events;
    exports com.tyrengard.unbound.combat.combatant;
}