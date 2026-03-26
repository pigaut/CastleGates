package io.github.pigaut.castlegates.settings;

import io.github.pigaut.castlegates.core.*;
import io.github.pigaut.castlegates.health.*;
import io.github.pigaut.voxel.bukkit.*;
import io.github.pigaut.voxel.plugin.*;
import io.github.pigaut.yaml.*;
import io.github.pigaut.yaml.amount.*;
import org.bukkit.*;
import org.bukkit.inventory.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class CastleGatesSettings extends Settings {

    // Generic settings
    private boolean keepBlocksOnRemove;
    private boolean restoreOriginalBlocksOnRemove;
    private ItemStack gateTool;

    // Gate
    private int clickCooldown;

    // Gate health settings
    private Amount defaultDamage;
    private boolean efficiencyDamage;
    private boolean reducedCooldownDamage;
    private List<ToolDamage> damageByTool;
    private List<HealthBar> healthBars;

    public CastleGatesSettings(EnhancedPlugin plugin) {
        super(plugin);
    }

    @Override
    public @NotNull List<ConfigException> loadConfigurationData() {
        List<ConfigException> errors = super.loadConfigurationData();

        ConfigSection config = plugin.getConfiguration();

        keepBlocksOnRemove = config.getBoolean("keep-blocks-on-remove")
                .withDefaultOrElse(false, errors::add);

        restoreOriginalBlocksOnRemove = config.getBoolean("restore-original-blocks-on-remove")
                .withDefaultOrElse(true, errors::add);

        gateTool = config.get("gate-tool", ItemStack.class)
                .require(ItemUtil::isNotAir, "Item type cannot be air")
                .withDefaultOrElse(GateTool.getItemTemplate(), errors::add);

        // Gate settings

        clickCooldown = config.getInteger("click-cooldown")
                .require(Requirements.positive())
                .withDefaultOrElse(4, errors::add);

        // Generator health settings
        defaultDamage = config.get("default-damage", Amount.class)
                .withDefaultOrElse(Amount.ONE, errors::add);

        efficiencyDamage = config.getBoolean("efficiency-damage")
                .withDefaultOrElse(true, errors::add);

        reducedCooldownDamage = config.getBoolean("reduced-cooldown-damage")
                .withDefaultOrElse(true, errors::add);

        damageByTool = config.getList("damage-by-tool-type", ToolDamage.class)
                .withDefaultOrElse(List.of(), errors::add);

        healthBars = config.getAll("health-bars", HealthBar.class)
                .withDefaultOrElse(List.of(), errors::add);

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

    public boolean isRestoreBlocksOnRemove() {
        return restoreOriginalBlocksOnRemove;
    }

    public boolean isEfficiencyDamage() {
        return efficiencyDamage;
    }

    public boolean isReducedCooldownDamage() {
        return reducedCooldownDamage;
    }

    @NotNull
    public Amount getToolDamage(@NotNull Material toolType, @NotNull Material blockType) {
        for (ToolDamage toolDamage : damageByTool) {
            if (toolDamage.test(toolType, blockType)) {
                return toolDamage.getDamage(toolType);
            }
        }
        return defaultDamage;
    }

    @NotNull
    public List<HealthBar> getHealthBars() {
        return new ArrayList<>(healthBars);
    }

}
