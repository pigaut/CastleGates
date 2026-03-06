package io.github.pigaut.orestack.command.generator;

import io.github.pigaut.orestack.*;
import io.github.pigaut.orestack.generator.*;
import io.github.pigaut.orestack.generator.template.*;
import io.github.pigaut.orestack.player.*;
import io.github.pigaut.orestack.util.*;
import io.github.pigaut.voxel.command.node.*;
import io.github.pigaut.voxel.core.structure.*;
import io.github.pigaut.voxel.bukkit.Rotation;
import org.bukkit.*;
import org.jetbrains.annotations.*;

public class GeneratorSetAllSubCommand extends SubCommand {

    public GeneratorSetAllSubCommand(@NotNull OrestackPlugin plugin) {
        super("set-all", plugin);
        withPermission(plugin.getPermission("generator.set-all"));
        withDescription(plugin.getTranslation("generator-set-all-command"));
        withParameter(GeneratorParameters.GENERATOR_NAME);
        withPlayerExecution((player, args, placeholders) -> {
            final OrestackPlayer playerState = plugin.getPlayerState(player);
            final GeneratorTemplate template = plugin.getGeneratorTemplate(args[0]);
            if (template == null) {
                plugin.sendMessage(player, "generator-not-found", placeholders);
                return;
            }
            final Location firstSelection = playerState.getFirstSelection();
            final Location secondSelection = playerState.getSecondSelection();
            if (firstSelection == null || secondSelection == null) {
                plugin.sendMessage(player, "incomplete-region", placeholders, template);
                return;
            }
            StructureTemplate structure = template.getLastStage().getStructureTemplate();
            for (Location location : CuboidRegion.getAllLocations(player.getWorld(), firstSelection, secondSelection)) {
                for (Rotation rotation : Rotation.values()) {
                    if (structure.isPlaced(location, rotation)) {
                        try {
                            Generator.create(template, location);
                        }
                        catch (GeneratorOverlapException ignored) {
                            // Ignore if generator overlaps
                        }
                    }
                }
            }
            plugin.sendMessage(player, "created-all-generators", placeholders, template);
        });
    }

}
