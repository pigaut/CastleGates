package io.github.pigaut.castlegates.core;

import io.github.pigaut.castlegates.*;
import io.github.pigaut.castlegates.gate.*;
import io.github.pigaut.castlegates.gate.template.*;
import io.github.pigaut.castlegates.health.*;
import io.github.pigaut.castlegates.settings.*;
import io.github.pigaut.voxel.core.placeholder.*;
import org.jetbrains.annotations.*;

public class CastleGatesPlaceholders {

    public static void registerAll(@NotNull CastleGatesPlugin plugin, @NotNull PlaceholderRegistry placeholders) {
        placeholders.register("gate", context -> {
            GateTemplate gateTemplate = context.get(GateTemplate.class);
            if (gateTemplate != null) {
                return gateTemplate.getName();
            }

            Gate gate = context.get(Gate.class);
            return gate != null ? gate.getName() : null;
        });

        placeholders.register("gate_phase", context -> {
            Gate gate = context.get(Gate.class);
            return gate != null ? gate.getState().getCurrentPhase() : null;
        });

        placeholders.register("gate_phases", context -> {
            Gate gate = context.get(Gate.class);
            return gate != null ? gate.getMaxPhase() + 1 : null;
        });

        placeholders.register("gate_rotation", context -> {
            Gate gate = context.get(Gate.class);
            return gate != null ? gate.getRotation() : null;
        });

        placeholders.register("gate_world", context -> {
            Gate gate = context.get(Gate.class);
            return gate != null ? gate.getOrigin().getWorld().getName() : null;
        });

        placeholders.register("gate_x", context -> {
            Gate gate = context.get(Gate.class);
            return gate != null ? gate.getOrigin().getBlockX() : null;
        });

        placeholders.register("gate_y", context -> {
            Gate gate = context.get(Gate.class);
            return gate != null ? gate.getOrigin().getBlockY() : null;
        });

        placeholders.register("gate_z", context -> {
            Gate gate = context.get(Gate.class);
            return gate != null ? gate.getOrigin().getBlockZ() : null;
        });

        placeholders.register("gate_health", context -> {
            Gate gate = context.get(Gate.class);
            return gate != null ? HealthUtil.getGateHealthInt(gate) : null;
        });

        placeholders.register("gate_max_health", context -> {
            Gate gate = context.get(Gate.class);
            return gate != null ? HealthUtil.getGateMaxHealthInt(gate) : null;
        });

        placeholders.register("phase_health", context -> {
            Gate gate = context.get(Gate.class);
            return gate != null ? HealthUtil.getPhaseHealthInt(gate) : null;
        });

        placeholders.register("phase_max_health", context -> {
            Gate gate = context.get(Gate.class);
            return gate != null ? HealthUtil.getPhaseMaxHealthInt(gate) : null;
        });

        CastleGatesSettings settings = plugin.getSettings();
        for (HealthBar healthBar : settings.getHealthBars()) {
            placeholders.register("gate_health_bar:" + healthBar.getId(), context -> {
                Gate gate = context.get(Gate.class);
                if (gate == null) {
                    return null;
                }
                Integer healthPercentage = HealthUtil.getHealthPercentage(gate);
                if (healthPercentage == null) {
                    return null;
                }
                return healthBar.getBarByHealthPercentage(healthPercentage);
            });

            placeholders.register("phase_health_bar:" + healthBar.getId(), context -> {
                Gate gate = context.get(Gate.class);
                if (gate == null) {
                    return null;
                }
                Integer healthPercentage = HealthUtil.getPhaseHealthPercentage(gate);
                if (healthPercentage == null) {
                    return null;
                }
                return healthBar.getBarByHealthPercentage(healthPercentage);
            });
        }

    }

}
