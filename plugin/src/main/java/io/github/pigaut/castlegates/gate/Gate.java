package io.github.pigaut.castlegates.gate;

import io.github.pigaut.castlegates.*;
import io.github.pigaut.castlegates.api.event.*;
import io.github.pigaut.castlegates.gate.template.*;
import io.github.pigaut.castlegates.util.*;
import io.github.pigaut.voxel.bukkit.Rotation;
import io.github.pigaut.voxel.core.function.*;
import io.github.pigaut.voxel.core.hologram.*;
import io.github.pigaut.voxel.core.structure.*;
import io.github.pigaut.voxel.placeholder.*;
import io.github.pigaut.voxel.plugin.task.*;
import io.github.pigaut.voxel.server.Server;
import io.github.pigaut.yaml.util.*;
import org.bukkit.*;
import org.bukkit.block.*;
import org.jetbrains.annotations.*;

import java.io.*;
import java.time.*;
import java.util.*;

public class Gate implements PlaceholderSupplier {

    private static final CastleGatesPlugin plugin = CastleGatesPlugin.getInstance();

    private final GateTemplate template;
    private final Location origin;
    private final Block block;
    private final Rotation rotation;

    private int currentStage;
    private @Nullable Task transitionTask = null;
    private @Nullable Instant transitionStart = null;
    private @Nullable HologramDisplay currentHologram = null;

    private GateTransition transition;
    private boolean updating = false;

    private Gate(GateTemplate template, Location origin, Rotation rotation, int currentStage, GateTransition transition) {
        this.template = template;
        this.origin = origin.clone();
        this.block = origin.getBlock();
        this.currentStage = currentStage;
        this.transition = transition;
        this.rotation = rotation;
    }

    public static @NotNull Gate create(@NotNull GateTemplate template, @NotNull Location origin, Rotation rotation, int stage, GateTransition transition) throws GateOverlapException {
        Gate blockGate = new Gate(template, origin, rotation, stage, transition);
        plugin.getGates().registerGate(blockGate);
        blockGate.updateState();
        return blockGate;
    }

    public static @NotNull Gate create(@NotNull GateTemplate template, @NotNull Location origin, Rotation rotation) throws GateOverlapException {
        return create(template, origin, rotation, 0, null);
    }

    public static @NotNull Gate create(@NotNull GateTemplate template, @NotNull Location origin) throws GateOverlapException {
        return create(template, origin, Rotation.NONE);
    }

    public boolean isValid() {
        for (Block block : getBlocks()) {
            Gate gate = plugin.getGate(block.getLocation());
            if (!this.equals(gate)) {
                return false;
            }
        }
        return true;
    }

    public @Nullable GateTransition getState() {
        return transition;
    }

    public boolean isClosed() {
        return currentStage == 0;
    }

    public boolean isOpened() {
        return currentStage >= template.getMaxStage();
    }

    public boolean isUpdating() {
        return updating;
    }

    public boolean matchBlocks() {
        return getCurrentStage().getStructure().isPlaced(origin, rotation);
    }

    public @NotNull GateTemplate getTemplate() {
        return template;
    }

    public @NotNull Location getOrigin() {
        return origin.clone();
    }

    public @NotNull Block getBlock() {
        return block;
    }

    public Rotation getRotation() {
        return rotation;
    }

    public List<Block> getBlocks() {
        return getCurrentStage().getStructure().getOccupiedBlocks(origin, rotation);
    }

    public Set<Block> getAllOccupiedBlocks() {
        return template.getAllOccupiedBlocks(origin, rotation);
    }

    public int getTicksToNextStage() {
        if (transitionStart == null) {
            return 0;
        }
        GateStage stage = getCurrentStage();
        int timePassed = (int) (Duration.between(transitionStart, Instant.now()).toMillis() / 50);
        return stage.getOpeningDelay() - timePassed;
    }

    public int getTicksToOpenStage() {
        int ticksToRegrown = getTicksToNextStage();
        for (int i = currentStage + 1; i < template.getMaxStage(); i++) {
            GateStage stage = template.getStage(i);
            if (stage.getOpeningDelay() != 0) {
                ticksToRegrown += stage.getOpeningDelay();
            }
        }
        return ticksToRegrown;
    }

    public int getCurrentStageId() {
        return currentStage;
    }

    public @NotNull GateStage getCurrentStage() {
        return template.getStage(currentStage);
    }

    private void setCurrentStage(int newStage) {
        if (newStage < 0 || newStage > template.getMaxStage()) {
            return;
        }

        if (!isValid()) {
            return;
        }

        updating = true;
        cancelTransition();

        BlockStructure oldStructure = getCurrentStage().getStructure();
        BlockStructure newStructure = template.getStage(newStage).getStructure();
        oldStructure.subtract(newStructure, origin, rotation);

        currentStage = newStage;
        updateState();
    }

    public @Nullable HologramDisplay getCurrentHologram() {
        return currentHologram;
    }

    public void cancelTransition() {
        if (transitionTask != null) {
            if (!transitionTask.isCancelled()) {
                transitionTask.cancel();
            }
            transitionTask = null;
        }
        transitionStart = null;
    }

    public void removeBlocks() {
        GateStage stage = getCurrentStage();
        stage.getStructure().remove(origin, rotation);
    }

    public void open() {
        if (isOpened()) {
            return;
        }

        GateStage currentStage = this.getCurrentStage();
        if (currentStage.getOpeningDelay() == 0) {
            return;
        }

        transition = GateTransition.OPENING;

        int peekStage = this.currentStage + 1;
        GateStage nextStage = template.getStage(peekStage);
        while (nextStage.getOpeningDelay() == 0) {
            if (peekStage >= template.getMaxStage()) {
                break;
            }
            peekStage++;
            nextStage = template.getStage(peekStage);
        }

        setCurrentStage(peekStage);
    }

    public void close() {
        if (isClosed()) {
            return;
        }

        transition = GateTransition.CLOSING;
        int peekStage = currentStage - 1;
        GateStage previousStage = template.getStage(peekStage);
        while (previousStage.getClosingDelay() == 0) {
            if (peekStage <= 0) {
                break;
            }
            peekStage--;
            previousStage = template.getStage(peekStage);
        }

        setCurrentStage(peekStage);
    }

    private void updateState() {
        GateStage stage = getCurrentStage();

        updating = false;
        if (currentHologram != null) {
            currentHologram.destroy();
            currentHologram = null;
        }

        stage.getStructure().place(origin, rotation);

        Function transitionFunction = stage.getTransitionFunction();
        if (transitionFunction != null) {
            transitionFunction.run(block);
        }

        if (transition == null) {
            return;
        }
        boolean opening = transition.isOpening();

        Function openOrCloseFunction = opening ? stage.getOpeningFunction() : stage.getClosingFunction();
        if (openOrCloseFunction != null) {
            openOrCloseFunction.run(block);
        }

        Hologram hologram = opening ? stage.getOpeningHologram() : stage.getClosingHologram();
        if (hologram != null) {
            Location offsetLocation = origin.clone().add(0.5, 0.5, 0.5);
            currentHologram = hologram.spawn(offsetLocation, rotation, List.of(this));
        }

        transitionStart = Instant.now();
        int delay = opening ? stage.getOpeningDelay() : stage.getClosingDelay();
        transitionTask = plugin.getScheduler().runTaskLater(delay, () -> {
            transitionTask = null;
            transition = null;
            GateTransitionEvent transitionEvent = new GateTransitionEvent(origin, block, opening);
            Server.callEvent(transitionEvent);
            if (!transitionEvent.isCancelled()) {
                if (opening) {
                    open();
                } else {
                    close();
                }
            }
        });
    }

    @Override
    public String toString() {
        return "BlockGate{" +
                "gate=" + template.getName() +
                ", location=" + origin +
                ", currentStage=" + currentStage +
                '}';
    }

    @Override
    public @NotNull Placeholder[] getPlaceholders() {
        GateStage stage = getCurrentStage();
        int ticksToNextStage = getTicksToNextStage();
        int ticksToRegrownStage = getTicksToOpenStage();

        return new Placeholder[] {
                Placeholder.of("{gate}", template.getName()),
                Placeholder.of("{gate_stage}", currentStage),
                Placeholder.of("{gate_stages}", template.getMaxStage()),
                Placeholder.of("{gate_state}", transition != null ? transition.toString().toLowerCase() : "none"),
                Placeholder.of("{gate_rotation}", rotation.toString().toLowerCase()),
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
