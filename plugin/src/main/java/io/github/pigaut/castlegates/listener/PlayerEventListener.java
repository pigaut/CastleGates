package io.github.pigaut.castlegates.listener;

import io.github.pigaut.castlegates.*;
import io.github.pigaut.castlegates.api.event.*;
import io.github.pigaut.castlegates.gate.*;
import io.github.pigaut.castlegates.gate.stage.*;
import io.github.pigaut.castlegates.gate.template.*;
import io.github.pigaut.castlegates.player.*;
import io.github.pigaut.castlegates.util.*;
import io.github.pigaut.voxel.bukkit.*;
import io.github.pigaut.voxel.bukkit.Rotation;
import io.github.pigaut.voxel.core.function.*;
import io.github.pigaut.voxel.server.Server;
import org.bukkit.*;
import org.bukkit.block.*;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.block.*;
import org.bukkit.event.player.*;
import org.bukkit.inventory.*;

public class PlayerEventListener implements Listener {

    private final CastleGatesPlugin plugin;

    public PlayerEventListener(CastleGatesPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        Gate gate = plugin.getGate(block.getLocation());
        if (gate == null) {
            return;
        }

        event.setCancelled(true);
        Player player = event.getPlayer();
        GateUtil.mine(gate, player, block);
    }

    @EventHandler
    public void onBlockInteract(PlayerInteractEvent event) {
        if (!event.hasBlock() || event.getHand() != EquipmentSlot.HAND) {
            return;
        }

        if (event.hasItem() && GateTool.isValidItem(event.getItem())) {
            return;
        }

        Gate gate = plugin.getGate(event.getClickedBlock().getLocation());
        if (gate == null) {
            return;
        }

        if (!gate.isValid()) {
            gate.remove();
            return;
        }

        Player player = event.getPlayer();
        Action action = event.getAction();
        Block block = event.getClickedBlock();

        if (action == Action.RIGHT_CLICK_BLOCK) {
            event.setCancelled(true);
            if (player.hasPermission("orestack.build.on.generator") && event.hasItem()
                    && !MaterialUtil.isInteractable(block.getType())) {
                event.setCancelled(false);
            }
        }

        GateStage stage = gate.getStage();
        if (stage.getDecorativeBlocks().contains(block.getType())) {
            return;
        }

        GatesPlayer playerState = plugin.getPlayerState(player);
        if (playerState.hasFlag("castlegates:click_cooldown")) {
            return;
        }

        playerState.addTemporaryFlag("castlegates:click_cooldown", stage.getClickCooldown());
        GateInteractEvent gateInteractEvent = new GateInteractEvent(player, action);
        Server.callEvent(gateInteractEvent);
        if (!gateInteractEvent.isCancelled()) {
            playerState.updatePlaceholders(gate.getState());
            Function clickFunction = stage.getClickFunction();
            if (clickFunction != null) {
                clickFunction.run(playerState, event, block);
            }

            if (action == Action.LEFT_CLICK_BLOCK) {
                Function leftClickFunction = stage.getLeftClickFunction();
                if (leftClickFunction != null) {
                    leftClickFunction.run(playerState, event, block);
                }
            }

            if (action == Action.RIGHT_CLICK_BLOCK) {
                Function rightClickFunction = stage.getRightClickFunction();
                if (rightClickFunction != null) {
                    rightClickFunction.run(playerState, event, block);
                }
            }
        }
    }

    @EventHandler
    public void onItemClick(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }

        ItemStack heldItem = event.getItem();
        if (heldItem == null) {
            return;
        }

        GateTemplate heldGate = GateTool.getGateTemplate(heldItem);
        if (heldGate == null) {
            return;
        }

        Player player = event.getPlayer();
        Action action = event.getAction();

        if (action == Action.LEFT_CLICK_AIR && player.isSneaking()) {
            event.setCancelled(true);

            if (!player.hasPermission("castlegates.gate.rotate")) {
                plugin.sendMessage(player, "cannot-rotate-gate", heldGate);
                return;
            }

            GateTool.switchToolRotation(heldItem);
            PlayerUtil.sendActionBar(player, plugin.getTranslation("changed-gate-rotation"));
            return;
        }

        Block clickedBlock = event.getClickedBlock();
        if (clickedBlock == null) {
            return;
        }

        if (action == Action.LEFT_CLICK_BLOCK) {
            Gate clickedGate = plugin.getGate(clickedBlock.getLocation());
            if (clickedGate == null || !clickedGate.getTemplate().equals(heldGate)) {
                return;
            }

            event.setCancelled(true);

            if (!player.hasPermission("castlegates.gate.break")) {
                plugin.sendMessage(player, "cannot-break-gate", heldGate);
                return;
            }

            plugin.getGates().unregisterGate(clickedGate);
            PlayerUtil.sendActionBar(player, plugin.getTranslation("broke-gate"));
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Location location = event.getBlockPlaced().getLocation();

        if (plugin.getGates().isGate(location)) {
            plugin.sendMessage(player, "gate-occupied-block");
            event.setCancelled(true);
            return;
        }

        ItemStack heldItem = event.getItemInHand();
        if (!GateTool.isValidItem(heldItem)) {
            return;
        }

        event.setCancelled(true);
        GateTemplate gate = GateTool.getGateTemplate(heldItem);
        if (gate == null) {
            plugin.sendMessage(player, "gate-not-exists");
            return;
        }

        if (!player.hasPermission("castlegates.gate.place")) {
            plugin.sendMessage(player, "cannot-place-gate", gate);
            return;
        }

        Rotation rotation = GateTool.getRotation(heldItem);
        if (rotation == null) {
            plugin.sendMessage(player, "corrupt-tool-rotation", gate);
            return;
        }

        plugin.getRegionScheduler(location).runTaskLater(1, () -> {
            try {
                Gate.create(gate, location, rotation);
                PlayerUtil.sendActionBar(player, plugin.getTranslation("placed-gate"));
            }
            catch (GateOverlapException e) {
                PlayerUtil.sendActionBar(player, plugin.getTranslation("gate-overlap"));
            }
        });
    }

}
