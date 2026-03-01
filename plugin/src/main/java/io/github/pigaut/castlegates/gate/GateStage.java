package io.github.pigaut.castlegates.gate;

import io.github.pigaut.castlegates.gate.template.*;
import io.github.pigaut.voxel.core.function.*;
import io.github.pigaut.voxel.core.hologram.*;
import io.github.pigaut.voxel.core.structure.*;
import org.bukkit.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class GateStage {

    private final GateTemplate gate;
    private final BlockStructure structure;
    private final List<Material> decorativeBlocks;
    private final int openingDelay;
    private final int closingDelay;
    private final int clickCooldown;
    private final @Nullable Hologram openingHologram;
    private final @Nullable Hologram closingHologram;
    private final @Nullable Function onBreak;
    private final @Nullable Function onTransition;
    private final @Nullable Function onOpening;
    private final @Nullable Function onClosing;
    private final @Nullable Function onClick;
    private final @Nullable Function onLeftClick;
    private final @Nullable Function onRightClick;

    public GateStage(@NotNull GateTemplate gate, @NotNull BlockStructure structure, List<Material> decorativeBlocks,
                     int openingDelay, int closingDelay, int clickCooldown,
                     @Nullable Hologram openingHologram, @Nullable Hologram closingHologram, @Nullable Function onBreak,
                     @Nullable Function onTransition, @Nullable Function onOpening, @Nullable Function onClosing,
                     @Nullable Function onClick, @Nullable Function onLeftClick, @Nullable Function onRightClick) {
        this.gate = gate;
        this.structure = structure;
        this.decorativeBlocks = decorativeBlocks;
        this.openingDelay = openingDelay;
        this.closingDelay = closingDelay;
        this.clickCooldown = clickCooldown;
        this.closingHologram = closingHologram;
        this.onBreak = onBreak;
        this.onTransition = onTransition;
        this.onOpening = onOpening;
        this.onClosing = onClosing;
        this.onClick = onClick;
        this.onLeftClick = onLeftClick;
        this.onRightClick = onRightClick;
        this.openingHologram = openingHologram;
    }

    public @NotNull GateTemplate getGate() {
        return gate;
    }

    public @NotNull BlockStructure getStructure() {
        return structure;
    }

    public List<Material> getDecorativeBlocks() {
        return new ArrayList<>(decorativeBlocks);
    }

    public int getClickCooldown() {
        return clickCooldown;
    }

    public int getOpeningDelay() {
        return openingDelay;
    }

    public int getClosingDelay() {
        return closingDelay;
    }

    public @Nullable Function getBreakFunction() {
        return onBreak;
    }

    public @Nullable Hologram getOpeningHologram() {
        return openingHologram;
    }

    public @Nullable Hologram getClosingHologram() {
        return closingHologram;
    }

    public @Nullable Function getTransitionFunction() {
        return onTransition;
    }

    public @Nullable Function getOpeningFunction() {
        return onOpening;
    }

    public @Nullable Function getClosingFunction() {
        return onClosing;
    }

    public @Nullable Function getClickFunction() {
        return onClick;
    }

    public @Nullable Function getLeftClickFunction() {
        return onLeftClick;
    }

    public @Nullable Function getRightClickFunction() {
        return onRightClick;
    }

    @Override
    public String toString() {
        return "GateStage{" +
                "gate=" + gate.getName() +
                ", structure=" + structure +
                ", onBreak=" + onBreak +
                ", onClick=" + onClick +
                '}';
    }

}
