package io.github.pigaut.castlegates.api.event;

import org.bukkit.*;
import org.bukkit.block.*;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.block.*;
import org.jetbrains.annotations.*;

/**
 * Called when a player interacts with a non-decorative gate block.
 */
public class GateInteractEvent extends GateEvent {

    private static final HandlerList HANDLERS = new HandlerList();

    private final Player player;
    private final Action action;
    private final Block clickedBlock;

    public GateInteractEvent(Player player, Action action, Block clickedBlock,
                             Location origin, String gate, int phase) {
        super(origin, gate, phase);
        this.player = player;
        this.action = action;
        this.clickedBlock = clickedBlock;
    }

    public @NotNull Player getPlayer() {
        return player;
    }

    public @NotNull Action getAction() {
        return action;
    }

    public @NotNull Block getClickedBlock() {
        return clickedBlock;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static @NotNull HandlerList getHandlerList() {
        return HANDLERS;
    }

}
