package io.github.pigaut.castlegates.command;

import io.github.pigaut.castlegates.*;
import io.github.pigaut.castlegates.menu.*;
import io.github.pigaut.voxel.command.node.*;
import org.jetbrains.annotations.*;

public class CastleGatesMenuCommand extends SubCommand {

    public CastleGatesMenuCommand(@NotNull CastleGatesPlugin plugin) {
        super("menu", plugin);
        withPermission(plugin.getPermission("menu"));
        withDescription(plugin.getTranslation("castlegates-menu-command"));
        withPlayerStateExecution((player, args, placeholders) -> {
            player.openMenu(new CastleGatesMenu(plugin));
        });
    }

}
