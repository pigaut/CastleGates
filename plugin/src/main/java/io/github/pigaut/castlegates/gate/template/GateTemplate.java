package io.github.pigaut.castlegates.gate.template;

import io.github.pigaut.castlegates.gate.*;
import io.github.pigaut.voxel.bukkit.*;
import io.github.pigaut.voxel.bukkit.Rotation;
import io.github.pigaut.voxel.plugin.manager.*;
import org.bukkit.*;
import org.bukkit.block.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class GateTemplate implements Identifiable {

    private final String name;
    private final String group;
    private final List<GatePhase> phases;
    private final boolean multiBlock;
    private final @Nullable Double maxHealth;
    private Material itemType;

    public GateTemplate(String name, @Nullable String group, List<GatePhase> phases,
                        boolean multiBlock, @Nullable Double maxHealth, Material itemType) {
        this.name = name;
        this.group = group;
        this.phases = phases;
        this.maxHealth = maxHealth;
        this.multiBlock = multiBlock;
        setItemType(itemType);
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

    public boolean hasHealth() {
        return maxHealth != null;
    }

    public @Nullable Double getMaxHealth() {
        return maxHealth;
    }

    public @NotNull Material getItemType() {
        return itemType;
    }

    public void setItemType(Material itemType) {
        if (MaterialUtil.isAir(itemType)) {
            this.itemType = Material.TERRACOTTA;
        } else if (MaterialUtil.isCrop(itemType)) {
            this.itemType = MaterialUtil.getCropSeeds(itemType);
        } else {
            this.itemType = itemType;
        }
    }

    public int getMaxPhase() {
        return phases.size() - 1;
    }

    public List<GatePhase> getPhases() {
        return new ArrayList<>(phases);
    }

    public GatePhase getPhase(int phase) {
        return phases.get(phase);
    }

    public GatePhase getLastPhase() {
        return phases.get(getMaxPhase());
    }

    public int indexOfPhase(GatePhase phase) {
        final int index = phases.indexOf(phase);
        if (index == -1) {
            throw new IllegalArgumentException("Gate does not contain that phase");
        }
        return index;
    }

    public int getPhaseFromStructure(Location origin, Rotation rotation) {
        int currentPhase = getMaxPhase();
        for (int i = getMaxPhase(); i >= 0; i--) {
            final GatePhase phase = getPhase(i);
            if (phase.getStructureTemplate().isPlaced(origin, rotation)) {
                currentPhase = i;
                break;
            }
        }
        return currentPhase;
    }

    public Set<Block> getOccupiedBlocks(Location location, Rotation rotation) {
        Set<Block> blocks = new HashSet<>();
        for (GatePhase phase : phases) {
            blocks.addAll(phase.getStructureTemplate().getOccupiedBlocks(location, rotation));
        }
        return blocks;
    }

    @Override
    public String toString() {
        return "GateTemplate{" +
                "name='" + name + '\'' +
                ", group='" + group + '\'' +
                ", phases=" + phases +
                ", multiBlock=" + multiBlock +
                ", itemType=" + itemType +
                '}';
    }

}
