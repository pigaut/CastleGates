package io.github.pigaut.castlegates.gate;

import io.github.pigaut.castlegates.*;
import io.github.pigaut.castlegates.api.event.*;
import io.github.pigaut.castlegates.gate.state.*;
import io.github.pigaut.castlegates.player.*;
import io.github.pigaut.voxel.bukkit.Rotation;
import io.github.pigaut.voxel.core.context.*;
import io.github.pigaut.voxel.core.hologram.*;
import io.github.pigaut.voxel.data.function.*;
import io.github.pigaut.voxel.data.structure.*;
import io.github.pigaut.voxel.data.structure.Structure;
import io.github.pigaut.voxel.util.Server;
import org.bukkit.*;
import org.bukkit.block.*;
import org.bukkit.entity.*;
import org.jetbrains.annotations.*;

import java.time.*;
import java.util.*;

public class GateUtil {

    private static final CastleGatesPlugin plugin = CastleGatesPlugin.getInstance();

    public static void mine(@NotNull Gate gate, @NotNull Player player, @NotNull Block block) {
        if (!gate.isValid()) {
            gate.remove();
            return;
        }

        GatePhase gatePhase = gate.getPhase();
        if (gatePhase.getDecorativeBlocks().contains(block.getType())) {
            return;
        }

        Function breakFunction = gatePhase.getBreakFunction();
        if (breakFunction != null) {
            Context context = Context.builder()
                    .withPlayer(player)
                    .withPlayerState(plugin.getPlayerState(player))
                    .withTool(player.getInventory().getItemInMainHand())
                    .withBlock(block)
                    .with(Gate.class, gate)
                    .build();

            breakFunction.run(context);
        }
    }

    public static void init(@NotNull Gate gate, int phaseIndex, @NotNull GateTransition transition) {
        GateState state = gate.getState();
        GatePhase phase = gate.getPhase(phaseIndex);
        Location origin = gate.getOrigin();
        Rotation rotation = gate.getRotation();
        Context context = Context.builder()
                .withBlock(origin.getBlock())
                .with(Gate.class, gate)
                .build();

        // Set phase and transition
        state.setCurrentPhase(phaseIndex);
        state.setTransition(transition);

        // Place structure
        StructureTemplate newStructure = phase.getStructureTemplate();
        state.setStructure(newStructure.place(origin, rotation));

        // Set health
        Double newHealth = phase.getMaxHealth();
        state.setHealth(newHealth);

        // Spawn hologram
        state.removeHologram();
        HologramTemplate newHologram = phase.getOpeningHologram();
        if (newHologram != null) {
            Location offsetLocation = origin.clone().add(0.5, 0.5, 0.5);
            state.setHologram(newHologram.spawn(offsetLocation, rotation, context));
        }

        if (transition != GateTransition.NONE) {
            boolean opening = transition.isOpening();
            int delay = opening ? phase.getOpeningDelay() : phase.getClosingDelay();
            state.setTransitionStart(Instant.now());
            state.setTransitionTask(plugin.getScheduler().runTaskLater(delay, () -> {
                state.setTransition(GateTransition.NONE);
                state.setTransitionTask(null);
                if (opening) {
                    open(gate);
                } else {
                    close(gate);
                }
            }));
        }

        // Run custom functions
        Function transitionFunction = phase.getTransitionFunction();
        if (transitionFunction != null) {
            transitionFunction.run(context);
        }
    }

    public static void open(@NotNull Gate gate) {
        if (gate.isFullyOpen()) {
            return;
        }

        GateState state = gate.getState();
        Location origin = gate.getOrigin();
        Rotation rotation = gate.getRotation();
        Context context = Context.builder()
                .withBlock(origin.getBlock())
                .with(Gate.class, gate)
                .build();

        // Call gate open event
        GateOpenEvent gateOpenEvent = new GateOpenEvent(origin, gate.getName(), gate.getState().getCurrentPhase());
        Server.callEvent(gateOpenEvent);
        if (gateOpenEvent.isCancelled()) {
            return;
        }

        // Wtf is this?
        GatePhase currentPhase = gate.getPhase();
        if (currentPhase.getOpeningDelay() == 0) {
            return;
        }

        // Replace phase and transition
        int nextPhaseIndex = gate.getNextOpeningPhase();
        state.setCurrentPhase(nextPhaseIndex);
        state.setTransition(GateTransition.OPENING);

        GatePhase nextPhase = gate.getPhase(nextPhaseIndex);

        // Replace structure
        Structure existingStructure = state.getStructure();
        StructureTemplate newStructure = nextPhase.getStructureTemplate();
        state.setStructure(existingStructure != null ?
                existingStructure.replace(newStructure) : newStructure.place(origin, rotation));

        // Replace hologram
        state.removeHologram();
        HologramTemplate newHologram = nextPhase.getOpeningHologram();
        if (newHologram != null) {
            Location offsetLocation = origin.clone().add(0.5, 0.5, 0.5);
            state.setHologram(newHologram.spawn(offsetLocation, rotation, context));
        }

        // Replace health
        Double newHealth = nextPhase.getMaxHealth();
        state.setHealth(newHealth);

        // Replace transition task
        int openingDelay = nextPhase.getOpeningDelay();
        state.cancelTransition();
        state.setTransitionStart(Instant.now());
        state.setTransitionTask(plugin.getScheduler().runTaskLater(openingDelay, () -> {
            state.setTransition(GateTransition.NONE);
            state.setTransitionTask(null);
            open(gate);
        }));

        // Run custom functions
        Function transitionFunction = nextPhase.getTransitionFunction();
        if (transitionFunction != null) {
            transitionFunction.run(context);
        }
        Function openFunction = nextPhase.getOpeningFunction();
        if (openFunction != null) {
            openFunction.run(context);
        }
    }

    public static void close(@NotNull Gate gate) {
        if (gate.isFullyClosed()) {
            return;
        }

        GateState state = gate.getState();
        Location origin = gate.getOrigin();
        Rotation rotation = gate.getRotation();
        Context context = Context.builder()
                .withBlock(origin.getBlock())
                .with(Gate.class, gate)
                .build();

        // Call gate close event
        GateCloseEvent gateCloseEvent = new GateCloseEvent(origin, gate.getName(), gate.getState().getCurrentPhase());
        Server.callEvent(gateCloseEvent);
        if (gateCloseEvent.isCancelled()) {
            return;
        }

        // Replace phase and transition
        int nextPhaseIndex = gate.getNextClosingPhase();
        state.setCurrentPhase(nextPhaseIndex);
        state.setTransition(GateTransition.CLOSING);

        GatePhase nextPhase = gate.getPhase(nextPhaseIndex);

        // Replace structure
        Structure existingStructure = state.getStructure();
        StructureTemplate newStructure = nextPhase.getStructureTemplate();
        state.setStructure(existingStructure != null ?
                existingStructure.replace(newStructure) : newStructure.place(origin, rotation));

        // Replace hologram
        state.removeHologram();
        HologramTemplate newHologram = nextPhase.getClosingHologram();
        if (newHologram != null) {
            Location offsetLocation = origin.clone().add(0.5, 0.5, 0.5);
            state.setHologram(newHologram.spawn(offsetLocation, rotation, context));
        }

        // Replace health
        Double newHealth = nextPhase.getMaxHealth();
        state.setHealth(newHealth);

        // Replace transition task
        int closingDelay = nextPhase.getClosingDelay();
        state.cancelTransition();
        state.setTransitionStart(Instant.now());
        state.setTransitionTask(plugin.getScheduler().runTaskLater(closingDelay, () -> {
            state.setTransition(GateTransition.NONE);
            state.setTransitionTask(null);
            close(gate);
        }));

        // Run custom functions
        Function transitionFunction = nextPhase.getTransitionFunction();
        if (transitionFunction != null) {
            transitionFunction.run(context);
        }
        Function closeFunction = nextPhase.getClosingFunction();
        if (closeFunction != null) {
            closeFunction.run(context);
        }
    }

}
