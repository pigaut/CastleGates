package io.github.pigaut.castlegates.command.gate;

import io.github.pigaut.castlegates.*;
import io.github.pigaut.castlegates.gate.*;
import io.github.pigaut.castlegates.gate.template.*;
import io.github.pigaut.castlegates.player.*;
import io.github.pigaut.castlegates.util.*;
import io.github.pigaut.voxel.command.node.*;
import io.github.pigaut.voxel.core.structure.*;
import io.github.pigaut.voxel.bukkit.Rotation;
import org.bukkit.*;
import org.jetbrains.annotations.*;

public class GateSetAllSubCommand extends SubCommand {

    public GateSetAllSubCommand(@NotNull CastleGatesPlugin plugin) {
        super("set-all", plugin);
        withPermission(plugin.getPermission("gate.set-all"));
        withDescription(plugin.getTranslation("gate-set-all-command"));
        withParameter(GateParameters.GATE_NAME);
        withPlayerExecution((player, args, placeholders) -> {
            final GatesPlayer playerState = plugin.getPlayerState(player);
            final GateTemplate template = plugin.getGateTemplate(args[0]);
            if (template == null) {
                plugin.sendMessage(player, "gate-not-found", placeholders);
                return;
            }
            final Location firstSelection = playerState.getFirstSelection();
            final Location secondSelection = playerState.getSecondSelection();
            if (firstSelection == null || secondSelection == null) {
                plugin.sendMessage(player, "incomplete-region", placeholders, template);
                return;
            }
            final StructureTemplate structure = template.getLastStage().getStructureTemplate();
            for (Location location : CuboidRegion.getAllLocations(player.getWorld(), firstSelection, secondSelection)) {
                for (Rotation rotation : Rotation.values()) {
                    if (structure.isPlaced(location, rotation)) {
                        try {
                            Gate.create(template, location);
                        }
                        catch (GateOverlapException ignored) {
                            // Ignore if gate overlaps
                        }
                    }
                }
            }
            plugin.sendMessage(player, "created-all-gates", placeholders, template);
        });
    }

}
