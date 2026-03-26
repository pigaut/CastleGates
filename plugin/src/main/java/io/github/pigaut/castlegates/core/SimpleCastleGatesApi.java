package io.github.pigaut.castlegates.core;

import io.github.pigaut.castlegates.*;
import io.github.pigaut.castlegates.api.*;
import io.github.pigaut.castlegates.gate.*;
import io.github.pigaut.castlegates.gate.template.*;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.jetbrains.annotations.*;

public class SimpleCastleGatesApi implements CastleGatesAPI {

    private final CastleGatesPlugin plugin;

    public SimpleCastleGatesApi(CastleGatesPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean isGate(@NotNull String name) {
        GateTemplate gateTemplate = plugin.getGateTemplate(name);
        return gateTemplate != null;
    }

    @Override
    public boolean isGate(@NotNull Location location) {
        Gate gate = plugin.getGate(location);
        return gate != null;
    }

    private @NotNull Gate getGate(@NotNull Location location) throws IllegalArgumentException {
        Gate gate = plugin.getGate(location);
        if (gate == null) {
            throw new IllegalArgumentException("Location does not belong to a gate.");
        }
        return gate;
    }

    @Override
    public void open(@NotNull Location location) {
        Gate gate = getGate(location);
        gate.open();
    }

    @Override
    public void close(@NotNull Location location) {
        Gate gate = getGate(location);
        gate.close();
    }

    @Override
    public void damage(@NotNull Location location, @NotNull Player player, int amount) throws IllegalArgumentException {

    }

}
