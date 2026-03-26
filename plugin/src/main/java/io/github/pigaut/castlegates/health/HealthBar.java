package io.github.pigaut.castlegates.health;

import io.github.pigaut.castlegates.gate.*;
import io.github.pigaut.voxel.core.context.*;
import io.github.pigaut.voxel.core.placeholder.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class HealthBar implements PlaceholderSupplier {

    private final String id;
    private final Map<Integer, String> barByHealthPercentage;

    public HealthBar(@NotNull String id, Map<Integer, String> barByHealthPercentage) {
        this.id = id;
        this.barByHealthPercentage = barByHealthPercentage;
    }

    public @NotNull String getId() {
        return id;
    }

    public @Nullable String getBarByHealthPercentage(int healthPercentage) {
        return barByHealthPercentage.get(healthPercentage);
    }

    @Override
    public @Nullable Object resolve(@NotNull Context context) {
        Gate gate = context.get(Gate.class);
        if (gate == null) {
            return null;
        }

        Double maxHealth = gate.getPhase().getMaxHealth();
        Double currentHealth = gate.getState().getPhaseHealth();
        if (maxHealth == null || currentHealth == null) {
            return null;
        }

        double ratio = (maxHealth > 0) ? (currentHealth / maxHealth) : 0.0;

        int percent = (int) Math.round(ratio * 100.0);
        percent = Math.max(0, Math.min(100, percent));

        return barByHealthPercentage.get(percent);
    }

}
