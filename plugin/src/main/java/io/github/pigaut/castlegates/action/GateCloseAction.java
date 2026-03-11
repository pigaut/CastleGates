package io.github.pigaut.castlegates.action;

import io.github.pigaut.castlegates.*;
import io.github.pigaut.castlegates.gate.*;
import io.github.pigaut.voxel.core.function.action.block.*;
import org.bukkit.block.*;
import org.jetbrains.annotations.*;

public class GateCloseAction implements BlockAction {

    private final CastleGatesPlugin plugin = CastleGatesPlugin.getInstance();

    @Override
    public void execute(@NotNull Block block) {
        Gate gate = plugin.getGate(block.getLocation());
        if (gate != null) {
            gate.close();
        }
    }

}
