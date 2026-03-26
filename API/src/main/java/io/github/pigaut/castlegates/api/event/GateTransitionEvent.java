package io.github.pigaut.castlegates.api.event;

import org.bukkit.*;
import org.bukkit.block.*;
import org.bukkit.event.*;
import org.jetbrains.annotations.*;

/**
 * Called when a gate transition to another phase.
 */
public abstract class GateTransitionEvent extends GateEvent {

    private static final HandlerList HANDLERS = new HandlerList();

    private final Location origin;
    private final boolean opening;

    public GateTransitionEvent(Location origin, String gate, int phase, boolean opening) {
        super(origin, gate, phase);
        this.origin = origin;
        this.opening = opening;
    }

    /**
     * Gets the block located at the gate's origin location.
     *
     * @return the non-null {@link Block} corresponding to this gate
     */
    public @NotNull Block getBlock() {
        return origin.getBlock();
    }

    /**
     * Gets whether the gate is opening
     *
     * @return true if the gate is opening, false if it is closing
     */
    public boolean isOpening() {
        return opening;
    }

    /**
     * Gets whether the gate is closing
     *
     * @return true if the gate is closing, false if it is opening
     */
    public boolean isClosing() {
        return !opening;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

}
