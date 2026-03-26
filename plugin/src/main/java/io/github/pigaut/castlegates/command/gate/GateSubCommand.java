package io.github.pigaut.castlegates.command.gate;

import io.github.pigaut.castlegates.*;
import io.github.pigaut.voxel.core.command.node.*;
import org.jetbrains.annotations.*;

public class GateSubCommand extends SubCommand {

    public GateSubCommand(@NotNull CastleGatesPlugin plugin) {
        super("gate", plugin);
        withPermission(plugin.getPermission("gate"));
        withDescription(plugin.getTranslation("gate-command"));
        addSubCommand(new GateGetSubCommand(plugin));
        addSubCommand(new GateGetGroupSubCommand(plugin));
        addSubCommand(new GateGetAllSubCommand(plugin));
        addSubCommand(new GateSetSubCommand(plugin));
        addSubCommand(new GateRemoveSubCommand(plugin));
        addSubCommand(new GateSetAllSubCommand(plugin));
        addSubCommand(new GateRemoveAllSubCommand(plugin));
        addSubCommand(new GateHarvestSubCommand(plugin));
    }

}
