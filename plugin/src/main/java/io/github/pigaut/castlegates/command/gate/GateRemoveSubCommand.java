package io.github.pigaut.castlegates.command.gate;

import io.github.pigaut.castlegates.*;
import io.github.pigaut.castlegates.gate.*;
import io.github.pigaut.voxel.command.node.*;
import org.bukkit.*;
import org.bukkit.block.*;
import org.jetbrains.annotations.*;

public class GateRemoveSubCommand extends SubCommand {

    public GateRemoveSubCommand(@NotNull CastleGatesPlugin plugin) {
        super("remove", plugin);
        withPermission(plugin.getPermission("gate.remove"));
        withDescription(plugin.getTranslation("gate-remove-command"));
        withPlayerExecution((player, args, placeholders) -> {
            Block targetBlock = player.getTargetBlockExact(6);
            if (targetBlock == null) {
                plugin.sendMessage(player, "too-far-away", placeholders);
                return;
            }
            Location location = targetBlock.getLocation();
            Gate gate = plugin.getGate(location);
            if (gate == null) {
                plugin.sendMessage(player, "target-not-gate", placeholders);
                return;
            }
            plugin.getGates().unregisterGate(gate);
            plugin.sendMessage(player, "removed-gate", placeholders, gate.getState());
        });

    }

}
