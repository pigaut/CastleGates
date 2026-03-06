package io.github.pigaut.orestack.generator;

import io.github.pigaut.orestack.*;
import io.github.pigaut.orestack.api.event.*;
import io.github.pigaut.orestack.generator.stage.*;
import io.github.pigaut.orestack.generator.template.*;
import io.github.pigaut.voxel.bukkit.Rotation;
import io.github.pigaut.voxel.core.hologram.*;
import io.github.pigaut.voxel.core.structure.*;
import io.github.pigaut.voxel.core.structure.Structure;
import io.github.pigaut.voxel.placeholder.*;
import io.github.pigaut.voxel.plugin.task.*;
import io.github.pigaut.yaml.util.*;
import org.bukkit.*;
import org.bukkit.block.*;
import org.jetbrains.annotations.*;

import java.time.*;
import java.util.*;

public class GeneratorState implements PlaceholderSupplier {
    
    private final Generator generator;

    private int currentStage = 0;
    private Structure structure;
    private @Nullable Task growthTask = null;
    private @Nullable Instant growthStart = null;
    private @Nullable HologramDisplay hologram = null;
    private @Nullable Double health;

    public GeneratorState(@NotNull Generator generator) {
        this.generator = generator;
    }

    public boolean isDepleted() {
        return currentStage <= 0;
    }

    public boolean isFullyGrown() {
        return currentStage >= generator.getTemplate().getMaxStage();
    }

    public int getCurrentStage() {
        return currentStage;
    }

    public @NotNull Structure getStructure() {
        return structure;
    }

    public @Nullable Double getHealth() {
        return health;
    }

    public void setHealth(@Nullable Double health) {
        Preconditions.checkArgument(health == null || health >= 1, "Health cannot be less than 1");
        this.health = health;
    }

    public int getTicksToNextStage() {
        if (growthStart == null) {
            return 0;
        }
        GeneratorStage stage = generator.getTemplate().getStage(currentStage);
        int timePassed = (int) (Duration.between(growthStart, Instant.now()).toMillis() / 50);
        return stage.getGrowthTime() - timePassed;
    }

    public int getTicksToRegrownStage() {
        GeneratorTemplate template = generator.getTemplate();
        int ticksToRegrown = getTicksToNextStage();
        for (int i = currentStage + 1; i < template.getMaxStage(); i++) {
            GeneratorStage stage = template.getStage(i);
            if (stage.getGrowthChance() == null && stage.getGrowthTime() != 0) {
                ticksToRegrown += stage.getGrowthTime();
            }
        }
        return ticksToRegrown;
    }

    public @Nullable Task getGrowthTask() {
        return growthTask;
    }

    public void setGrowthTask(@Nullable Task growthTask) {
        this.growthTask = growthTask;
    }

    public void cancelGrowth() {
        if (growthTask != null) {
            if (!growthTask.isCancelled()) {
                growthTask.cancel();
            }
            growthTask = null;
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

    public @Nullable HologramDisplay getHologram() {
        return hologram;
    }

    public void setStage(int newStage) {
        if (newStage < 0 || newStage > generator.getMaxStage()) {
            return;
        }

        cancelGrowth();
        currentStage = newStage;

        GeneratorStage stage = generator.getStage(currentStage);
        StructureTemplate newStructure = stage.getStructureTemplate();

        if (structure != null) {
            structure.subtract(newStructure);
        }

        Location origin = generator.getOrigin();
        Rotation rotation = generator.getRotation();

        structure = new Structure(newStructure, origin, rotation);
        structure.place();

        removeHologram();
        Hologram newHologram = stage.getHologram();
        if (newHologram != null) {
            Location offsetLocation = origin.clone().add(0.5, 0.5, 0.5);
            hologram = newHologram.spawn(offsetLocation, generator.getRotation(), List.of(this));
        }

        health = stage.getHealth();

        if (stage.getState() != GrowthState.REGROWN) {
            growthStart = Instant.now();
            GeneratorUtil.startGrowth(generator, origin, stage.getGrowthTime());
        }
    }

    @Override
    public @NotNull Placeholder[] getPlaceholders() {
        GeneratorStage stage = generator.getStage(currentStage);
        int ticksToNextStage = getTicksToNextStage();
        int ticksToRegrownStage = getTicksToRegrownStage();

        Location origin = generator.getOrigin();
        return new Placeholder[] {
                Placeholder.of("{generator}", generator.getName()),
                Placeholder.of("{generator_stage}", currentStage),
                Placeholder.of("{generator_stages}", generator.getMaxStage()),
                Placeholder.of("{generator_state}", stage.getState().toString().toLowerCase()),
                Placeholder.of("{generator_rotation}", generator.getRotation().toString().toLowerCase()),
                Placeholder.of("{generator_world}", origin.getWorld().getName()),
                Placeholder.of("{generator_x}", origin.getBlockX()),
                Placeholder.of("{generator_y}", origin.getBlockY()),
                Placeholder.of("{generator_z}", origin.getBlockZ()),

                Placeholder.of("{stage_timer}", Ticks.formatCompact(ticksToNextStage)),
                Placeholder.of("{stage_timer_full}", Ticks.formatFull(ticksToNextStage)),
                Placeholder.of("{stage_timer_hours}", Ticks.toHours(ticksToNextStage)),
                Placeholder.of("{stage_timer_minutes}", Ticks.toMinutes(ticksToNextStage)),
                Placeholder.of("{stage_timer_seconds}", Ticks.toSeconds(ticksToNextStage)),

                Placeholder.of("{generator_timer}", Ticks.formatCompact(ticksToRegrownStage)),
                Placeholder.of("{generator_timer_full}", Ticks.formatFull(ticksToRegrownStage)),
                Placeholder.of("{generator_timer_hours}", Ticks.toHours(ticksToRegrownStage)),
                Placeholder.of("{generator_timer_minutes}", Ticks.toMinutes(ticksToRegrownStage)),
                Placeholder.of("{generator_timer_seconds}", Ticks.toSeconds(ticksToRegrownStage))
        };
    }

}
