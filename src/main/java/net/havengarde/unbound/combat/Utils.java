package net.havengarde.unbound.combat;

public class Utils {
    public static boolean passChanceCheck(double chance) {
        return Math.random() <= chance;
    }
}
