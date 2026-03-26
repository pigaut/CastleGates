package io.github.pigaut.castlegates.command.gate;

import io.github.pigaut.castlegates.*;
import io.github.pigaut.castlegates.api.event.*;
import io.github.pigaut.castlegates.core.*;
import io.github.pigaut.castlegates.gate.*;
import io.github.pigaut.castlegates.gate.template.*;
import io.github.pigaut.voxel.bukkit.*;
import io.github.pigaut.voxel.bukkit.Rotation;
import io.github.pigaut.voxel.core.command.node.*;
import io.github.pigaut.voxel.util.Server;
import org.bukkit.*;
import org.bukkit.block.*;
import org.jetbrains.annotations.*;

public class GateSetSubCommand extends SubCommand {

    public GateSetSubCommand(@NotNull CastleGatesPlugin plugin) {
        super("set", plugin);
        withPermission(plugin.getPermission("gate.set"));
        withDescription(plugin.getTranslation("gate-set-command"));
        withParameter(GateParameters.GATE_NAME);
        withPlayerExecution((player, context, args) -> {
            GateTemplate gate = plugin.getGateTemplate(args[0]);
            if (gate == null) {
                plugin.sendMessage(player, context, "gate-not-found");
                return;
            }

            Block targetBlock = player.getTargetBlockExact(6);
            if (targetBlock == null) {
                plugin.sendMessage(player, context, "too-far-away");
                return;
            }

            Location location = targetBlock.getLocation();

            GatePlaceEvent gatePlaceEvent = new GatePlaceEvent(player, location, gate.getName(), gate.getOccupiedBlocks(location, Rotation.NONE));
            Server.callEvent(gatePlaceEvent);

            if (gatePlaceEvent.isCancelled()) {
                plugin.sendMessage(player, context, "gate-conflict");
                return;
            }

            try {
                Gate.create(gate, location);
                plugin.sendMessage(player, context, "created-gate");
            }
            catch (GateOverlapException e) {
                plugin.sendMessage(player, context, "gate-overlap");
            }
        });
    }

}
