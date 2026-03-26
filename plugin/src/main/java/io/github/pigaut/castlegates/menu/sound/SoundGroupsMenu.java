package io.github.pigaut.castlegates.menu.sound;

import io.github.pigaut.voxel.core.menu.*;
import io.github.pigaut.voxel.core.menu.button.*;
import io.github.pigaut.voxel.core.menu.template.menu.*;
import io.github.pigaut.voxel.core.menu.template.button.*;
import io.github.pigaut.voxel.plugin.*;
import io.github.pigaut.yaml.convert.format.*;
import org.bukkit.*;

import java.util.*;

public class SoundGroupsMenu extends FramedSelectionMenu {

    private final EnhancedPlugin plugin;

    public SoundGroupsMenu(EnhancedPlugin plugin) {
        super("Sound Effect Groups", MenuSize.BIG);
        this.plugin = plugin;
    }

    @Override
    public List<Button> createEntries() {
        return plugin.getSounds().getAllGroups().stream()
                .map(group -> Button.builder()
                        .type(Material.CHEST)
                        .name("&3&l" + CaseFormatter.toTitleCase(group))
                        .addEmptyLine()
                        .addLine("&eLeft-Click: &fView all sound effects")
                        .onLeftClick((view, player) -> player.openMenu(new SoundsMenu(plugin, group)))
                        .buildButton())
                .toList();
    }

}
