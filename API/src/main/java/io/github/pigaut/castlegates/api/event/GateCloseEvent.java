package io.github.pigaut.castlegates.api.event;

import org.bukkit.*;
import org.bukkit.event.*;
import org.jetbrains.annotations.*;

public class GateCloseEvent extends GateTransitionEvent {

    private static final HandlerList HANDLERS = new HandlerList();

    public GateCloseEvent(@NotNull Location origin) {
        super(origin, false);
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

}
