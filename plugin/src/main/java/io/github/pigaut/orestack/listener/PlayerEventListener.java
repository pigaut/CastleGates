package io.github.pigaut.orestack.listener;

import io.github.pigaut.orestack.*;
import io.github.pigaut.orestack.api.event.*;
import io.github.pigaut.orestack.generator.*;
import io.github.pigaut.orestack.generator.template.*;
import io.github.pigaut.orestack.hook.veinminer.*;
import io.github.pigaut.orestack.player.*;
import io.github.pigaut.orestack.settings.*;
import io.github.pigaut.orestack.util.*;
import io.github.pigaut.voxel.bukkit.Rotation;
import io.github.pigaut.voxel.core.function.*;
import io.github.pigaut.voxel.player.*;
import io.github.pigaut.voxel.server.*;
import io.github.pigaut.voxel.server.Server;
import org.bukkit.*;
import org.bukkit.block.*;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.block.*;
import org.bukkit.event.player.*;
import org.bukkit.inventory.*;

public class PlayerEventListener implements Listener {

    private final OrestackPlugin plugin;

    public PlayerEventListener(OrestackPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        Generator generator = plugin.getGenerator(block.getLocation());
        if (generator == null) {
            return;
        }

        event.setCancelled(true);

        Player player = event.getPlayer();
        int expToDrop = event.getExpToDrop();

        OrestackSettings settings = plugin.getSettings();
        if (settings.isVeinMiner()) {
            int maxVeinSize = settings.getToolMaxVeinSize(player.getInventory().getItemInMainHand());
            if (maxVeinSize > 1) {
                GeneratorBlockVein.mineBlocks(generator, player, maxVeinSize, expToDrop);
                return;
            }
        }

        GeneratorBlock.mineBlock(generator, player, block, expToDrop);
    }

    @EventHandler
    public void onBlockInteract(PlayerInteractEvent event) {
        if (!event.hasBlock() || event.getHand() != EquipmentSlot.HAND) {
            return;
        }

        if (event.hasItem() && GeneratorTool.isValidItem(event.getItem())) {
            return;
        }

        Generator generator = plugin.getGenerator(event.getClickedBlock().getLocation());
        if (generator == null) {
            return;
        }

        Action action = event.getAction();
        if (action == Action.RIGHT_CLICK_BLOCK) {
            event.setCancelled(true);
        }

        if (!generator.matchBlocks()) {
            plugin.getGenerators().unregisterGenerator(generator);
            return;
        }

        Block block = event.getClickedBlock();
        GeneratorStage stage = generator.getCurrentStage();
        if (stage.getDecorativeBlocks().contains(block.getType())) {
            return;
        }

        Player player = event.getPlayer();
        OrestackPlayer playerState = plugin.getPlayerState(player);
        if (!playerState.hasFlag("orestack:click_cooldown")) {
            playerState.addTemporaryFlag("orestack:click_cooldown", stage.getClickCooldown());

            GeneratorInteractEvent generatorInteractEvent = new GeneratorInteractEvent(player, action);
            Server.callEvent(generatorInteractEvent);
            if (!generatorInteractEvent.isCancelled()) {
                playerState.updatePlaceholders(generator);
                Function clickFunction = stage.getClickFunction();
                if (clickFunction != null) {
                    clickFunction.run(playerState, event, block);
                }
            }
        }

        if (action == Action.LEFT_CLICK_BLOCK && !playerState.hasFlag("orestack:hit_cooldown")) {
            playerState.addTemporaryFlag("orestack:hit_cooldown", stage.getHitCooldown());

            GeneratorHitEvent generatorHitEvent = new GeneratorHitEvent(player);
            Server.callEvent(generatorHitEvent);
            if (!generatorHitEvent.isCancelled()) {
                playerState.updatePlaceholders(generator);
                Function hitFunction = stage.getHitFunction();
                if (hitFunction != null) {
                    hitFunction.run(playerState, event, block);
                }
            }
        }

        if (action == Action.RIGHT_CLICK_BLOCK && !playerState.hasFlag("orestack:harvest_cooldown")) {
            playerState.addTemporaryFlag("orestack:harvest_cooldown", stage.getHarvestCooldown());

            GeneratorHarvestEvent generatorHarvestEvent = new GeneratorHarvestEvent(player);
            Server.callEvent(generatorHarvestEvent);
            if (!generatorHarvestEvent.isCancelled()) {
                playerState.updatePlaceholders(generator);
                Function harvestFunction = stage.getHarvestFunction();
                if (harvestFunction != null) {
                    harvestFunction.run(playerState, event, block);
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

        GeneratorTemplate heldGenerator = GeneratorTool.getGeneratorTemplate(heldItem);
        if (heldGenerator == null) {
            return;
        }

        Player player = event.getPlayer();
        Action action = event.getAction();

        if (action == Action.LEFT_CLICK_AIR && player.isSneaking()) {
            event.setCancelled(true);

            if (!player.hasPermission("orestack.generator.rotate")) {
                plugin.sendMessage(player, "cannot-rotate-generator", heldGenerator);
                return;
            }

            GeneratorTool.switchToolRotation(heldItem);
            PlayerUtil.sendActionBar(player, plugin.getTranslation("changed-generator-rotation"));
            return;
        }

        Block clickedBlock = event.getClickedBlock();

        if (action == Action.LEFT_CLICK_BLOCK) {
            Generator clickedGenerator = plugin.getGenerator(clickedBlock.getLocation());
            if (clickedGenerator == null || !clickedGenerator.getTemplate().equals(heldGenerator)) {
                return;
            }

            event.setCancelled(true);

            if (!player.hasPermission("orestack.generator.break")) {
                plugin.sendMessage(player, "cannot-break-generator", heldGenerator);
                return;
            }

            plugin.getGenerators().unregisterGenerator(clickedGenerator);
            PlayerUtil.sendActionBar(player, plugin.getTranslation("broke-generator"));
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlockPlaced();
        Location location = block.getLocation();

        if (plugin.getGenerators().isGenerator(location)) {
            plugin.sendMessage(player, "generator-occupied-block");
            event.setCancelled(true);
            return;
        }

        ItemStack heldItem = event.getItemInHand();
        if (!GeneratorTool.isValidItem(heldItem)) {
            return;
        }

        event.setCancelled(true);
        GeneratorTemplate generator = GeneratorTool.getGeneratorTemplate(heldItem);
        if (generator == null) {
            plugin.sendMessage(player, "generator-not-exists");
            return;
        }

        if (!player.hasPermission("orestack.generator.place")) {
            plugin.sendMessage(player, "cannot-place-generator", generator);
            return;
        }

        Rotation rotation = GeneratorTool.getRotation(heldItem);
        if (rotation == null) {
            plugin.sendMessage(player, "corrupt-tool-rotation", generator);
            return;
        }

        plugin.getRegionScheduler(location).runTaskLater(1, () -> {
            try {
                Generator.create(generator, location, rotation);
                PlayerUtil.sendActionBar(player, plugin.getTranslation("placed-generator"));
            }
            catch (GeneratorOverlapException e) {
                PlayerUtil.sendActionBar(player, plugin.getTranslation("generator-overlap"));
            }
        });
    }

}
