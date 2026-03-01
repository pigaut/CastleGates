package io.github.pigaut.castlegates.menu.sound;

import io.github.pigaut.voxel.menu.*;
import io.github.pigaut.voxel.menu.button.*;
import io.github.pigaut.voxel.menu.template.button.*;
import io.github.pigaut.voxel.menu.template.menu.*;
import io.github.pigaut.voxel.plugin.*;
import io.github.pigaut.yaml.convert.format.*;

import java.util.*;

public class SoundsMenu extends FramedSelectionMenu {

    private final EnhancedPlugin plugin;
    private final String group;

    public SoundsMenu(EnhancedPlugin plugin, String group) {
        super(CaseFormatter.toTitleCase(group) + " Sound Effects", MenuSize.BIG);
        this.plugin = plugin;
        this.group = group;
    }

    @Override
    public List<Button> createEntries() {
        return plugin.getSounds().getAll(group).stream()
                .map(sound -> Button.builder()
                        .type(sound.getIcon().getType())
                        .name("&3&o" + CaseFormatter.toTitleCase(sound.getName()))
                        .addEmptyLine()
                        .addLine("&eLeft-Click: &fPlay-me sound")
                        .onLeftClick((menuView, player) -> {
                            sound.play(player.asPlayer(), player.getLocation());
                        })
                        .buildButton())
                .toList();
    }

    @Override
    public Button getToolbarButton4() {
        return Buttons.MAIN_MENU;
    }

}
