package com.tyrengard.unbound.combat.damageindicator;

import com.tyrengard.aureycore.foundation.common.stringformat.Color;
import org.bukkit.Location;

import java.text.DecimalFormat;

public final class DamageIndicator {
    private static final DecimalFormat df = new DecimalFormat("#.##");
    final Location location;
    final String label;

    public DamageIndicator(Location location, double healthChange) {
        this(location, healthChange, null);
    }

    public DamageIndicator(Location location, double healthChange, Color customColor) {
        this.location = location;
        String formattedValue = df.format(healthChange);

        if (customColor != null) this.label = customColor.toChatColor() + formattedValue;
        else if (healthChange > 0) this.label = Color.GREEN.toChatColor() + formattedValue;
        else if (healthChange < 0) this.label = Color.RED.toChatColor() + formattedValue;
        else this.label = Color.GRAY.toChatColor() + formattedValue;
    }

    public DamageIndicator(Location location, String label) {
        this.location = location;
        this.label = label;
    }
}
