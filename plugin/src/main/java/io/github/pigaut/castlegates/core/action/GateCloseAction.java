package io.github.pigaut.castlegates.core.action;

import io.github.pigaut.castlegates.*;
import io.github.pigaut.castlegates.gate.*;
import io.github.pigaut.voxel.core.context.*;
import io.github.pigaut.voxel.data.function.action.*;
import org.bukkit.block.*;
import org.jetbrains.annotations.*;

public class GateCloseAction implements Action {

    @Override
    public void execute(@NotNull Context context) {
        Gate gate = context.get(Gate.class);
        if (gate != null) {
            gate.close();
        }
    }

}
