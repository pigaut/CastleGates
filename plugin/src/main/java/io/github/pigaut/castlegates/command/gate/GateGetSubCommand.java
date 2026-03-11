package io.github.pigaut.castlegates.command.gate;

import io.github.pigaut.castlegates.*;
import io.github.pigaut.castlegates.gate.template.*;
import io.github.pigaut.castlegates.util.*;
import io.github.pigaut.voxel.bukkit.*;
import io.github.pigaut.voxel.command.node.*;
import io.github.pigaut.voxel.player.*;
import org.jetbrains.annotations.*;

public class GateGetSubCommand extends SubCommand {

    public GateGetSubCommand(@NotNull CastleGatesPlugin plugin) {
        super("get", plugin);
        withPermission(plugin.getPermission("gate.get"));
        withDescription(plugin.getTranslation("gate-get-command"));
        withParameter(GateParameters.GATE_NAME);
        withPlayerExecution((player, args, placeholders) -> {
            GateTemplate gate = plugin.getGateTemplate(args[0]);
            if (gate == null) {
                plugin.sendMessage(player, "gate-not-found", placeholders);
                return;
            }
            PlayerUtil.giveItemsOrDrop(player, GateTool.createItem(gate));
            plugin.sendMessage(player, "received-gate", placeholders, gate);
        });
    }

}
