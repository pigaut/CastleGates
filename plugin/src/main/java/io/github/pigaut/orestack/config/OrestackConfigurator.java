package io.github.pigaut.orestack.config;

import io.github.pigaut.orestack.*;
import io.github.pigaut.orestack.action.*;
import io.github.pigaut.orestack.damage.*;
import io.github.pigaut.orestack.generator.template.*;
import io.github.pigaut.orestack.hook.auraskill.*;
import io.github.pigaut.orestack.hook.mcmmo.*;
import io.github.pigaut.voxel.config.*;
import io.github.pigaut.voxel.core.function.action.*;
import io.github.pigaut.voxel.core.function.condition.config.*;
import io.github.pigaut.yaml.*;
import io.github.pigaut.yaml.amount.*;
import org.jetbrains.annotations.*;

import static io.github.pigaut.yaml.configurator.load.ConfigLoader.Line;

public class OrestackConfigurator extends PluginConfigurator {

    public OrestackConfigurator(@NotNull OrestackPlugin plugin) {
        super(plugin);

        addLoader(GeneratorTemplate.class, new GeneratorLoader(plugin));
        addLoader(ToolDamage.class, new ToolDamageLoader());

        final ConditionLoader conditions = getConditionLoader();
        final ActionLoader actions = getActionLoader();

        // Generator Actions
        actions.addLoader("KEEP_STAGE", (Line<Action>) line ->
                new GeneratorKeepStageAction());

        actions.addLoader("NEXT_STAGE", (Line<Action>) line ->
                new GeneratorGrowAction());

        actions.addLoader("GROW_GENERATOR", (Line<Action>) line ->
                new GeneratorGrowAction());

        actions.addLoader("PREVIOUS_STAGE", (Line<Action>) line ->
                new GeneratorHarvestAction());

        actions.addLoader("HARVEST_GENERATOR", (Line<Action>) line ->
                new GeneratorHarvestAction());

        actions.addLoader("SET_STAGE", (Line<Action>) line ->
                new GeneratorSetStageAction(line.getRequiredInteger(1) - 1));

        actions.addLoader("DAMAGE_GENERATOR", (Line<Action>) line -> {
            ConfigOptional<Amount> amount = line.get(1, Amount.class);
            if (!amount.existsInConfig()) {
                return new DamageGeneratorWithTool();
            }
            return new DamageGeneratorAction(amount.withDefault(Amount.ONE));
        });

        actions.addLoader("DAMAGE_GENERATOR_WITH_TOOL", (Line<Action>) line ->
                new DamageGeneratorWithTool());

        AuraSkillsHook.addConditions(this);
        AuraSkillsHook.addActions(this);

        McMMOHook.addConditions(this);
        McMMOHook.addActions(this);

    }

}
