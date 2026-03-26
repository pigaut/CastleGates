package io.github.pigaut.castlegates.command.gate;

import io.github.pigaut.castlegates.*;
import io.github.pigaut.castlegates.gate.*;
import io.github.pigaut.castlegates.gate.template.*;
import io.github.pigaut.castlegates.player.*;
import io.github.pigaut.voxel.core.command.node.*;
import io.github.pigaut.voxel.data.structure.*;
import org.bukkit.*;
import org.jetbrains.annotations.*;

public class GateRemoveAllSubCommand extends SubCommand {

    public GateRemoveAllSubCommand(@NotNull CastleGatesPlugin plugin) {
        super("remove-all", plugin);
        withPermission(plugin.getPermission("gate.remove-all"));
        withDescription(plugin.getTranslation("gate-remove-all-command"));
        withParameter(GateParameters.GATE_NAME);
        withPlayerExecution((player, context, args) -> {
            final GatesPlayer playerState = plugin.getPlayerState(player);
            final GateTemplate gate = plugin.getGateTemplate(args[0]);
            if (gate == null) {
                plugin.sendMessage(player, context, "gate-not-found");
                return;
            }
            final Location firstSelection = playerState.getFirstSelection();
            final Location secondSelection = playerState.getSecondSelection();
            if (firstSelection == null || secondSelection == null) {
                plugin.sendMessage(player, context, "incomplete-region");
                return;
            }
            for (Location point : CuboidRegion.getAllLocations(player.getWorld(), firstSelection, secondSelection)) {
                final Gate blockGate = plugin.getGate(point);
                if (blockGate == null) {
                    continue;
                }
                if (blockGate.getTemplate() == gate) {
                    plugin.getGates().unregisterGate(blockGate);
                }
            }
            plugin.sendMessage(player, context, "removed-all-gates");
        });
    }

}
