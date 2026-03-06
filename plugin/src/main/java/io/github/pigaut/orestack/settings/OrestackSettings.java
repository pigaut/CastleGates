package io.github.pigaut.orestack.settings;

import io.github.pigaut.orestack.damage.*;
import io.github.pigaut.orestack.util.*;
import io.github.pigaut.voxel.bukkit.*;
import io.github.pigaut.voxel.plugin.*;
import io.github.pigaut.voxel.util.reflection.*;
import io.github.pigaut.yaml.*;
import io.github.pigaut.yaml.amount.*;
import io.github.pigaut.yaml.node.scalar.*;
import org.bukkit.*;
import org.bukkit.enchantments.*;
import org.bukkit.inventory.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class OrestackSettings extends Settings {

    private final boolean spigotEnchants = Reflect.onClass(Enchantment.class)
            .matchMethod("getKeyOrNull");

    // Generic settings
    private boolean keepBlocksOnRemove;
    private boolean restoreOriginalBlocksOnRemove;
    private ItemStack generatorTool;

    // VeinMiner settings
    private boolean veinMiner;
    private List<String> veinMinerAliases;
    private Map<Integer, Integer> veinSizeByLevel;

    // Generator
    private Amount defaultToolDamage;
    private int clickCooldown;
    private int hitCooldown;
    private int harvestCooldown;

    // Generator health settings
    private Amount defaultDamage;
    private boolean efficiencyDamage;
    private boolean reducedCooldownDamage;
    private List<ToolDamage> damageByTool;

    public OrestackSettings(EnhancedPlugin plugin) {
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

        generatorTool = config.get("generator-tool", ItemStack.class)
                .require(ItemUtil::isNotAir, "Item type cannot be air")
                .withDefaultOrElse(GeneratorTool.getItemTemplate(), errors::add);

        // Generator settings
        defaultToolDamage = config.get("default-tool-damage", Amount.class)
                .withDefaultOrElse(Amount.ONE, errors::add);

        clickCooldown = config.getInteger("click-cooldown")
                .require(Requirements.positive())
                .withDefaultOrElse(4, errors::add);

        hitCooldown = config.getInteger("hit-cooldown")
                .require(Requirements.positive())
                .withDefaultOrElse(4, errors::add);

        harvestCooldown = config.getInteger("harvest-cooldown")
                .require(Requirements.positive())
                .withDefaultOrElse(4, errors::add);

        // Vein miner settings
        veinMiner = config.getBoolean("vein-miner")
                .withDefaultOrElse(false, errors::add);

        veinMinerAliases = config.getStringList("vein-miner-aliases")
                .withDefaultOrElse(List.of("veinminer", "vein-miner", "vein_miner"), errors::add);

        veinSizeByLevel = new HashMap<>();
        for (KeyedScalar scalar : config.getSectionOrCreate("vein-size-by-level").getNestedScalars()) {
            Integer veinSize = scalar.toInteger()
                    .require(Requirements.positive())
                    .withDefaultOrElse(null, errors::add);

            Integer enchantLevel = scalar.getIntegerKey()
                    .require(Requirements.positive())
                    .withDefaultOrElse(null, errors::add);

            if (veinSize != null && enchantLevel != null) {
                veinSizeByLevel.put(enchantLevel, veinSize);
            }
        }

        // Generator health options
        defaultDamage = config.get("default-damage", Amount.class)
                .withDefaultOrElse(Amount.ONE, errors::add);

        efficiencyDamage = config.getBoolean("efficiency-damage")
                .withDefaultOrElse(true, errors::add);

        reducedCooldownDamage = config.getBoolean("reduced-cooldown-damage")
                .withDefaultOrElse(true, errors::add);

        damageByTool = config.getList("damage-by-tool-type", ToolDamage.class)
                .withDefaultOrElse(List.of(), errors::add);

        return errors;
    }

    public boolean isKeepBlocksOnRemove() {
        return keepBlocksOnRemove;
    }

    public boolean isRestoreBlocksOnRemove() {
        return restoreOriginalBlocksOnRemove;
    }

    public @NotNull ItemStack getGeneratorTool() {
        return generatorTool.clone();
    }

    public boolean isEfficiencyDamage() {
        return efficiencyDamage;
    }

    public boolean isDefaultToolDamage() {
        return !defaultToolDamage.match(0);
    }

    public Amount getDefaultToolDamage() {
        return defaultToolDamage;
    }

    public int getHitCooldown() {
        return hitCooldown;
    }

    public int getClickCooldown() {
        return clickCooldown;
    }

    public int getHarvestCooldown() {
        return harvestCooldown;
    }

    public boolean isReducedCooldownDamage() {
        return reducedCooldownDamage;
    }

    public @NotNull Amount getToolDamage(@NotNull Material toolType, @NotNull Material blockType) {
        for (ToolDamage toolDamage : damageByTool) {
            if (toolDamage.test(toolType, blockType)) {
                return toolDamage.getDamage(toolType);
            }
        }
        return defaultDamage;
    }

    public boolean isVeinMiner() {
        return veinMiner;
    }

    public int getToolMaxVeinSize(@NotNull ItemStack tool) {
        if (!tool.hasItemMeta()) {
            return 1;
        }

        for (Map.Entry<Enchantment, Integer> enchantToLevel : tool.getItemMeta().getEnchants().entrySet()) {
            Enchantment enchant = enchantToLevel.getKey();

            NamespacedKey enchantKey;
            if (spigotEnchants) {
                enchantKey = enchant.getKeyOrNull();
            } else {
                enchantKey = enchant.getKey();
            }

            if (enchantKey == null) {
                continue;
            }

            if (!veinMinerAliases.contains(enchantKey.getKey())) {
                continue;
            }

            return veinSizeByLevel.getOrDefault(enchantToLevel.getValue(), 1);
        }

        return 1;
    }

}
