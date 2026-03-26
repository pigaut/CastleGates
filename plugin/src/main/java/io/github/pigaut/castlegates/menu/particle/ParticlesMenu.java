package io.github.pigaut.castlegates.menu.particle;

import io.github.pigaut.voxel.core.menu.*;
import io.github.pigaut.voxel.core.menu.template.menu.*;
import io.github.pigaut.voxel.core.menu.button.*;
import io.github.pigaut.voxel.core.menu.template.button.*;
import io.github.pigaut.voxel.plugin.*;
import io.github.pigaut.yaml.convert.format.*;
import org.bukkit.*;

import java.util.*;

public class ParticlesMenu extends FramedSelectionMenu {

    private final EnhancedPlugin plugin;
    private final String group;

    public ParticlesMenu(EnhancedPlugin plugin, String group) {
        super(CaseFormatter.toTitleCase(group) + " Particle Effects", MenuSize.BIG);
        this.plugin = plugin;
        this.group = group;
    }

    @Override
    public List<Button> createEntries() {
        return plugin.getParticles().getAll(group).stream()
                .map(particle -> Button.builder()
                        .type(particle.getIcon().getType())
                        .name("&d&o" + CaseFormatter.toTitleCase(particle.getName()))
                        .addEmptyLine()
                        .addLine("&eLeft-Click: &fShow-me particle")
                        .onLeftClick((view, player) -> {
                            view.close();
                            player.performCommand("castlegates particle show-me " + particle.getName());
                            final int guiReopenDelay = plugin.getSettings().guiReopenDelay;
                            player.sendMessage(ChatColor.RED + "The menu will reopen in " + (guiReopenDelay / 20) + " seconds...");
                            plugin.getScheduler().runTaskLater(guiReopenDelay, view::open);
                        })
                        .buildButton())
                .toList();
    }

    @Override
    public Button getToolbarButton4() {
        return Buttons.MAIN_MENU;
    }

}
