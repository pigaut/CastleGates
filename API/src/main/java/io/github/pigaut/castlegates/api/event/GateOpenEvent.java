package io.github.pigaut.castlegates.api.event;

import org.bukkit.*;
import org.bukkit.event.*;
import org.jetbrains.annotations.*;

public class GateOpenEvent extends GateTransitionEvent {

    private static final HandlerList HANDLERS = new HandlerList();

    public GateOpenEvent(@NotNull Location origin) {
        super(origin, true);
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

}
