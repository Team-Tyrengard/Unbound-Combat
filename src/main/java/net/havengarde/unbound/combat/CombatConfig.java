package net.havengarde.unbound.combat;

public class CombatConfig {
    private static CombatConfig instance;

    // region Config values
    private int maxSecondsInCombat;

    private double basePlayerMovementSpeed;
    // endregion

    CombatConfig() { instance = this; }

    // region Setters
    void setMaxSecondsInCombat(int maxSecondsInCombat) {
        this.maxSecondsInCombat = maxSecondsInCombat;
    }
    void setBasePlayerMovementSpeed(double basePlayerMovementSpeed) {
        this.basePlayerMovementSpeed = basePlayerMovementSpeed;
    }
    // endregion

    // region Getters
    public static int getMaxSecondsInCombat() {
        return instance.maxSecondsInCombat;
    }
    public static double getBasePlayerMovementSpeed() {
        return instance.basePlayerMovementSpeed;
    }
    // endregion
}
