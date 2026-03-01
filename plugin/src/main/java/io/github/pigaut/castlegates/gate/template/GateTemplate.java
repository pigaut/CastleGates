package io.github.pigaut.castlegates.gate.template;

import io.github.pigaut.castlegates.gate.*;
import io.github.pigaut.castlegates.util.*;
import io.github.pigaut.voxel.bukkit.*;
import io.github.pigaut.voxel.bukkit.Rotation;
import io.github.pigaut.voxel.placeholder.*;
import io.github.pigaut.voxel.plugin.manager.*;
import org.bukkit.*;
import org.bukkit.block.*;
import org.bukkit.block.structure.*;
import org.bukkit.inventory.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class GateTemplate implements Identifiable, PlaceholderSupplier {

    private final String name;
    private final String group;
    private final List<GateStage> stages;
    private final boolean multiBlock;
    private Material itemType = Material.TERRACOTTA;

    public GateTemplate(String name, @Nullable String group, List<GateStage> stages) {
        this.name = name;
        this.group = group;
        this.stages = stages;
        boolean multiBlock = false;
        for (GateStage stage : stages) {
            if (stage.getStructure().hasMultipleBlocks()) {
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

    public List<GateStage> getStages() {
        return new ArrayList<>(stages);
    }

    public GateStage getStage(int stage) {
        return stages.get(stage);
    }

    public GateStage getLastStage() {
        return stages.get(getMaxStage());
    }

    public int indexOfStage(GateStage stage) {
        final int index = stages.indexOf(stage);
        if (index == -1) {
            throw new IllegalArgumentException("Gate does not contain that stage");
        }
        return index;
    }

    public int getStageFromStructure(Location origin, Rotation rotation) {
        int currentStage = getMaxStage();
        for (int i = getMaxStage(); i >= 0; i--) {
            final GateStage stage = getStage(i);
            if (stage.getStructure().isPlaced(origin, rotation)) {
                currentStage = i;
                break;
            }
        }
        return currentStage;
    }

    public Set<Block> getAllOccupiedBlocks(Location location, Rotation rotation) {
        Set<Block> blocks = new HashSet<>();
        for (GateStage stage : stages) {
            blocks.addAll(stage.getStructure().getOccupiedBlocks(location, rotation));
        }
        return blocks;
    }

    public Placeholder[] getPlaceholders() {
        return new Placeholder[]{
                Placeholder.of("{gate}", name),
                Placeholder.of("{gate_stages}", stages),
        };
    }

    @Override
    public String toString() {
        return "GateTemplate{" +
                "name='" + name + '\'' +
                ", group='" + group + '\'' +
                ", stages=" + stages +
                ", itemType=" + itemType +
                '}';
    }

}
