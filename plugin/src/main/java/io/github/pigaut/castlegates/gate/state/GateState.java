package io.github.pigaut.castlegates.gate.state;

import io.github.pigaut.castlegates.gate.*;
import io.github.pigaut.castlegates.gate.stage.*;
import io.github.pigaut.voxel.core.hologram.*;
import io.github.pigaut.voxel.core.structure.*;
import io.github.pigaut.voxel.placeholder.*;
import io.github.pigaut.voxel.plugin.task.*;
import io.github.pigaut.yaml.util.*;
import org.bukkit.*;
import org.jetbrains.annotations.*;

import java.time.*;

public class GateState implements PlaceholderSupplier {

    private final Gate gate;

    private Structure structure;
    private @Nullable HologramDisplay hologram = null;
    private @Nullable Double health;

    private @Nullable Instant transitionStart = null;
    private @Nullable Task transitionTask = null;

    private GateTransition transition;
    private int currentStage;

    public GateState(@NotNull Gate gate) {
        this.gate = gate;
    }

    public @NotNull GateTransition getTransition() {
        return transition;
    }

    public void setTransition(@NotNull GateTransition transition) {
        this.transition = transition;
    }

    public Structure getStructure() {
        return structure;
    }

    public void setStructure(@NotNull Structure structure) {
        this.structure = structure;
    }

    public int getTicksToNextStage() {
        if (transitionStart == null) {
            return 0;
        }
        GateStage stage = gate.getStage(currentStage);
        int timePassed = (int) (Duration.between(transitionStart, Instant.now()).toMillis() / 50);
        return stage.getOpeningDelay() - timePassed;
    }

    public int getTicksToOpenStage() {
        int ticksToRegrown = getTicksToNextStage();
        for (int i = currentStage + 1; i < gate.getMaxStage(); i++) {
            GateStage stage = gate.getStage(i);
            if (stage.getOpeningDelay() != 0) {
                ticksToRegrown += stage.getOpeningDelay();
            }
        }
        return ticksToRegrown;
    }

    public int getCurrentStage() {
        return currentStage;
    }

    public void setCurrentStage(int currentStage) {
        Preconditions.checkArgument(gate.isStage(currentStage), "Gate stage is out of bounds");
        this.currentStage = currentStage;
    }

    public @Nullable HologramDisplay getHologram() {
        return hologram;
    }

    public void setHologram(@Nullable HologramDisplay hologram) {
        this.hologram = hologram;
    }

    public void cancelTransition() {
        if (transitionTask != null) {
            if (!transitionTask.isCancelled()) {
                transitionTask.cancel();
            }
            transitionTask = null;
        }
    }

    public void removeBlocks() {
        structure.remove();
    }

    public void removeHologram() {
        if (hologram != null) {
            hologram.destroy();
            hologram = null;
        }
    }

    public void setHealth(@Nullable Double health) {
        this.health = health;
    }

    public void setTransitionStart(@NotNull Instant transitionStart) {
        this.transitionStart = transitionStart;
    }

    public void setTransitionTask(@Nullable Task transitionTask) {
        this.transitionTask = transitionTask;
    }

    @Override
    public @NotNull Placeholder[] getPlaceholders() {
        int ticksToNextStage = getTicksToNextStage();
        int ticksToRegrownStage = getTicksToOpenStage();

        Location origin = gate.getOrigin();
        return new Placeholder[]{
                Placeholder.of("{gate}", gate.getName()),
                Placeholder.of("{gate_stage}", currentStage),
                Placeholder.of("{gate_stages}", gate.getMaxStage()),
                Placeholder.of("{gate_state}", transition != null ? transition.toString().toLowerCase() : "none"),
                Placeholder.of("{gate_rotation}", gate.getRotation().toString().toLowerCase()),
                Placeholder.of("{gate_world}", origin.getWorld().getName()),
                Placeholder.of("{gate_x}", origin.getBlockX()),
                Placeholder.of("{gate_y}", origin.getBlockY()),
                Placeholder.of("{gate_z}", origin.getBlockZ()),

                Placeholder.of("{stage_timer}", Ticks.formatCompact(ticksToNextStage)),
                Placeholder.of("{stage_timer_full}", Ticks.formatFull(ticksToNextStage)),
                Placeholder.of("{stage_timer_hours}", Ticks.toHours(ticksToNextStage)),
                Placeholder.of("{stage_timer_minutes}", Ticks.toMinutes(ticksToNextStage)),
                Placeholder.of("{stage_timer_seconds}", Ticks.toSeconds(ticksToNextStage)),

                Placeholder.of("{gate_timer}", Ticks.formatCompact(ticksToRegrownStage)),
                Placeholder.of("{gate_timer_full}", Ticks.formatFull(ticksToRegrownStage)),
                Placeholder.of("{gate_timer_hours}", Ticks.toHours(ticksToRegrownStage)),
                Placeholder.of("{gate_timer_minutes}", Ticks.toMinutes(ticksToRegrownStage)),
                Placeholder.of("{gate_timer_seconds}", Ticks.toSeconds(ticksToRegrownStage))
        };
    }

}
