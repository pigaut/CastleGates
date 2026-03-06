package io.github.pigaut.orestack.generator;

import io.github.pigaut.orestack.*;
import io.github.pigaut.orestack.api.event.*;
import io.github.pigaut.orestack.generator.stage.*;
import io.github.pigaut.orestack.player.*;
import io.github.pigaut.voxel.bukkit.*;
import io.github.pigaut.voxel.core.function.*;
import io.github.pigaut.voxel.player.*;
import io.github.pigaut.voxel.plugin.task.*;
import io.github.pigaut.voxel.server.Server;
import org.bukkit.*;
import org.bukkit.block.*;
import org.bukkit.entity.*;
import org.bukkit.inventory.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class GeneratorUtil {

    private static final OrestackPlugin plugin = OrestackPlugin.getInstance();

    public static void mineBlock(@NotNull Generator generator, @NotNull Player player, @NotNull Block block, int expToDrop) {
        if (!generator.isValid()) {
            generator.remove();
            return;
        }

        GeneratorStage generatorStage = generator.getStage();
        if (generatorStage.getDecorativeBlocks().contains(block.getType())) {
            return;
        }

        OrestackPlayer playerState = plugin.getPlayerState(player);
        playerState.updatePlaceholders(generator.getState());

        GeneratorMineEvent generatorMineEvent = new GeneratorMineEvent(player, block);
        {
            if (generatorStage.isIdle()) {
                generatorMineEvent.setIdle(true);
            }

            if (generatorStage.isDropItems()) {
                ItemStack tool = player.getInventory().getItemInMainHand();
                generatorMineEvent.setItemDrops(block.getDrops(tool));
            }

            if (generatorStage.isDropExp()) {
                generatorMineEvent.setExpDrops(expToDrop);
            }

            int toolDamage = generatorStage.getToolDamage().intValue();
            generatorMineEvent.setToolDamage(toolDamage);

            Server.callEvent(generatorMineEvent);
        }

        if (!generatorMineEvent.isCancelled()) {
            Function breakFunction = generatorStage.getBreakFunction();
            if (breakFunction != null) {
                breakFunction.run(playerState, generatorMineEvent, block);
            }
        }

        if (!generatorMineEvent.isCancelled() && generatorStage.getState().isHarvestable()) {
            Location dropLocation = block.getLocation().add(0.5, 1, 0.5);

            Collection<ItemStack> itemDrops = generatorMineEvent.getItemDrops();
            if (itemDrops != null) {
                for (ItemStack itemDrop : itemDrops) {
                    ItemUtil.dropItem(dropLocation, itemDrop);
                }
            }

            int expDrops = generatorMineEvent.getExpDrops();
            if (expDrops != 0) {
                Exp.drop(dropLocation, expDrops);
            }

            int toolDamage = generatorMineEvent.getToolDamage();
            if (toolDamage != 0) {
                ItemUtil.damagePlayerTool(player, toolDamage);
            }

            if (!generatorMineEvent.isIdle()) {
                generator.harvest();
            }
        }
    }

    public static void startGrowth(Generator generator, Location origin, int growthTime) {
        GeneratorState state = generator.getState();

        Task growthTask = plugin.getScheduler().runTaskLater(growthTime, () -> {
            state.setGrowthTask(null);

            if (generator.isFullyGrown()) {
                return;
            }

            GeneratorGrowthEvent growthEvent = new GeneratorGrowthEvent(origin);
            Server.callEvent(growthEvent);

            if (!growthEvent.isCancelled()) {
                generator.grow();
            }
        });
        state.setGrowthTask(growthTask);

    }

    public static void damage(@NotNull Generator generator, @NotNull PlayerState damageDealer, double damage) {
        Double health = generator.getHealth();
        if (health == null) {
            return;
        }

        double newHealth = Math.max(0, health - damage);
        if (newHealth != 0) {
            generator.setHealth(newHealth);
            return;
        }

        GeneratorDestroyEvent event = new GeneratorDestroyEvent(damageDealer.asPlayer());
        Server.callEvent(event);
        if (event.isCancelled()) {
            return;
        }

        Function onDestroy = generator.getStage().getDestroyFunction();
        if (onDestroy != null) {
            onDestroy.dispatch(damageDealer, event, generator.getBlock(), null);
        }

        generator.harvest();
    }

}


