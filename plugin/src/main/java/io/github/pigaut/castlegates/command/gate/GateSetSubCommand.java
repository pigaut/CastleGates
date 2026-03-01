package io.github.pigaut.castlegates.command.gate;

import io.github.pigaut.castlegates.*;
import io.github.pigaut.castlegates.gate.*;
import io.github.pigaut.castlegates.gate.template.*;
import io.github.pigaut.castlegates.util.*;
import io.github.pigaut.voxel.command.node.*;
import org.bukkit.*;
import org.bukkit.block.*;
import org.jetbrains.annotations.*;

public class GateSetSubCommand extends SubCommand {

    public GateSetSubCommand(@NotNull CastleGatesPlugin plugin) {
        super("set", plugin);
        withPermission(plugin.getPermission("gate.set"));
        withDescription(plugin.getTranslation("gate-set-command"));
        withParameter(GateParameters.GATE_NAME);
        withPlayerExecution((player, args, placeholders) -> {
            final GateTemplate gate = plugin.getGateTemplate(args[0]);
            if (gate == null) {
                plugin.sendMessage(player, "gate-not-found", placeholders);
                return;
            }

            final Block targetBlock = player.getTargetBlockExact(6);
            if (targetBlock == null) {
                plugin.sendMessage(player, "too-far-away", placeholders, gate);
                return;
            }

            final Location location = targetBlock.getLocation();
            try {
                Gate.create(gate, location);
                plugin.sendMessage(player, "created-gate", placeholders, gate);
            }
            catch (GateOverlapException e) {
                plugin.sendMessage(player, "gate-overlap");
            }
        });
    }

}
