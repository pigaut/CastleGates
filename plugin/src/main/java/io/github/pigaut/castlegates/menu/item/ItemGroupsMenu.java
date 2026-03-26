package io.github.pigaut.castlegates.menu.item;

import io.github.pigaut.castlegates.*;
import io.github.pigaut.voxel.core.menu.*;
import io.github.pigaut.voxel.core.menu.button.*;
import io.github.pigaut.voxel.core.menu.template.menu.*;
import io.github.pigaut.voxel.core.menu.template.button.*;
import io.github.pigaut.yaml.convert.format.*;
import org.bukkit.*;

import java.util.*;

public class ItemGroupsMenu extends FramedSelectionMenu {

    private final CastleGatesPlugin plugin;

    public ItemGroupsMenu(CastleGatesPlugin plugin) {
        super("Item Groups", MenuSize.BIG);
        this.plugin = plugin;
    }

    @Override
    public List<Button> createEntries() {
        return plugin.getItems().getAllGroups().stream()
                .map(group -> Button.builder()
                        .type(Material.CHEST)
                        .name("&a&l" + CaseFormatter.toTitleCase(group))
                        .addEmptyLine()
                        .addLine("&eLeft-Click: &fView all items")
                        .addLine("&6Right-Click: &fGet all items")
                        .onLeftClick((view, player) ->
                                player.openMenu(new ItemsMenu(plugin, group)))
                        .onRightClick((view, player) ->
                                player.performCommand("castlegates item get-group " + group))
                        .buildButton())
                .toList();
    }

}
