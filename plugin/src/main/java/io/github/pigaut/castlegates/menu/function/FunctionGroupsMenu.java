package io.github.pigaut.castlegates.menu.function;

import io.github.pigaut.voxel.core.menu.*;
import io.github.pigaut.voxel.core.menu.button.*;
import io.github.pigaut.voxel.core.menu.template.menu.*;
import io.github.pigaut.voxel.core.menu.template.button.*;
import io.github.pigaut.voxel.plugin.*;
import io.github.pigaut.yaml.convert.format.*;
import org.bukkit.*;

import java.util.*;

public class FunctionGroupsMenu extends FramedSelectionMenu {

    private final EnhancedPlugin plugin;

    public FunctionGroupsMenu(EnhancedPlugin plugin) {
        super("Function Groups", MenuSize.BIG);
        this.plugin = plugin;
    }

    @Override
    public List<Button> createEntries() {
        return plugin.getFunctions().getAllGroups().stream()
                .map(group -> Button.builder()
                        .type(Material.CHEST)
                        .name("&8&l" + CaseFormatter.toTitleCase(group))
                        .addEmptyLine()
                        .addLine("&eLeft-Click: &fView all functions")
                        .onLeftClick((menuView, player) -> menuView.getViewer().openMenu(new FunctionsMenu(plugin, group)))
                        .buildButton())
                .toList();
    }

}
