package io.github.pigaut.castlegates.config;

import io.github.pigaut.castlegates.*;
import io.github.pigaut.castlegates.core.action.*;
import io.github.pigaut.castlegates.gate.*;
import io.github.pigaut.castlegates.gate.template.*;
import io.github.pigaut.castlegates.health.*;
import io.github.pigaut.castlegates.hook.auraskill.*;
import io.github.pigaut.castlegates.hook.mcmmo.*;
import io.github.pigaut.voxel.core.config.*;
import io.github.pigaut.voxel.data.function.action.*;
import io.github.pigaut.voxel.data.function.condition.config.*;
import io.github.pigaut.yaml.*;
import io.github.pigaut.yaml.amount.*;
import org.jetbrains.annotations.*;

import static io.github.pigaut.yaml.configurator.load.ConfigLoader.Line;

public class CastleGatesConfigurator extends PluginConfigurator {

    public CastleGatesConfigurator(@NotNull CastleGatesPlugin plugin) {
        super(plugin);

        addLoader(GateTemplate.class, new GateLoader(plugin));
        addLoader(GatePhase.class, new GatePhaseLoader(plugin));

        addLoader(ToolDamage.class, new ToolDamageLoader());
        addLoader(HealthBar.class, new HealthBarLoader());

        final ConditionLoader conditions = getConditionLoader();
        final ActionLoader actions = getActionLoader();

        // Gate Actions
        actions.addLoader("OPEN_GATE", (Line<Action>) line ->
                new GateOpenAction());

        actions.addLoader("CLOSE_GATE", (Line<Action>) line ->
                new GateCloseAction());

        actions.addLoader("REPLACE_GATE", (Line<Action>) line ->
                new ReplaceGateAction(plugin, line.getRequiredString(1)));

        actions.addLoader("DAMAGE_GATE", (Line<Action>) line -> {
            ConfigOptional<Amount> amount = line.get(1, Amount.class);
            if (!amount.existsInConfig()) {
                return new DamageGateWithTool(plugin);
            }
            return new DamageGateAction(amount.withDefault(Amount.ONE));
        });

        actions.addLoader("DAMAGE_GATE_WITH_TOOL", (Line<Action>) line ->
                new DamageGateWithTool(plugin));

        AuraSkillsHook.addConditions(this);
        AuraSkillsHook.addActions(this);

        McMMOHook.addConditions(this);
        McMMOHook.addActions(this);

    }

}
