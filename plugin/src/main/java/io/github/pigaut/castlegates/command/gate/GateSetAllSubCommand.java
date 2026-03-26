package io.github.pigaut.castlegates.command.gate;

import io.github.pigaut.castlegates.*;
import io.github.pigaut.castlegates.api.event.*;
import io.github.pigaut.castlegates.core.*;
import io.github.pigaut.castlegates.gate.*;
import io.github.pigaut.castlegates.gate.template.*;
import io.github.pigaut.castlegates.player.*;
import io.github.pigaut.voxel.core.command.node.*;
import io.github.pigaut.voxel.data.structure.*;
import io.github.pigaut.voxel.bukkit.Rotation;
import io.github.pigaut.voxel.util.Server;
import org.bukkit.*;
import org.jetbrains.annotations.*;

public class GateSetAllSubCommand extends SubCommand {

    public GateSetAllSubCommand(@NotNull CastleGatesPlugin plugin) {
        super("set-all", plugin);
        withPermission(plugin.getPermission("gate.set-all"));
        withDescription(plugin.getTranslation("gate-set-all-command"));
        withParameter(GateParameters.GATE_NAME);
        withPlayerExecution((player, context, args) -> {
            GatesPlayer playerState = plugin.getPlayerState(player);
            GateTemplate template = plugin.getGateTemplate(args[0]);
            if (template == null) {
                plugin.sendMessage(player, context, "gate-not-found");
                return;
            }
            Location firstSelection = playerState.getFirstSelection();
            Location secondSelection = playerState.getSecondSelection();
            if (firstSelection == null || secondSelection == null) {
                plugin.sendMessage(player, context, "incomplete-region");
                return;
            }
            StructureTemplate structure = template.getLastPhase().getStructureTemplate();
            for (Location location : CuboidRegion.getAllLocations(player.getWorld(), firstSelection, secondSelection)) {
                for (Rotation rotation : Rotation.values()) {
                    if (structure.isPlaced(location, rotation)) {
                        GatePlaceEvent gatePlaceEvent = new GatePlaceEvent(player, location, template.getName(), template.getOccupiedBlocks(location, Rotation.NONE));
                        Server.callEvent(gatePlaceEvent);

                        if (gatePlaceEvent.isCancelled()) {
                            continue;
                        }

                        try {
                            Gate.create(template, location);
                        }
                        catch (GateOverlapException ignored) {
                            // Ignore if gate overlaps
                        }
                    }
                }
            }
            plugin.sendMessage(player, context, "created-all-gates");
        });
    }

}
