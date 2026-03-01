package io.github.pigaut.castlegates.command.gate;

import io.github.pigaut.castlegates.*;
import io.github.pigaut.castlegates.gate.*;
import io.github.pigaut.castlegates.gate.template.*;
import io.github.pigaut.voxel.command.node.*;
import io.github.pigaut.voxel.server.*;
import org.bukkit.block.*;
import org.bukkit.event.block.*;
import org.jetbrains.annotations.*;

public class GateHarvestSubCommand extends SubCommand {

    public GateHarvestSubCommand(@NotNull CastleGatesPlugin plugin) {
        super("harvest-all", plugin);
        withPermission(plugin.getPermission("gate.harvest-all"));
        withDescription(plugin.getTranslation("gate-harvest-all-command"));
        withParameter(GateParameters.GATE_NAME);
        withPlayerExecution((player, args, placeholders) -> {
            final GateTemplate gate = plugin.getGateTemplate(args[0]);
            if (gate == null) {
                plugin.sendMessage(player, "gate-not-found", placeholders);
                return;
            }
            for (Gate blockGate : plugin.getGates().getAllGates()) {
                if (blockGate.getTemplate() == gate) {
                    Block block = blockGate.getBlocks().get(0);
                    BlockBreakEvent event = new BlockBreakEvent(block, player);
                    Server.callEvent(event);
                }
            }
            plugin.sendMessage(player, "harvested-all-gates", placeholders, gate);
        });
    }

}
