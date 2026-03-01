package io.github.pigaut.castlegates;

import io.github.pigaut.castlegates.gate.template.*;
import io.github.pigaut.castlegates.settings.*;
import io.github.pigaut.yaml.amount.*;
import org.bukkit.*;
import org.bukkit.block.*;
import org.bukkit.enchantments.*;
import org.bukkit.entity.*;
import org.bukkit.inventory.*;
import org.jetbrains.annotations.*;

public class CastleGates {

    private static final CastleGatesPlugin plugin = CastleGatesPlugin.getInstance();

    public static GateTemplate getGateTemplate(String name) {
        return plugin.getGateTemplate(name);
    }

    public static boolean isGate(Location location) {
        return plugin.getGates().isGate(location);
    }

}
