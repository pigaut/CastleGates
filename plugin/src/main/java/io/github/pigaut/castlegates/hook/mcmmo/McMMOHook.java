package io.github.pigaut.castlegates.hook.mcmmo;

import com.gmail.nossr50.datatypes.skills.*;
import io.github.pigaut.voxel.config.*;
import io.github.pigaut.voxel.core.function.action.*;
import io.github.pigaut.voxel.core.function.condition.*;
import io.github.pigaut.voxel.core.function.condition.config.*;
import io.github.pigaut.voxel.server.*;
import io.github.pigaut.yaml.*;
import io.github.pigaut.yaml.amount.*;
import io.github.pigaut.yaml.configurator.load.*;

public class McMMOHook {

    public static void addConditions(PluginConfigurator configurator) {
        ConditionLoader conditions = configurator.getConditionLoader();

        ConfigLoader.Line<Condition> NOT_ENABLED_LOADER = line -> {
            throw new InvalidConfigException(line, "McMMO is not loaded/enabled");
        };

        if (!Server.isPluginEnabled("McMMO")) {
            conditions.addLoader("HAS_MCMMO_LEVEL", NOT_ENABLED_LOADER);
            return;
        }

        conditions.addLoader("HAS_MCMMO_LEVEL", (ConfigLoader.Line<Condition>) line ->
                new HasMcMMOLevel(
                        line.getRequired(1, Amount.class),
                        line.get("skill", PrimarySkillType.class).withDefault(PrimarySkillType.MINING)
                ));

    }

    public static void addActions(PluginConfigurator configurator) {
        ActionLoader actions = configurator.getActionLoader();

        ConfigLoader.Line<Action> NOT_ENABLED_LOADER = line -> {
            throw new InvalidConfigException(line, "McMMO is not loaded/enabled");
        };

        if (!Server.isPluginEnabled("McMMO")) {
            actions.addLoader("GIVE_MCMMO_EXP", NOT_ENABLED_LOADER);
            actions.addLoader("TAKE_MCMMO_EXP", NOT_ENABLED_LOADER);
            actions.addLoader("RESET_MCMMO_COOLDOWNS", NOT_ENABLED_LOADER);
            actions.addLoader("LEVEL_UP_MCMMO_SKILL", NOT_ENABLED_LOADER);
            return;
        }

        actions.addLoader("GIVE_MCMMO_EXP", (ConfigLoader.Line<Action>) line ->
                new GiveMcMMOExp(
                        line.getRequired(1, Amount.class),
                        line.get("skill", PrimarySkillType.class).withDefault(PrimarySkillType.MINING)
                ));

        actions.addLoader("TAKE_MCMMO_EXP", (ConfigLoader.Line<Action>) line ->
                new TakeMcMMOExp(
                        line.getRequired(1, Amount.class),
                        line.get("skill", PrimarySkillType.class).withDefault(PrimarySkillType.MINING)
                ));

        actions.addLoader("RESET_MCMMO_COOLDOWNS", (ConfigLoader.Line<Action>) line ->
                new ResetMcMMOCooldowns());


        actions.addLoader("LEVEL_UP_MCMMO_SKILL", (ConfigLoader.Line<Action>) line ->
                new LevelUpMcMMOSkill(line.getRequired(1, PrimarySkillType.class)));

    }

}
