package io.github.pigaut.castlegates.command.gate;

import io.github.pigaut.castlegates.*;
import io.github.pigaut.castlegates.core.*;
import io.github.pigaut.castlegates.gate.template.*;
import io.github.pigaut.voxel.bukkit.*;
import io.github.pigaut.voxel.core.command.node.*;
import org.jetbrains.annotations.*;

public class GateGetSubCommand extends SubCommand {

    public GateGetSubCommand(@NotNull CastleGatesPlugin plugin) {
        super("get", plugin);
        withPermission(plugin.getPermission("gate.get"));
        withDescription(plugin.getTranslation("gate-get-command"));
        withParameter(GateParameters.GATE_NAME);
        withPlayerExecution((player, context, args) -> {
            GateTemplate gate = plugin.getGateTemplate(args[0]);
            if (gate == null) {
                plugin.sendMessage(player, context, "gate-not-found");
                return;
            }
            PlayerUtil.giveItemsOrDrop(player, GateTool.createItem(gate));
            plugin.sendMessage(player, context, "received-gate");
        });
    }

}
