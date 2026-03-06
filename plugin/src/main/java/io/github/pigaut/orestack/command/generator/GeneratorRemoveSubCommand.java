package io.github.pigaut.orestack.command.generator;

import io.github.pigaut.orestack.*;
import io.github.pigaut.orestack.generator.*;
import io.github.pigaut.voxel.command.node.*;
import org.bukkit.*;
import org.bukkit.block.*;
import org.jetbrains.annotations.*;

public class GeneratorRemoveSubCommand extends SubCommand {

    public GeneratorRemoveSubCommand(@NotNull OrestackPlugin plugin) {
        super("remove", plugin);
        withPermission(plugin.getPermission("generator.remove"));
        withDescription(plugin.getTranslation("generator-remove-command"));
        withPlayerExecution((player, args, placeholders) -> {
            Block targetBlock = player.getTargetBlockExact(6);
            if (targetBlock == null) {
                plugin.sendMessage(player, "too-far-away", placeholders);
                return;
            }
            Location location = targetBlock.getLocation();
            Generator generator = plugin.getGenerator(location);
            if (generator == null) {
                plugin.sendMessage(player, "target-not-generator", placeholders);
                return;
            }

            generator.remove();
            plugin.sendMessage(player, "removed-generator", placeholders, generator.getState());
        });

    }

}
