package io.github.pigaut.castlegates.hook.plotsquared;

import io.github.pigaut.castlegates.*;
import io.github.pigaut.voxel.server.*;
import org.bukkit.block.*;
import org.bukkit.event.*;
import org.bukkit.event.block.*;

// Register this listener after PlotSquared is enabled to override its block-damage handler (highest priority).
public class PlotBlockDamageListener implements Listener {

    private final CastleGatesPlugin plugin;

    public PlotBlockDamageListener(CastleGatesPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onDamageLowest(BlockDamageEvent event) {
        Block block = event.getBlock();
        if (plugin.getGates().isGate(block.getLocation())) {
            // Cancel so that PlotSquared block break handler ignores this event
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDamage(BlockDamageEvent event) {
        Block block = event.getBlock();
        if (plugin.getGates().isGate(block.getLocation())) {
            event.setCancelled(false);
        }
    }

}
