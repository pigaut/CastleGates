package io.github.pigaut.castlegates.command.gate;

import io.github.pigaut.castlegates.*;
import io.github.pigaut.castlegates.gate.template.*;
import io.github.pigaut.castlegates.util.*;
import io.github.pigaut.voxel.command.node.*;
import io.github.pigaut.voxel.player.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class GateGetGroupSubCommand extends SubCommand {

    public GateGetGroupSubCommand(@NotNull CastleGatesPlugin plugin) {
        super("get-group", plugin);
        withPermission(plugin.getPermission("gate.get-group"));
        withDescription(plugin.getTranslation("gate-get-group-command"));
        withParameter(GateParameters.GATE_GROUP);
        withPlayerExecution((player, args, placeholders) -> {
            final List<GateTemplate> groupGates = plugin.getGateTemplates().getAll(args[0]);

            if (groupGates.isEmpty()) {
                plugin.sendMessage(player, "gate-group-not-found", placeholders);
                return;
            }

            for (GateTemplate gate : groupGates) {
                PlayerUtil.giveItemsOrDrop(player, GateTool.createItem(gate));
            }
            plugin.sendMessage(player, "received-gate-group", placeholders);
        });
    }

}
