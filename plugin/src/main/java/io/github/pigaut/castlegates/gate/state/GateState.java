package io.github.pigaut.castlegates.gate.state;

import io.github.pigaut.castlegates.gate.*;
import io.github.pigaut.voxel.core.hologram.*;
import io.github.pigaut.voxel.core.placeholder.*;
import io.github.pigaut.voxel.data.structure.*;
import io.github.pigaut.voxel.data.structure.*;
import io.github.pigaut.voxel.plugin.task.*;
import io.github.pigaut.yaml.util.*;
import org.bukkit.*;
import org.jetbrains.annotations.*;

import java.time.*;

public class GateState {

    private final Gate gate;

    private Structure structure;
    private @Nullable Hologram hologram = null;
    private @Nullable Double health;

    private @Nullable Instant transitionStart = null;
    private @Nullable Task transitionTask = null;

    private GateTransition transition;
    private int currentPhase;

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

    public int getTicksToNextPhase() {
        if (transitionStart == null) {
            return 0;
        }
        GatePhase phase = gate.getPhase(currentPhase);
        int timePassed = (int) (Duration.between(transitionStart, Instant.now()).toMillis() / 50);
        return phase.getOpeningDelay() - timePassed;
    }

    public int getTicksToOpenPhase() {
        int ticksToRegrown = getTicksToNextPhase();
        for (int i = currentPhase + 1; i < gate.getMaxPhase(); i++) {
            GatePhase phase = gate.getPhase(i);
            if (phase.getOpeningDelay() != 0) {
                ticksToRegrown += phase.getOpeningDelay();
            }
        }
        return ticksToRegrown;
    }

    public int getCurrentPhase() {
        return currentPhase;
    }

    public void setCurrentPhase(int currentPhase) {
        Preconditions.checkArgument(gate.isValidPhase(currentPhase), "Gate phase is out of bounds");
        this.currentPhase = currentPhase;
    }

    public @Nullable Hologram getHologram() {
        return hologram;
    }

    public void setHologram(@Nullable Hologram hologram) {
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
            hologram.remove();
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

    public @Nullable Double getHealth() {
        if (!gate.getTemplate().hasHealth()) {
            return null;
        }

        double totalHealth = 0.0;
        for (int i = 0; i < currentPhase; i++) {
            Double phaseHealth = gate.getPhase(i).getMaxHealth();
            if (phaseHealth != null) {
                totalHealth += phaseHealth;
            }
        }

        if (health != null) {
            totalHealth += health;
        }

        return totalHealth;
    }

    public Double getPhaseHealth() {
        return health;
    }

//    @Override
//    public @NotNull Placeholder[] getPlaceholders() {
//        int ticksToNextPhase = getTicksToNextPhase();
//        int ticksToRegrownPhase = getTicksToOpenPhase();
//
//        Location origin = gate.getOrigin();
//        return new Placeholder[]{
//                Placeholder.of("{gate}", gate.getName()),
//                Placeholder.of("{gate_phase}", currentPhase),
//                Placeholder.of("{gate_phases}", gate.getMaxPhase()),
//                Placeholder.of("{gate_state}", transition != null ? transition.toString().toLowerCase() : "none"),
//                Placeholder.of("{gate_rotation}", gate.getRotation().toString().toLowerCase()),
//                Placeholder.of("{gate_world}", origin.getWorld().getName()),
//                Placeholder.of("{gate_x}", origin.getBlockX()),
//                Placeholder.of("{gate_y}", origin.getBlockY()),
//                Placeholder.of("{gate_z}", origin.getBlockZ()),
//
//                Placeholder.of("{phase_timer}", Ticks.formatCompact(ticksToNextPhase)),
//                Placeholder.of("{phase_timer_full}", Ticks.formatFull(ticksToNextPhase)),
//                Placeholder.of("{phase_timer_hours}", Ticks.toHours(ticksToNextPhase)),
//                Placeholder.of("{phase_timer_minutes}", Ticks.toMinutes(ticksToNextPhase)),
//                Placeholder.of("{phase_timer_seconds}", Ticks.toSeconds(ticksToNextPhase)),
//
//                Placeholder.of("{gate_timer}", Ticks.formatCompact(ticksToRegrownPhase)),
//                Placeholder.of("{gate_timer_full}", Ticks.formatFull(ticksToRegrownPhase)),
//                Placeholder.of("{gate_timer_hours}", Ticks.toHours(ticksToRegrownPhase)),
//                Placeholder.of("{gate_timer_minutes}", Ticks.toMinutes(ticksToRegrownPhase)),
//                Placeholder.of("{gate_timer_seconds}", Ticks.toSeconds(ticksToRegrownPhase))
//        };
//    }

}
