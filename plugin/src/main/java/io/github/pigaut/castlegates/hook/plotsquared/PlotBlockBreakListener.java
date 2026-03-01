package io.github.pigaut.castlegates.hook.plotsquared;

import io.github.pigaut.castlegates.*;
import org.bukkit.block.*;
import org.bukkit.event.*;
import org.bukkit.event.block.*;

// Register this listener before PlotSquared is enabled to override its block-break handler (lowest priority).
public class PlotBlockBreakListener implements Listener {

    private final CastleGatesPlugin plugin;

    public PlotBlockBreakListener(CastleGatesPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        if (plugin.getGates().isGate(block.getLocation())) {
            // Cancel so that PlotSquared block break handler ignores this event
            event.setCancelled(true);
        }
    }

}
