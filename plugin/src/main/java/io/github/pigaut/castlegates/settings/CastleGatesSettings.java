package io.github.pigaut.castlegates.settings;

import io.github.pigaut.castlegates.util.*;
import io.github.pigaut.voxel.bukkit.*;
import io.github.pigaut.voxel.plugin.*;
import io.github.pigaut.voxel.util.reflection.*;
import io.github.pigaut.yaml.*;
import io.github.pigaut.yaml.amount.*;
import io.github.pigaut.yaml.node.scalar.*;
import io.github.pigaut.yaml.util.*;
import org.bukkit.*;
import org.bukkit.enchantments.*;
import org.bukkit.inventory.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class CastleGatesSettings extends Settings {

    // Generic settings
    private boolean keepBlocksOnRemove;
    private ItemStack gateTool;

    // Gate
    private int clickCooldown;

    public CastleGatesSettings(EnhancedPlugin plugin) {
        super(plugin);
    }

    @Override
    public @NotNull List<ConfigException> loadConfigurationData() {
        List<ConfigException> errors = super.loadConfigurationData();

        ConfigSection config = plugin.getConfiguration();

        keepBlocksOnRemove = config.getBoolean("keep-blocks-on-remove")
                .withDefaultOrElse(false, errors::add);

        gateTool = config.get("gate-tool", ItemStack.class)
                .require(ItemUtil::isNotAir, "Item type cannot be air")
                .withDefaultOrElse(GateTool.getItemTemplate(), errors::add);

        // Gate settings

        clickCooldown = config.getInteger("click-cooldown")
                .require(Requirements.positive())
                .withDefaultOrElse(4, errors::add);

        return errors;
    }

    public boolean isKeepBlocksOnRemove() {
        return keepBlocksOnRemove;
    }

    public @NotNull ItemStack getGateTool() {
        return gateTool.clone();
    }

    public int getClickCooldown() {
        return clickCooldown;
    }

}
