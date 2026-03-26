package io.github.pigaut.castlegates.health;

import io.github.pigaut.castlegates.gate.*;
import org.jetbrains.annotations.*;

public class HealthUtil {

    private static @Nullable Integer toRoundedInt(@Nullable Double value) {
        return value != null ? (int) Math.round(value) : null;
    }

    public static @Nullable Integer getGateHealthInt(@NotNull Gate gate) {
        return toRoundedInt(gate.getTotalHealth());
    }

    public static @Nullable Integer getGateMaxHealthInt(@NotNull Gate gate) {
        return toRoundedInt(gate.getTemplate().getMaxHealth());
    }

    public static @Nullable Integer getPhaseHealthInt(@NotNull Gate gate) {
        return toRoundedInt(gate.getHealth());
    }

    public static @Nullable Integer getPhaseMaxHealthInt(@NotNull Gate gate) {
        return toRoundedInt(gate.getPhase().getMaxHealth());
    }

    public static @Nullable Integer getHealthPercentage(@NotNull Gate gate) {
        Double maxHealth = gate.getTemplate().getMaxHealth();
        Double currentHealth = gate.getState().getHealth();
        if (maxHealth == null || currentHealth == null) {
            return null;
        }

        double ratio = (maxHealth > 0) ? (currentHealth / maxHealth) : 0.0;

        int percent = (int) Math.round(ratio * 100.0);
        percent = Math.max(0, Math.min(100, percent));
        return percent;
    }

    public static @Nullable Integer getPhaseHealthPercentage(@NotNull Gate gate) {
        Double maxHealth = gate.getPhase().getMaxHealth();
        Double currentHealth = gate.getState().getPhaseHealth();
        if (maxHealth == null || currentHealth == null) {
            return null;
        }

        double ratio = (maxHealth > 0) ? (currentHealth / maxHealth) : 0.0;

        int percent = (int) Math.round(ratio * 100.0);
        percent = Math.max(0, Math.min(100, percent));
        return percent;
    }

}
