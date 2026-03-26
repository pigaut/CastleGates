package io.github.pigaut.castlegates.listener;

import io.github.pigaut.castlegates.*;
import io.github.pigaut.castlegates.api.event.*;
import io.github.pigaut.castlegates.core.*;
import io.github.pigaut.castlegates.gate.*;
import io.github.pigaut.castlegates.gate.template.*;
import io.github.pigaut.castlegates.player.*;
import io.github.pigaut.voxel.bukkit.*;
import io.github.pigaut.voxel.bukkit.Rotation;
import io.github.pigaut.voxel.core.context.*;
import io.github.pigaut.voxel.data.function.*;
import io.github.pigaut.voxel.util.Server;
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
    public void handleGateBlockBreak(BlockBreakEvent event) {
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
    public void handleGateBlockInteract(PlayerInteractEvent event) {
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
            if (player.hasPermission("castlegates.build.on.gate") && event.hasItem()
                    && !MaterialUtil.isInteractable(block.getType())) {
                event.setCancelled(false);
            }
        }

        GatePhase phase = gate.getPhase();
        if (phase.getDecorativeBlocks().contains(block.getType())) {
            return;
        }

        GatesPlayer playerState = plugin.getPlayerState(player);
        if (playerState.hasFlag("castlegates:click_cooldown")) {
            return;
        }
        playerState.addTemporaryFlag("castlegates:click_cooldown", phase.getClickCooldown());

        GateInteractEvent gateInteractEvent = new GateInteractEvent(player, action, block,
                gate.getOrigin(), gate.getName(), gate.getState().getCurrentPhase());
        Server.callEvent(gateInteractEvent);

        Context context = Context.builder()
                .withPlayer(player)
                .withPlayerState(playerState)
                .withAction(action)
                .withTool(player.getInventory().getItemInMainHand())
                .withBlock(block)
                .withEvent(gateInteractEvent)
                .with(Gate.class, gate)
                .build();

        if (!gateInteractEvent.isCancelled()) {
            Function clickFunction = phase.getClickFunction();
            if (clickFunction != null) {
                clickFunction.run(context);
            }

            if (action == Action.LEFT_CLICK_BLOCK) {
                Function leftClickFunction = phase.getLeftClickFunction();
                if (leftClickFunction != null) {
                    leftClickFunction.run(context);
                }
            }

            if (action == Action.RIGHT_CLICK_BLOCK) {
                Function rightClickFunction = phase.getRightClickFunction();
                if (rightClickFunction != null) {
                    rightClickFunction.run(context);
                }
            }
        }
    }

    // Click functions are not triggering

    @EventHandler
    public void handleGateItemInteract(PlayerInteractEvent event) {
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
        Block clickedBlock = event.getClickedBlock();
        Context context = Context.builder()
                .withPlayer(player)
                .withPlayerState(plugin.getPlayerState(player))
                .withTool(heldItem)
                .withBlock(clickedBlock)
                .build();

        if (action == Action.LEFT_CLICK_AIR && player.isSneaking()) {
            event.setCancelled(true);

            if (!player.hasPermission("castlegates.gate.rotate")) {
                plugin.sendMessage(player, context, "cannot-rotate-gate");
                return;
            }

            GateTool.switchToolRotation(heldItem);
            PlayerUtil.sendActionBar(player, plugin.getTranslation("changed-gate-rotation"));
            return;
        }

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
                plugin.sendMessage(player, context, "cannot-break-gate");
                return;
            }

            plugin.getGates().unregisterGate(clickedGate);
            PlayerUtil.sendActionBar(player, plugin.getTranslation("broke-gate"));
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void handleGatePlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Location location = event.getBlockPlaced().getLocation();
        Context context = Context.builder()
                .withPlayer(player)
                .withPlayerState(plugin.getPlayerState(player))
                .withTool(event.getItemInHand())
                .withBlock(event.getBlockPlaced())
                .build();

        if (plugin.getGates().isGate(location)) {
            plugin.sendMessage(player, context, "gate-occupied-block");
            event.setCancelled(true);
            return;
        }

        ItemStack heldItem = event.getItemInHand();
        if (!GateTool.isValidItem(heldItem)) {
            return;
        }

        event.setCancelled(true);
        GateTemplate gateTemplate = GateTool.getGateTemplate(heldItem);
        if (gateTemplate == null) {
            plugin.sendMessage(player, context, "gate-not-exists");
            return;
        }

        context = context.with(GateTemplate.class, gateTemplate);
        if (!player.hasPermission("castlegates.gate.place")) {
            plugin.sendMessage(player, context, "cannot-place-gate");
            return;
        }

        Rotation rotation = GateTool.getRotation(heldItem);
        if (rotation == null) {
            plugin.sendMessage(player, context, "corrupt-tool-rotation");
            return;
        }

        GatePlaceEvent gatePlaceEvent = new GatePlaceEvent(player, location, gateTemplate.getName(), gateTemplate.getOccupiedBlocks(location, rotation));
        Server.callEvent(gatePlaceEvent);

        if (gatePlaceEvent.isCancelled()) {
            PlayerUtil.sendActionBar(player, plugin.getTranslation("gate-conflict"));
            return;
        }

        plugin.getRegionScheduler(location).runTaskLater(1, () -> {
            try {
                Gate.create(gateTemplate, location, rotation);
                PlayerUtil.sendActionBar(player, plugin.getTranslation("placed-gate"));
            }
            catch (GateOverlapException e) {
                PlayerUtil.sendActionBar(player, plugin.getTranslation("gate-overlap"));
            }
        });
    }

}
