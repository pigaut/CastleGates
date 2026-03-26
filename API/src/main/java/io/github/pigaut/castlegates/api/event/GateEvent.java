package io.github.pigaut.castlegates.api.event;

import org.bukkit.*;
import org.bukkit.event.*;
import org.jetbrains.annotations.*;

public abstract class GateEvent extends Event implements Cancellable {

    private final Location origin;
    private final String gate;
    private final int phase;
    private boolean cancelled = false;

    protected GateEvent(Location origin, String gate, int phase) {
        this.origin = origin;
        this.gate = gate;
        this.phase = phase;
    }

    /**
     * Gets the origin location of the gate.
     * <p>
     * The origin represents the center where the gate is placed in the world.
     *
     * @return the {@link Location} of the gate's origin
     */
    public @NotNull Location getOrigin() {
        return origin;
    }

    /**
     * Returns the name of the gate that triggered the event.
     *
     * @return the name of the gate
     */
    public @NotNull String getGate() {
        return gate;
    }

    /**
     * Returns the current phase of the gate that triggered the event.
     *
     * @return the current phase of the gate
     */
    public int getGatePhase() {
        return phase;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }


}
