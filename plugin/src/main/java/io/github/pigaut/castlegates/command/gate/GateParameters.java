package io.github.pigaut.castlegates.command.gate;

import io.github.pigaut.castlegates.*;
import io.github.pigaut.voxel.core.command.*;

public class GateParameters {

    private static final CastleGatesPlugin plugin = CastleGatesPlugin.getInstance();

    public static final CommandParameter GATE_NAME = CommandParameter.create("gate-name",
            (commandSender, strings) -> plugin.getGateTemplates().getAllNames());

    public static final CommandParameter GATE_GROUP = CommandParameter.create("gate-group",
            (commandSender, strings) -> plugin.getGateTemplates().getAllGroups());

}
