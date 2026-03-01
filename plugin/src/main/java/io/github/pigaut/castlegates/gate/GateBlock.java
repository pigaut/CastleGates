package io.github.pigaut.castlegates.gate;

import io.github.pigaut.castlegates.*;
import io.github.pigaut.castlegates.player.*;
import io.github.pigaut.voxel.core.function.*;
import org.bukkit.block.*;
import org.bukkit.entity.*;
import org.jetbrains.annotations.*;

public class GateBlock {

    private static final CastleGatesPlugin plugin = CastleGatesPlugin.getInstance();

    private GateBlock() {}

    public static void mineBlock(@NotNull Gate gate, @NotNull Player player, @NotNull Block block) {
        if (gate.isUpdating()) {
            return;
        }

        if (!gate.matchBlocks()) {
            plugin.getGates().unregisterGate(gate);
            return;
        }

        GateStage gateStage = gate.getCurrentStage();
        if (gateStage.getDecorativeBlocks().contains(block.getType())) {
            return;
        }

        GatesPlayer playerState = plugin.getPlayerState(player);
        playerState.updatePlaceholders(gate);

        Function breakFunction = gateStage.getBreakFunction();
        if (breakFunction != null) {
            breakFunction.run(playerState, null, block);
        }
    }

}
