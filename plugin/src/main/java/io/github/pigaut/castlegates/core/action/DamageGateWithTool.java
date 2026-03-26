package io.github.pigaut.castlegates.core.action;

import io.github.pigaut.castlegates.*;
import io.github.pigaut.castlegates.gate.*;
import io.github.pigaut.castlegates.settings.*;
import io.github.pigaut.voxel.core.context.*;
import io.github.pigaut.voxel.data.function.action.*;
import org.bukkit.block.*;
import org.bukkit.entity.*;
import org.jetbrains.annotations.*;

public class DamageGateWithTool implements Action {

    private final CastleGatesPlugin plugin;

    public DamageGateWithTool(CastleGatesPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(@NotNull Context context) {
        Player player = context.player();
        Block block = context.block();
        if (player == null || block == null) {
            return;
        }

        Gate gate = context.get(Gate.class);
        if (gate == null || !gate.exists()) {
            return;
        }

        CastleGatesSettings settings = plugin.getSettings();
        gate.damage(player, context, settings.getGateDamage(player, block));
    }

}
