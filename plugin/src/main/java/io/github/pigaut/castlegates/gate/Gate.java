package io.github.pigaut.castlegates.gate;

import io.github.pigaut.castlegates.*;
import io.github.pigaut.castlegates.core.*;
import io.github.pigaut.castlegates.gate.state.*;
import io.github.pigaut.castlegates.gate.template.*;
import io.github.pigaut.voxel.bukkit.Rotation;
import io.github.pigaut.voxel.data.structure.Structure;
import org.bukkit.*;
import org.bukkit.block.*;
import org.jetbrains.annotations.*;

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
                                       int phase, @NotNull GateTransition transition) throws GateOverlapException {
        Gate gate = new Gate(template, origin, rotation);
        plugin.getGates().registerGate(gate);
        GateUtil.init(gate, phase, transition);
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
        return state.getCurrentPhase() <= 0;
    }

    public boolean isFullyOpen() {
        return state.getCurrentPhase() >= template.getMaxPhase();
    }

    public int getMaxPhase() {
        return template.getMaxPhase();
    }

    public boolean isValidPhase(int phase) {
        return phase >= 0 && phase <= template.getMaxPhase();
    }

    public int getNextOpeningPhase() {
        int currentPhase = state.getCurrentPhase();
        int phase = currentPhase + 1;
        while (phase < getMaxPhase()) {
            if (template.getPhase(phase).getOpeningDelay() > 0) {
                break;
            }
            phase++;
        }
        return phase;
    }

    public int getNextClosingPhase() {
        int currentPhase = state.getCurrentPhase();
        int phase = currentPhase - 1;
        while (phase > 0) {
            if (template.getPhase(phase).getClosingDelay() > 0) {
                break;
            }
            phase--;
        }
        return phase;
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
        return getPhase().getStructureTemplate().getOccupiedBlocks(origin, rotation);
    }

    public Set<Block> getOccupiedBlocks() {
        return template.getOccupiedBlocks(origin, rotation);
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


    public @NotNull GatePhase getPhase() {
        return template.getPhase(state.getCurrentPhase());
    }

    public @NotNull GatePhase getPhase(int phase) {
        return template.getPhase(phase);
    }

    public @NotNull String getName() {
        return template.getName();
    }

    public GateState getState() {
        return state;
    }


    public @Nullable Double getHealth() {
        return state.getPhaseHealth();
    }


    public @Nullable Double getTotalHealth() {
        return state.getHealth();
    }

}
