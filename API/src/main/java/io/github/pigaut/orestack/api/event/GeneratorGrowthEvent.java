package io.github.pigaut.orestack.api.event;

import io.github.pigaut.voxel.event.*;
import org.bukkit.*;
import org.bukkit.block.*;
import org.bukkit.event.*;
import org.jetbrains.annotations.*;

/**
 * Called when a generator grows to the next stage.
 */
public class GeneratorGrowthEvent extends CancellableEvent {

    private static final HandlerList HANDLERS = new HandlerList();

    private final Location origin;

    public GeneratorGrowthEvent(@NotNull Location origin) {
        this.origin = origin;
    }

    /**
     * Gets the origin location of the generator.
     * <p>
     * The origin represents the center where the generator is placed in the world.
     *
     * @return the non-null {@link Location} of the generator's origin
     */
    public @NotNull Location getOrigin() {
        return origin;
    }

    /**
     * Gets the block located at the generator's origin location.
     *
     * @return the non-null {@link Block} corresponding to this generator
     */
    public @NotNull Block getBlock() {
        return origin.getBlock();
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

}
