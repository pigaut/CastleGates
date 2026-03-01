package io.github.pigaut.orestack.damage;

import io.github.pigaut.voxel.config.deserializer.*;
import io.github.pigaut.yaml.*;
import io.github.pigaut.yaml.amount.*;
import io.github.pigaut.yaml.configurator.load.*;
import org.bukkit.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class ToolDamageLoader implements ConfigLoader<ToolDamage> {

    private final MaterialDeserializer materialDeserializer = new MaterialDeserializer();

    @Override
    public @Nullable String getErrorDescription() {
        return "invalid tool damage";
    }

    @Override
    public @NotNull ToolDamage loadFromSection(@NotNull ConfigSection section) throws InvalidConfigException {
        List<Material> blockTypes = section.getAllRequired("blocks", Material.class);
        Map<Material, Amount> damageByTool = new HashMap<>();

        ConfigSection toolSection = section.getSectionOrCreate("tools");
        for (String key : toolSection.getKeys()) {
           Material material = materialDeserializer.loadFromKey(toolSection, key);
           Amount amount = toolSection.getRequired(key, Amount.class);
           damageByTool.put(material, amount);
        }

        return new ToolDamage(blockTypes, damageByTool);
    }

}
