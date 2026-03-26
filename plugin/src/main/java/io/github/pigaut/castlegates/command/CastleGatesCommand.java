package io.github.pigaut.castlegates.command;

import io.github.pigaut.castlegates.*;
import io.github.pigaut.castlegates.command.gate.*;
import io.github.pigaut.castlegates.menu.*;
import io.github.pigaut.voxel.core.command.*;
import io.github.pigaut.voxel.core.command.node.*;
import io.github.pigaut.voxel.data.item.command.*;
import io.github.pigaut.voxel.data.message.command.*;
import io.github.pigaut.voxel.data.particle.command.*;
import io.github.pigaut.voxel.data.sound.command.*;
import io.github.pigaut.voxel.data.structure.command.*;
import io.github.pigaut.voxel.plugin.command.*;
import org.jetbrains.annotations.*;

public class CastleGatesCommand extends EnhancedCommand {

    public CastleGatesCommand(@NotNull CastleGatesPlugin plugin) {
        super(plugin, "castlegates");
        this.description = "CastleGates plugin commands";
        this.setAliases("cg", "gates");

        RootCommand command = this.getRootCommand();
        command.withPermission("castlegates");
        command.withPlayerStateExecution((player, context, args) -> {
           player.openMenu(new CastleGatesMenu(plugin));
        });

        addSubCommand(new HelpSubCommand(plugin));
        addSubCommand(new WikiSubCommand(plugin));
        addSubCommand(new SupportSubCommand(plugin));
        addSubCommand(new ReloadSubCommand(plugin));
        addSubCommand(new ItemSubCommand(plugin));
        addSubCommand(new ParticleSubCommand(plugin));
        addSubCommand(new MessageSubCommand(plugin));
        addSubCommand(new SoundSubCommand(plugin));
        addSubCommand(new GateSubCommand(plugin));
        addSubCommand(new StructureSubCommand(plugin));
        addSubCommand(new GetWandSubCommand(plugin));
        addSubCommand(new CastleGatesMenuCommand(plugin));
    }

}
