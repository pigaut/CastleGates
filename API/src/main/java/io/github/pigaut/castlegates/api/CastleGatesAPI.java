package io.github.pigaut.castlegates.api;

import org.bukkit.*;
import org.bukkit.entity.*;
import org.jetbrains.annotations.*;

public interface CastleGatesAPI {
    
    /**
     * Checks whether a gate with the given name exists.
     *
     * @param name the gate identifier
     * @return true if a gate with this name exists, otherwise false
     */
    boolean isGate(@NotNull String name);

    /**
     * Checks whether the specified location is a gate.
     *
     * @param location the location to check
     * @return true if the location is a gate, otherwise false
     */
    boolean isGate(@NotNull Location location);

    /**
     * Opens the gate at the specified location.
     *
     * @param location the location of the gate to open
     * @throws IllegalArgumentException if the location is not a gate
     */
    void open(@NotNull Location location);

    /**
     * Closes the gate at the specified location.
     *
     * @param location the location of the gate to close
     * @throws IllegalArgumentException if the location is not a gate
     */
    void close(@NotNull Location location);

    /**
     * Applies damage to the gate on behalf of a player.
     *
     * @param location the gate location
     * @param player the player dealing the damage
     * @param amount the amount of damage to apply
     * @throws IllegalArgumentException if the location is not a gate
     */
    void damage(@NotNull Location location, @NotNull Player player, int amount) throws IllegalArgumentException;
    
}
