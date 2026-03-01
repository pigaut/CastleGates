package io.github.pigaut.orestack.config;

import io.github.pigaut.orestack.*;
import io.github.pigaut.orestack.generator.*;
import io.github.pigaut.orestack.generator.template.*;
import io.github.pigaut.voxel.bukkit.*;
import io.github.pigaut.voxel.core.function.*;
import io.github.pigaut.voxel.core.hologram.*;
import io.github.pigaut.voxel.core.structure.*;
import io.github.pigaut.voxel.plugin.manager.*;
import io.github.pigaut.voxel.server.*;
import io.github.pigaut.voxel.server.Server;
import io.github.pigaut.yaml.*;
import io.github.pigaut.yaml.amount.*;
import io.github.pigaut.yaml.configurator.load.*;
import io.github.pigaut.yaml.util.*;
import org.bukkit.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class GeneratorLoader implements ConfigLoader<GeneratorTemplate> {

    private final OrestackPlugin plugin;

    public GeneratorLoader(OrestackPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getErrorDescription() {
        return "invalid generator";
    }

    @Override
    public @NotNull GeneratorTemplate loadFromScalar(ConfigScalar scalar) throws InvalidConfigException {
        String generatorName = scalar.toString();
        GeneratorTemplate generatorTemplate = plugin.getGeneratorTemplate(generatorName);
        if (generatorTemplate == null) {
            throw new InvalidConfigException(scalar, "Could not find generator with name: '" + generatorName + "'");
        }
        return generatorTemplate;
    }

    @Override
    public @NotNull GeneratorTemplate loadFromSequence(@NotNull ConfigSequence sequence) throws InvalidConfigException {
        if (!(sequence instanceof ConfigRoot root)) {
            throw new InvalidConfigException(sequence, "Generator can only be loaded from a root configuration sequence");
        }

        String name = root.getName();
        String group = Group.byFile(root.getFile(), "generators", true);

        List<GeneratorStage> generatorStages = new ArrayList<>();
        GeneratorTemplate generator = new GeneratorTemplate(name, group, generatorStages);
        for (ConfigSection nestedSection : sequence.getNestedSections()) {
            generatorStages.add(loadStage(generator, nestedSection));
        }

        if (generatorStages.size() < 2) {
            throw new InvalidConfigException(sequence, "Generator must have at least one depleted and one regrown stage");
        }

        GeneratorStage firstStage = generatorStages.get(0);
        if (firstStage.getState() != GrowthState.DEPLETED) {
            throw new InvalidConfigException(sequence, "The first stage must be depleted");
        }

        if (firstStage.getGrowthFunction() != null) {
            throw new InvalidConfigException(sequence, "The first stage cannot have a growth function");
        }

        if (firstStage.getGrowthTime() == 0) {
            throw new InvalidConfigException(sequence, "The depleted stage must have a growth time set");
        }

        GeneratorStage lastStage = generatorStages.get(generatorStages.size() - 1);
        if (lastStage.getState() != GrowthState.REGROWN) {
            throw new InvalidConfigException(sequence, "The last stage must be regrown");
        }

        boolean firstHarvestableFound = false;
        for (int i = 1; i < generatorStages.size(); i++) {
            final GeneratorStage stage = generatorStages.get(i);
            if (stage.getState() == GrowthState.DEPLETED) {
                throw new InvalidConfigException(sequence, "Only the first stage should be depleted");
            }

            if (!firstHarvestableFound && stage.getState().isHarvestable()) {
                if (stage.getGrowthChance() != null && stage.getGrowthChance() != 1.0) {
                    throw new InvalidConfigException(sequence, "Generator must have at least one unripe/ripe/regrown stage with 100% growth chance");
                }
                firstHarvestableFound = true;
            }
        }

        Material mostCommonMaterial = generator.getLastStage().getStructure().getMostCommonMaterial();
        generator.setItemType(mostCommonMaterial);

        return generator;
    }

    private GeneratorStage loadStage(GeneratorTemplate generator, ConfigSection section) throws InvalidConfigException {
        GrowthState state = section.getRequired("type|state", GrowthState.class);

        BlockStructure structure = section.contains("structure|blocks") ?
                section.getRequired("structure|blocks", BlockStructure.class) :
                section.getRequired(BlockStructure.class);

        List<Material> decorativeBlocks = section.getAllRequired("decorative-blocks", Material.class);

        Boolean defaultDrops = section.getBoolean("default-drops|drops").withDefault(null);
        boolean dropItems = defaultDrops != null ? defaultDrops :
                section.getBoolean("drop-items").withDefault(false);
        boolean dropExp = defaultDrops != null ? defaultDrops :
                section.getBoolean("drop-exp|drop-xp").withDefault(false);

        Amount toolDamage = section.get("tool-damage", Amount.class)
                .withDefault(plugin.getSettings().getDefaultToolDamage());

        Double health = section.getDouble("health")
                .require(Requirements.positive())
                .withDefault(null);

        boolean idle = section.getBoolean("idle").withDefault(health != null);

        if (health != null && !idle) {
            throw new InvalidConfigException(section, "idle", "Generator Stage with health set must be idle.");
        }

        Boolean harvestOnly = section.getBoolean("harvest-only").withDefault(null);
        int growthTime = section.get("growth|growth-time", Ticks.class)
                .check(harvestOnly == null || !harvestOnly, "Cannot set growth time while harvest-only is true")
                .map(Ticks::getCount)
                .withDefault(0);

        Double chance = section.getDouble("chance|growth-chance").withDefault(null);

        int clickCooldown = section.getInteger("click-cooldown")
                .require(Requirements.min(1))
                .withDefault(plugin.getSettings().getClickCooldown());

        int hitCooldown = section.getInteger("hit-cooldown")
                .require(Requirements.min(1))
                .withDefault(plugin.getSettings().getHitCooldown());

        int harvestCooldown = section.getInteger("harvest-cooldown")
                .require(Requirements.min(1))
                .withDefault(plugin.getSettings().getClickCooldown());

        Hologram hologram = null;
        if (Server.isPluginEnabled("DecentHolograms")) {
            hologram = section.get("hologram", Hologram.class).withDefault(null);
        }

        Function onBreak = section.get("on-break", Function.class).withDefault(null);
        Function onGrowth = section.get("on-growth", Function.class).withDefault(null);
        Function onClick = section.get("on-click", Function.class).withDefault(null);
        Function onHit = section.get("on-hit|on-left-click", Function.class).withDefault(null);
        Function onHarvest = section.get("on-harvest|on-right-click", Function.class).withDefault(null);
        Function onDestroy = section.get("on-destroy", Function.class).withDefault(null);

        return new GeneratorStage(generator, state, structure, decorativeBlocks, dropItems, dropExp,
                toolDamage, idle, growthTime, chance, health, clickCooldown, hitCooldown, harvestCooldown,
                hologram, onBreak, onGrowth, onClick, onHit, onHarvest, onDestroy);
    }

}