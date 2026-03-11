package io.github.pigaut.castlegates.gate;

import io.github.pigaut.castlegates.*;
import io.github.pigaut.castlegates.api.event.*;
import io.github.pigaut.castlegates.gate.stage.*;
import io.github.pigaut.castlegates.gate.state.*;
import io.github.pigaut.castlegates.gate.template.*;
import io.github.pigaut.castlegates.util.*;
import io.github.pigaut.voxel.bukkit.Rotation;
import io.github.pigaut.voxel.core.function.*;
import io.github.pigaut.voxel.core.hologram.*;
import io.github.pigaut.voxel.core.structure.Structure;
import io.github.pigaut.voxel.server.Server;
import org.bukkit.*;
import org.bukkit.block.*;
import org.jetbrains.annotations.*;

import java.time.*;
import java.util.*;

public class Gate {

    private static final CastleGatesPlugin plugin = CastleGatesPlugin.getInstance();

    private final GateTemplate template;
    private final Location origin;
    private final Rotation rotation;

    private final GateState state;

    private Gate(GateTemplate template, Location origin, Rotation rotation) {
        this.template = template;
        this.origin = origin.clone();
        this.rotation = rotation;
        this.state = new GateState(this);
    }

    public static @NotNull Gate create(@NotNull GateTemplate template, @NotNull Location origin, @NotNull Rotation rotation,
                                       int stage, @NotNull GateTransition transition) throws GateOverlapException {
        Gate gate = new Gate(template, origin, rotation);
        plugin.getGates().registerGate(gate);
        GateUtil.init(gate, stage, transition);
        return gate;
    }

    public static @NotNull Gate create(@NotNull GateTemplate template, @NotNull Location origin, @NotNull Rotation rotation) throws GateOverlapException {
        return create(template, origin, rotation, 0, GateTransition.NONE);
    }

    public static @NotNull Gate create(@NotNull GateTemplate template, @NotNull Location origin) throws GateOverlapException {
        return create(template, origin, Rotation.NONE);
    }

    public boolean isValid() {
        Structure structure = state.getStructure();

        for (Block block : structure.getOccupiedBlocks()) {
            Gate gate = plugin.getGate(block.getLocation());
            if (!this.equals(gate)) {
                return false;
            }
        }

        return structure.isPlaced();
    }

    public void remove() {

    }

    public boolean isFullyClosed() {
        return state.getCurrentStage() <= 0;
    }

    public boolean isFullyOpen() {
        return state.getCurrentStage() >= template.getMaxStage();
    }

    public int getMaxStage() {
        return template.getMaxStage();
    }

    public boolean isStage(int stage) {
        return stage >= 0 && stage < template.getMaxStage();
    }

    public int getNextOpeningStage() {
        int currentStage = state.getCurrentStage();
        int stage = currentStage + 1;
        while (stage < getMaxStage()) {
            if (template.getStage(stage).getOpeningDelay() > 0) {
                break;
            }
            stage++;
        }
        return stage;
    }

    public int getNextClosingStage() {
        int currentStage = state.getCurrentStage();
        int stage = currentStage - 1;
        while (stage > 0) {
            if (template.getStage(stage).getClosingDelay() > 0) {
                break;
            }
            stage--;
        }
        return stage;
    }

    public @NotNull GateTemplate getTemplate() {
        return template;
    }

    public @NotNull Location getOrigin() {
        return origin.clone();
    }

    public @NotNull Block getBlock() {
        return origin.getBlock();
    }

    public Rotation getRotation() {
        return rotation;
    }

    public Set<Block> getBlocks() {
        return getStage().getStructureTemplate().getOccupiedBlocks(origin, rotation);
    }

    public Set<Block> getOccupiedBlocks() {
        return template.getAllOccupiedBlocks(origin, rotation);
    }

    public void open() {
        GateUtil.open(this);
    }

    public void close() {
        GateUtil.close(this);
    }

    @Override
    public String toString() {
        return "BlockGate{" +
                "gate=" + template.getName() +
                ", location=" + origin +
                '}';
    }


    public @NotNull GateStage getStage() {
        return template.getStage(state.getCurrentStage());
    }

    public @NotNull GateStage getStage(int stage) {
        return template.getStage(stage);
    }

    public @NotNull String getName() {
        return template.getName();
    }

    public GateState getState() {
        return state;
    }


}
