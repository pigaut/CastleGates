package io.github.pigaut.castlegates.health;

import io.github.pigaut.yaml.amount.*;
import org.bukkit.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class ToolDamage {

    private final List<Material> blockTypes;
    private final Map<Material, Amount> damageByTool;

    public ToolDamage(List<Material> blockTypes, Map<Material, Amount> damageByTool) {
        this.blockTypes = blockTypes;
        this.damageByTool = damageByTool;
    }

    public boolean test(@NotNull Material toolType, @NotNull Material blockType) {
        return damageByTool.containsKey(toolType) && blockTypes.contains(blockType);
    }

    public Amount getDamage(@NotNull Material toolType) {
        return damageByTool.get(toolType);
    }

    public List<Material> getBlockTypes() {
        return new ArrayList<>(blockTypes);
    }

    @Override
    public String toString() {
        return "ToolDamage{" +
                "blockTypes=" + blockTypes +
                ", damageByTool=" + damageByTool +
                '}';
    }

}
