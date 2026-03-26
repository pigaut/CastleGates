package io.github.pigaut.castlegates.api.event;

import org.bukkit.*;
import org.bukkit.block.*;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class GatePlaceEvent extends GateEvent {

    private static final HandlerList HANDLERS = new HandlerList();

    private final Player player;
    private final Set<Block> occupiedBlocks;

    public GatePlaceEvent(Player player, Location origin, String gate, Set<Block> occupiedBlocks) {
        super(origin, gate, 0);
        this.player = player;
        this.occupiedBlocks = occupiedBlocks;
    }

    /**
     * Gets the player who placed the generator.
     *
     * @return the player who placed the generator
     */
    public @NotNull Player getPlayer() {
        return player;
    }

    /**
     * Gets the blocks occupied by the generator.
     *
     * @return a copy of the occupied blocks
     */
    public @NotNull Set<Block> getOccupiedBlocks() {
        return new HashSet<>(occupiedBlocks);
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

}
