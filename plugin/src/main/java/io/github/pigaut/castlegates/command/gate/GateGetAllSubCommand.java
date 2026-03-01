package io.github.pigaut.castlegates.command.gate;

import io.github.pigaut.castlegates.*;
import io.github.pigaut.castlegates.gate.template.*;
import io.github.pigaut.castlegates.util.*;
import io.github.pigaut.voxel.command.node.*;
import io.github.pigaut.voxel.player.*;
import org.jetbrains.annotations.*;

public class GateGetAllSubCommand extends SubCommand {

    public GateGetAllSubCommand(@NotNull CastleGatesPlugin plugin) {
        super("get-all", plugin);
        withPermission(plugin.getPermission("gate.get-all"));
        withDescription(plugin.getTranslation("gate-get-all-command"));
        withPlayerExecution((player, args, placeholders) -> {
            for (GateTemplate gate : plugin.getGateTemplates().getAll()) {
                PlayerUtil.giveItemsOrDrop(player, GateTool.createItem(gate));
            }
            plugin.sendMessage(player, "received-all-gates", placeholders);
        });
    }
}
