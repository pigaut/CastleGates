package io.github.pigaut.castlegates.gate;

import io.github.pigaut.voxel.core.hologram.*;
import io.github.pigaut.voxel.plugin.task.*;
import org.jetbrains.annotations.*;

public class GateState {

    private int currentStage;
    private @Nullable Task transitionTask = null;
    private @Nullable HologramDisplay currentHologram = null;

    private GateTransition transition;
    private boolean updating;

}
