package io.github.pigaut.orestack.generator;

import io.github.pigaut.orestack.*;
import io.github.pigaut.orestack.api.event.*;
import io.github.pigaut.orestack.generator.stage.*;
import io.github.pigaut.orestack.generator.template.*;
import io.github.pigaut.orestack.util.*;
import io.github.pigaut.voxel.bukkit.Rotation;
import io.github.pigaut.voxel.core.function.*;
import io.github.pigaut.voxel.core.hologram.*;
import io.github.pigaut.voxel.core.structure.*;
import io.github.pigaut.voxel.core.structure.Structure;
import io.github.pigaut.voxel.player.*;
import io.github.pigaut.voxel.util.*;
import io.github.pigaut.yaml.util.*;
import org.bukkit.*;
import org.bukkit.block.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class Generator {

    private static final OrestackPlugin plugin = OrestackPlugin.getInstance();

    private final GeneratorTemplate template;
    private final Location origin;
    private final Rotation rotation;

    private final GeneratorState state;

    private Generator(GeneratorTemplate template, Location origin, Rotation rotation) {
        this.template = template;
        this.origin = origin.clone();
        this.rotation = rotation;
        this.state = new GeneratorState(this);
    }

    public static @NotNull Generator create(@NotNull GeneratorTemplate template, @NotNull Location origin, @NotNull Rotation rotation, int stage) throws GeneratorOverlapException {
        Generator generator = new Generator(template, origin, rotation);
        plugin.getGenerators().registerGenerator(generator);
        generator.setStage(stage);
        return generator;
    }

    public static @NotNull Generator create(@NotNull GeneratorTemplate template, @NotNull Location origin, @NotNull Rotation rotation) throws GeneratorOverlapException {
        return create(template, origin, rotation, template.getMaxStage());
    }

    public static @NotNull Generator create(@NotNull GeneratorTemplate template, @NotNull Location origin) throws GeneratorOverlapException {
        return create(template, origin, Rotation.NONE);
    }

    public @NotNull String getName() {
        return template.getName();
    }

    public @NotNull GeneratorStage getStage(int stage) {
        return template.getStage(stage);
    }

    public int getMaxStage() {
        return template.getMaxStage();
    }

    public boolean isDepleted() {
        return state.isDepleted();
    }

    public boolean isFullyGrown() {
        return state.isFullyGrown();
    }

    public boolean isValid() {
        Structure structure = state.getStructure();

        for (Block block : structure.getOccupiedBlocks()) {
            Generator generator = plugin.getGenerator(block.getLocation());
            if (!this.equals(generator)) {
                return false;
            }
        }

        return structure.isPlaced();
    }

    public void remove() {
        state.cancelGrowth();
        state.removeBlocks();
        state.removeHologram();

        if (plugin.getSettings().isKeepBlocksOnRemove()) {
            template.getLastStage().getStructureTemplate().place(origin, rotation);
        }

        plugin.getGenerators().unregisterGenerator(this);
    }

    public @NotNull GeneratorTemplate getTemplate() {
        return template;
    }

    public @NotNull Location getOrigin() {
        return origin.clone();
    }

    public @NotNull Block getBlock() {
        return origin.getBlock();
    }

    public @Nullable Double getHealth() {
        return state.getHealth();
    }

    public void setHealth(double newHealth) {
        state.setHealth(newHealth);
    }

    public void damage(@NotNull PlayerState damageDealer, double amount) {
        GeneratorUtil.damage(this, damageDealer, amount);
    }

    public @NotNull Rotation getRotation() {
        return rotation;
    }

    public List<Block> getBlocks() {
        GeneratorStage stage = getStage();
        return stage.getStructureTemplate().getOccupiedBlocks(origin, rotation);
    }

    public Set<Block> getAllOccupiedBlocks() {
        return template.getAllOccupiedBlocks(origin, rotation);
    }

    public @NotNull GeneratorState getState() {
        return state;
    }

    public @NotNull GeneratorStage getStage() {
        return template.getStage(state.getCurrentStage());
    }

    public void setStage(int newStage) {
        state.setStage(newStage);
    }

    public void grow() {
        if (isFullyGrown()) {
            return;
        }

        GeneratorStage currentStage = this.getStage();
        if (currentStage.getGrowthTime() == 0) {
            return;
        }

        int peekStage = state.getCurrentStage() + 1;
        GeneratorStage nextStage = template.getStage(peekStage);
        while (nextStage.getGrowthTime() == 0) {
            if (peekStage >= template.getMaxStage()) {
                break;
            }
            peekStage++;
            nextStage = template.getStage(peekStage);
        }

        boolean shouldGrow = false;
        while (!shouldGrow) {
            Double growthChance = nextStage.getGrowthChance();
            if (growthChance != null && !Probability.test(growthChance)) {
                if (currentStage.getState().isHarvestable()) {
                    return;
                }
                peekStage++;
                nextStage = template.getStage(peekStage);
                continue;
            }
            shouldGrow = true;
        }

        Function growthFunction = nextStage.getGrowthFunction();
        if (growthFunction != null) {
            growthFunction.run(getBlock());
        }

        state.setStage(peekStage);
    }

    public void harvest() {
        if (isDepleted()) {
            return;
        }

        int peekStage = state.getCurrentStage();
        GeneratorStage previousStage;
        do {
            peekStage--;
            previousStage = template.getStage(peekStage);
        }
        while (previousStage.getState().isTransitional());

        state.setStage(peekStage);
    }

    @Override
    public String toString() {
        return "BlockGenerator{" +
                "generator=" + template.getName() +
                ", location=" + origin +
                '}';
    }


}
