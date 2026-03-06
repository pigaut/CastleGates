package io.github.pigaut.orestack.command.generator;

import io.github.pigaut.orestack.*;
import io.github.pigaut.orestack.generator.template.*;
import io.github.pigaut.orestack.util.*;
import io.github.pigaut.voxel.bukkit.*;
import io.github.pigaut.voxel.command.node.*;
import io.github.pigaut.voxel.player.*;
import org.jetbrains.annotations.*;

public class GeneratorGetSubCommand extends SubCommand {

    public GeneratorGetSubCommand(@NotNull OrestackPlugin plugin) {
        super("get", plugin);
        withPermission(plugin.getPermission("generator.get"));
        withDescription(plugin.getTranslation("generator-get-command"));
        withParameter(GeneratorParameters.GENERATOR_NAME);
        withPlayerExecution((player, args, placeholders) -> {
            final GeneratorTemplate generator = plugin.getGeneratorTemplate(args[0]);
            if (generator == null) {
                plugin.sendMessage(player, "generator-not-found", placeholders);
                return;
            }
            PlayerUtil.giveItemsOrDrop(player, GeneratorTool.createItem(generator));
            plugin.sendMessage(player, "received-generator", placeholders, generator);
        });
    }

}
