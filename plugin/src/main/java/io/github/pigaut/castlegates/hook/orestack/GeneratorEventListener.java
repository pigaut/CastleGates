package io.github.pigaut.castlegates.hook.orestack;

import io.github.pigaut.castlegates.*;
import io.github.pigaut.orestack.api.event.*;
import org.bukkit.block.*;
import org.bukkit.event.*;

public class GeneratorEventListener implements Listener {

    private final CastleGatesPlugin plugin;

    public GeneratorEventListener(CastleGatesPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlace(GeneratorPlaceEvent event) {
        for (Block block : event.getOccupiedBlocks()) {
            if (plugin.getGates().isGate(block.getLocation())) {
                event.setCancelled(true);
                return;
            }
        }
    }

}
