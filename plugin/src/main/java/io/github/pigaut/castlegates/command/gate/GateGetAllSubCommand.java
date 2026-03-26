package io.github.pigaut.castlegates.command.gate;

import io.github.pigaut.castlegates.*;
import io.github.pigaut.castlegates.core.*;
import io.github.pigaut.castlegates.gate.template.*;
import io.github.pigaut.voxel.bukkit.*;
import io.github.pigaut.voxel.core.command.node.*;
import org.jetbrains.annotations.*;

public class GateGetAllSubCommand extends SubCommand {

    public GateGetAllSubCommand(@NotNull CastleGatesPlugin plugin) {
        super("get-all", plugin);
        withPermission(plugin.getPermission("gate.get-all"));
        withDescription(plugin.getTranslation("gate-get-all-command"));
        withPlayerExecution((player, context, args) -> {
            for (GateTemplate gate : plugin.getGateTemplates().getAll()) {
                PlayerUtil.giveItemsOrDrop(player, GateTool.createItem(gate));
            }
            plugin.sendMessage(player, context, "received-all-gates");
        });
    }
}
