package io.github.pigaut.castlegates.core.action;

import io.github.pigaut.castlegates.*;
import io.github.pigaut.castlegates.gate.*;
import io.github.pigaut.castlegates.gate.template.*;
import io.github.pigaut.voxel.core.context.*;
import io.github.pigaut.voxel.data.function.action.*;
import org.jetbrains.annotations.*;

public class ReplaceGateAction implements Action {

    private final CastleGatesPlugin plugin;
    private final String gateName;

    public ReplaceGateAction(CastleGatesPlugin plugin, String gateName) {
        this.plugin = plugin;
        this.gateName = gateName;
    }

    @Override
    public void execute(@NotNull Context context) {
        Gate gate = context.get(Gate.class);
        if (gate == null || !gate.exists()) {
            return;
        }

        GateTemplate replacementGate = plugin.getGateTemplate(gateName);
        if (replacementGate != null) {
            gate.replace(replacementGate);
        }
    }
}
