package io.github.pigaut.castlegates.menu.particle;

import io.github.pigaut.castlegates.*;
import io.github.pigaut.voxel.menu.*;
import io.github.pigaut.voxel.menu.button.*;
import io.github.pigaut.voxel.menu.template.menu.*;
import io.github.pigaut.yaml.convert.format.*;
import org.bukkit.*;

import java.util.*;

public class ParticleGroupsMenu extends FramedSelectionMenu {

    private final CastleGatesPlugin plugin;

    public ParticleGroupsMenu(CastleGatesPlugin plugin) {
        super("Particle Effect Groups", MenuSize.BIG);
        this.plugin = plugin;
    }

    @Override
    public List<Button> createEntries() {
        return plugin.getParticles().getAllGroups().stream()
                .map(group -> Button.builder()
                        .type(Material.CHEST)
                        .name("&d&l" + CaseFormatter.toTitleCase(group))
                        .addEmptyLine()
                        .addLine("&eLeft-Click: &fView all particle effects")
                        .onLeftClick((view, player) -> player.openMenu(new ParticlesMenu(plugin, group)))
                        .buildButton())
                .toList();
    }

}
