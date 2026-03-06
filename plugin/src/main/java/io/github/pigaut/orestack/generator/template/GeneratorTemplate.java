package io.github.pigaut.orestack.generator.template;

import io.github.pigaut.orestack.generator.stage.*;
import io.github.pigaut.voxel.bukkit.*;
import io.github.pigaut.voxel.bukkit.Rotation;
import io.github.pigaut.voxel.placeholder.*;
import io.github.pigaut.voxel.plugin.manager.*;
import org.bukkit.*;
import org.bukkit.block.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class GeneratorTemplate implements Identifiable, PlaceholderSupplier {

    private final String name;
    private final String group;
    private final List<GeneratorStage> stages;
    private final boolean multiBlock;
    private Material itemType = Material.TERRACOTTA;

    public GeneratorTemplate(String name, @Nullable String group, List<GeneratorStage> stages) {
        this.name = name;
        this.group = group;
        this.stages = stages;
        boolean multiBlock = false;
        for (GeneratorStage stage : stages) {
            if (stage.getStructureTemplate().hasMultipleBlocks()) {
                multiBlock = true;
            }
        }
        this.multiBlock = multiBlock;
    }

    @Override
    public @NotNull String getName() {
        return name;
    }

    @Override
    public @Nullable String getGroup() {
        return group;
    }

    public boolean isMultiBlock() {
        return multiBlock;
    }

    public @NotNull Material getItemType() {
        return itemType;
    }

    public void setItemType(Material itemType) {
        if (MaterialUtil.isAir(itemType)) {
            this.itemType = Material.TERRACOTTA;
        }
        else if (MaterialUtil.isCrop(itemType)) {
            this.itemType = MaterialUtil.getCropSeeds(itemType);
        }
        else {
            this.itemType = itemType;
        }
    }

    public int getMaxStage() {
        return stages.size() - 1;
    }

    public List<GeneratorStage> getStages() {
        return new ArrayList<>(stages);
    }

    public GeneratorStage getStage(int stage) {
        return stages.get(stage);
    }

    public GeneratorStage getLastStage() {
        return stages.get(getMaxStage());
    }

    public int indexOfStage(GeneratorStage stage) {
        final int index = stages.indexOf(stage);
        if (index == -1) {
            throw new IllegalArgumentException("Generator does not contain that stage");
        }
        return index;
    }

    public int getStageFromStructure(Location origin, Rotation rotation) {
        int currentStage = getMaxStage();
        for (int i = getMaxStage(); i >= 0; i--) {
            final GeneratorStage stage = getStage(i);
            if (stage.getStructureTemplate().isPlaced(origin, rotation)) {
                currentStage = i;
                break;
            }
        }
        return currentStage;
    }

    public Set<Block> getAllOccupiedBlocks(Location location, Rotation rotation) {
        Set<Block> blocks = new HashSet<>();
        for (GeneratorStage stage : stages) {
            blocks.addAll(stage.getStructureTemplate().getOccupiedBlocks(location, rotation));
        }
        return blocks;
    }

    public Placeholder[] getPlaceholders() {
        return new Placeholder[]{
                Placeholder.of("{generator}", name),
                Placeholder.of("{generator_stages}", stages),
        };
    }

    @Override
    public String toString() {
        return "GeneratorTemplate{" +
                "name='" + name + '\'' +
                ", group='" + group + '\'' +
                ", stages=" + stages +
                ", itemType=" + itemType +
                '}';
    }

}
