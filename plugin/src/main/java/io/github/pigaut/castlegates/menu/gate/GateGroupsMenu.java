package io.github.pigaut.castlegates.menu.gate;

import io.github.pigaut.castlegates.*;
import io.github.pigaut.voxel.menu.*;
import io.github.pigaut.voxel.menu.button.*;
import io.github.pigaut.voxel.menu.template.menu.*;
import io.github.pigaut.yaml.convert.format.*;
import org.bukkit.*;

import java.util.*;

public class GateGroupsMenu extends FramedSelectionMenu {

    private final CastleGatesPlugin plugin;

    public GateGroupsMenu(CastleGatesPlugin plugin) {
        super("Gate Groups", MenuSize.BIG);
        this.plugin = plugin;
    }

    @Override
    public List<Button> createEntries() {
        return plugin.getGateTemplates().getAllGroups().stream()
                .map(group -> Button.builder()
                        .type(Material.CHEST)
                        .name("&6&l" + CaseFormatter.toTitleCase(group))
                        .addEmptyLine()
                        .addLine("&eLeft-Click: &fView all gates")
                        .addLine("&6Right-Click: &fGet all gates")
                        .onLeftClick((menuView, player) ->
                                player.openMenu(new GatesMenu(plugin, group)))
                        .onRightClick((menuView, player) ->
                                player.performCommand("castlegates gate get-group " + group))
                        .buildButton())
                .toList();
    }

}
