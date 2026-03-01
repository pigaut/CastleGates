package io.github.pigaut.castlegates.config;

import io.github.pigaut.castlegates.*;
import io.github.pigaut.castlegates.action.*;
import io.github.pigaut.castlegates.gate.template.*;
import io.github.pigaut.castlegates.hook.auraskill.*;
import io.github.pigaut.castlegates.hook.mcmmo.*;
import io.github.pigaut.voxel.config.*;
import io.github.pigaut.voxel.core.function.action.*;
import io.github.pigaut.voxel.core.function.condition.config.*;
import org.jetbrains.annotations.*;

import static io.github.pigaut.yaml.configurator.load.ConfigLoader.Line;

public class CastleGatesConfigurator extends PluginConfigurator {

    public CastleGatesConfigurator(@NotNull CastleGatesPlugin plugin) {
        super(plugin);

        addLoader(GateTemplate.class, new GateLoader(plugin));

        final ConditionLoader conditions = getConditionLoader();
        final ActionLoader actions = getActionLoader();

        // Gate Actions
        actions.addLoader("OPEN_GATE", (Line<Action>) line ->
                new GateOpenAction());

        actions.addLoader("CLOSE_GATE", (Line<Action>) line ->
                new GateCloseAction());

        AuraSkillsHook.addConditions(this);
        AuraSkillsHook.addActions(this);

        McMMOHook.addConditions(this);
        McMMOHook.addActions(this);

    }

}
