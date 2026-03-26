package io.github.pigaut.castlegates.hook.auraskill;

import dev.aurelium.auraskills.api.skill.*;
import io.github.pigaut.voxel.core.config.*;
import io.github.pigaut.voxel.data.function.action.*;
import io.github.pigaut.voxel.data.function.condition.*;
import io.github.pigaut.voxel.data.function.condition.config.*;
import io.github.pigaut.voxel.util.*;
import io.github.pigaut.yaml.*;
import io.github.pigaut.yaml.amount.*;
import io.github.pigaut.yaml.configurator.load.*;

public class AuraSkillsHook {

    public static void addConditions(PluginConfigurator configurator) {
        ConditionLoader conditions = configurator.getConditionLoader();

        ConfigLoader.Line<Condition> NOT_ENABLED_LOADER = line -> {
            throw new InvalidConfigException(line, "AuraSkills is not loaded/enabled");
        };

        if (!Server.isPluginEnabled("AuraSkills")) {
            conditions.addLoader("HAS_AURA_LEVEL", NOT_ENABLED_LOADER);
            conditions.addLoader("HAS_AURA_MANA", NOT_ENABLED_LOADER);
            return;
        }

        conditions.addLoader("HAS_AURA_LEVEL", (ConfigLoader.Line<Condition>) line ->
                new HasAuraSkillLevel(
                        line.getRequired(1, Amount.class),
                        line.get("skill", Skills.class).withDefault(Skills.MINING)
                ));

        conditions.addLoader("HAS_AURA_MANA", (ConfigLoader.Line<Condition>) line ->
                new HasAuraMana(line.getRequired(1, Amount.class)));

    }

    public static void addActions(PluginConfigurator configurator) {
        ActionLoader actions = configurator.getActionLoader();

        ConfigLoader.Line<Action> NOT_ENABLED_LOADER = line -> {
            throw new InvalidConfigException(line, "AuraSkills is not loaded/enabled");
        };

        if (!Server.isPluginEnabled("AuraSkills")) {
            actions.addLoader("GIVE_AURA_EXP", NOT_ENABLED_LOADER);
            actions.addLoader("GIVE_AURA_MANA", NOT_ENABLED_LOADER);
            actions.addLoader("TAKE_AURA_MANA", NOT_ENABLED_LOADER);
            return;
        }

        actions.addLoader("GIVE_AURA_EXP", (ConfigLoader.Line<Action>) line ->
                new GiveAuraExp(
                        line.getRequired(1, Amount.class),
                        line.get("skill", Skills.class).withDefault(Skills.MINING),
                        line.getBoolean("raw").withDefault(false)
                ));

        actions.addLoader("GIVE_AURA_MANA", (ConfigLoader.Line<Action>) line ->
                new GiveAuraMana(line.getRequired(1, Amount.class)));

        actions.addLoader("TAKE_AURA_MANA", (ConfigLoader.Line<Action>) line ->
                new TakeAuraMana(line.getRequired(1, Amount.class)));

    }

}
