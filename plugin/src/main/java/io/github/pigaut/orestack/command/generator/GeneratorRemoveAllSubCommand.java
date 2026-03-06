package io.github.pigaut.orestack.command.generator;

import io.github.pigaut.orestack.*;
import io.github.pigaut.orestack.generator.*;
import io.github.pigaut.orestack.generator.template.*;
import io.github.pigaut.orestack.player.*;
import io.github.pigaut.voxel.command.node.*;
import io.github.pigaut.voxel.core.structure.*;
import org.bukkit.*;
import org.jetbrains.annotations.*;

public class GeneratorRemoveAllSubCommand extends SubCommand {

    public GeneratorRemoveAllSubCommand(@NotNull OrestackPlugin plugin) {
        super("remove-all", plugin);
        withPermission(plugin.getPermission("generator.remove-all"));
        withDescription(plugin.getTranslation("generator-remove-all-command"));
        withParameter(GeneratorParameters.GENERATOR_NAME);
        withPlayerExecution((player, args, placeholders) -> {
            OrestackPlayer playerState = plugin.getPlayerState(player);
            GeneratorTemplate generatorTemplate = plugin.getGeneratorTemplate(args[0]);
            if (generatorTemplate == null) {
                plugin.sendMessage(player, "generator-not-found", placeholders);
                return;
            }
            Location firstSelection = playerState.getFirstSelection();
            Location secondSelection = playerState.getSecondSelection();
            if (firstSelection == null || secondSelection == null) {
                plugin.sendMessage(player, "incomplete-region", placeholders);
                return;
            }
            for (Location point : CuboidRegion.getAllLocations(player.getWorld(), firstSelection, secondSelection)) {
                Generator foundGenerator = plugin.getGenerator(point);
                if (foundGenerator == null) {
                    continue;
                }
                if (foundGenerator.getTemplate() == generatorTemplate) {
                    foundGenerator.remove();
                }
            }
            plugin.sendMessage(player, "removed-all-generators", placeholders, generatorTemplate);
        });
    }

}
