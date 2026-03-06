package io.github.pigaut.orestack.command.generator;

import io.github.pigaut.orestack.*;
import io.github.pigaut.orestack.generator.template.*;
import io.github.pigaut.orestack.util.*;
import io.github.pigaut.voxel.bukkit.*;
import io.github.pigaut.voxel.command.node.*;
import io.github.pigaut.voxel.player.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class GeneratorGetGroupSubCommand extends SubCommand {

    public GeneratorGetGroupSubCommand(@NotNull OrestackPlugin plugin) {
        super("get-group", plugin);
        withPermission(plugin.getPermission("generator.get-group"));
        withDescription(plugin.getTranslation("generator-get-group-command"));
        withParameter(GeneratorParameters.GENERATOR_GROUP);
        withPlayerExecution((player, args, placeholders) -> {
            final List<GeneratorTemplate> groupGenerators = plugin.getGeneratorTemplates().getAll(args[0]);

            if (groupGenerators.isEmpty()) {
                plugin.sendMessage(player, "generator-group-not-found", placeholders);
                return;
            }

            for (GeneratorTemplate generator : groupGenerators) {
                PlayerUtil.giveItemsOrDrop(player, GeneratorTool.createItem(generator));
            }
            plugin.sendMessage(player, "received-generator-group", placeholders);
        });
    }

}
