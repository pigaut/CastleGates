package io.github.pigaut.castlegates.gate;

import io.github.pigaut.castlegates.*;
import io.github.pigaut.castlegates.api.event.*;
import io.github.pigaut.castlegates.gate.stage.*;
import io.github.pigaut.castlegates.gate.state.*;
import io.github.pigaut.castlegates.player.*;
import io.github.pigaut.voxel.bukkit.Rotation;
import io.github.pigaut.voxel.core.function.*;
import io.github.pigaut.voxel.core.hologram.*;
import io.github.pigaut.voxel.core.structure.*;
import io.github.pigaut.voxel.core.structure.Structure;
import io.github.pigaut.voxel.plugin.task.*;
import io.github.pigaut.voxel.server.Server;
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

        GateStage gateStage = gate.getStage();
        if (gateStage.getDecorativeBlocks().contains(block.getType())) {
            return;
        }

        GatesPlayer playerState = plugin.getPlayerState(player);
        playerState.updatePlaceholders(gate.getState());

        Function breakFunction = gateStage.getBreakFunction();
        if (breakFunction != null) {
            breakFunction.run(playerState, null, block);
        }
    }

    public static void init(@NotNull Gate gate, int stageIndex, @NotNull GateTransition transition) {
        GateState state = gate.getState();
        GateStage stage = gate.getStage(stageIndex);
        Location origin = gate.getOrigin();
        Rotation rotation = gate.getRotation();

        // Set stage and transition
        state.setCurrentStage(stageIndex);
        state.setTransition(transition);

        // Place structure
        StructureTemplate newStructure = stage.getStructureTemplate();
        state.setStructure(newStructure.place(origin, rotation));

        // Spawn hologram
        state.removeHologram();
        Hologram newHologram = stage.getOpeningHologram();
        if (newHologram != null) {
            Location offsetLocation = origin.clone().add(0.5, 0.5, 0.5);
            state.setHologram(newHologram.spawn(offsetLocation, rotation, List.of(state)));
        }

        // Set health
        Double newHealth = stage.getHealth();
        state.setHealth(newHealth);

        if (transition != GateTransition.NONE) {
            boolean opening = transition.isOpening();
            int delay = opening ? stage.getOpeningDelay() : stage.getClosingDelay();
            plugin.getScheduler().runTaskLater(delay, () -> {
                if (opening) {
                    open(gate);
                } else {
                    close(gate);
                }
            });
        }
    }

    public static void open(@NotNull Gate gate) {
        if (gate.isFullyOpen()) {
            return;
        }

        GateState state = gate.getState();
        Location origin = gate.getOrigin();
        Rotation rotation = gate.getRotation();

        // Call gate open event
        GateOpenEvent gateOpenEvent = new GateOpenEvent(origin);
        Server.callEvent(gateOpenEvent);
        if (gateOpenEvent.isCancelled()) {
            return;
        }

        // Wtf is this?
        GateStage currentStage = gate.getStage();
        if (currentStage.getOpeningDelay() == 0) {
            return;
        }

        // Replace stage and transition
        int nextStageIndex = gate.getNextOpeningStage();
        state.setCurrentStage(nextStageIndex);
        state.setTransition(GateTransition.OPENING);

        GateStage nextStage = gate.getStage(nextStageIndex);

        // Replace structure
        Structure existingStructure = state.getStructure();
        StructureTemplate newStructure = nextStage.getStructureTemplate();
        state.setStructure(existingStructure != null ?
                existingStructure.replace(newStructure) : newStructure.place(origin, rotation));

        // Replace hologram
        state.removeHologram();
        Hologram newHologram = nextStage.getOpeningHologram();
        if (newHologram != null) {
            Location offsetLocation = origin.clone().add(0.5, 0.5, 0.5);
            state.setHologram(newHologram.spawn(offsetLocation, rotation, List.of(state)));
        }

        // Replace health
        Double newHealth = nextStage.getHealth();
        state.setHealth(newHealth);

        // Replace transition task
        int openingDelay = nextStage.getOpeningDelay();
        state.cancelTransition();
        state.setTransitionStart(Instant.now());
        state.setTransitionTask(plugin.getScheduler().runTaskLater(openingDelay, () -> {
            state.setTransition(GateTransition.NONE);
            state.setTransitionTask(null);
            open(gate);
        }));

        // Run custom functions
        Block block = origin.getBlock();
        Function transitionFunction = nextStage.getTransitionFunction();
        if (transitionFunction != null) {
            transitionFunction.run(block);
        }
        Function openFunction = nextStage.getOpeningFunction();
        if (openFunction != null) {
            openFunction.run(block);
        }
    }

    public static void close(@NotNull Gate gate) {
        if (gate.isFullyClosed()) {
            return;
        }

        GateState state = gate.getState();
        Location origin = gate.getOrigin();
        Rotation rotation = gate.getRotation();

        // Call gate close event
        GateCloseEvent gateCloseEvent = new GateCloseEvent(origin);
        Server.callEvent(gateCloseEvent);
        if (gateCloseEvent.isCancelled()) {
            return;
        }

        // Replace stage and transition
        int nextStageIndex = gate.getNextClosingStage();
        state.setCurrentStage(nextStageIndex);
        state.setTransition(GateTransition.CLOSING);

        GateStage nextStage = gate.getStage(nextStageIndex);

        // Replace structure
        Structure existingStructure = state.getStructure();
        StructureTemplate newStructure = nextStage.getStructureTemplate();
        state.setStructure(existingStructure != null ?
                existingStructure.replace(newStructure) : newStructure.place(origin, rotation));

        // Replace hologram
        state.removeHologram();
        Hologram newHologram = nextStage.getOpeningHologram();
        if (newHologram != null) {
            Location offsetLocation = origin.clone().add(0.5, 0.5, 0.5);
            state.setHologram(newHologram.spawn(offsetLocation, rotation, List.of(state)));
        }

        // Replace health
        Double newHealth = nextStage.getHealth();
        state.setHealth(newHealth);

        // Replace transition task
        int closingDelay = nextStage.getClosingDelay();
        state.cancelTransition();
        state.setTransitionStart(Instant.now());
        state.setTransitionTask(plugin.getScheduler().runTaskLater(closingDelay, () -> {
            state.setTransition(GateTransition.NONE);
            state.setTransitionTask(null);
            close(gate);
        }));

        // Run custom functions
        Block block = origin.getBlock();
        Function transitionFunction = nextStage.getTransitionFunction();
        if (transitionFunction != null) {
            transitionFunction.run(block);
        }
        Function closeFunction = nextStage.getClosingFunction();
        if (closeFunction != null) {
            closeFunction.run(block);
        }
    }

}
