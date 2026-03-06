package io.github.pigaut.orestack.command.generator;

import io.github.pigaut.orestack.*;
import io.github.pigaut.orestack.generator.template.*;
import io.github.pigaut.orestack.util.*;
import io.github.pigaut.voxel.bukkit.*;
import io.github.pigaut.voxel.command.node.*;
import io.github.pigaut.voxel.player.*;
import org.jetbrains.annotations.*;

public class GeneratorGetAllSubCommand extends SubCommand {

    public GeneratorGetAllSubCommand(@NotNull OrestackPlugin plugin) {
        super("get-all", plugin);
        withPermission(plugin.getPermission("generator.get-all"));
        withDescription(plugin.getTranslation("generator-get-all-command"));
        withPlayerExecution((player, args, placeholders) -> {
            for (GeneratorTemplate generator : plugin.getGeneratorTemplates().getAll()) {
                PlayerUtil.giveItemsOrDrop(player, GeneratorTool.createItem(generator));
            }
            plugin.sendMessage(player, "received-all-generators", placeholders);
        });
    }
}
