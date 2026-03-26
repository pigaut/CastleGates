package io.github.pigaut.castlegates.menu.gate;

import io.github.pigaut.castlegates.*;
import io.github.pigaut.voxel.core.menu.*;
import io.github.pigaut.voxel.core.menu.template.menu.*;
import io.github.pigaut.voxel.core.menu.button.*;
import io.github.pigaut.voxel.core.menu.template.button.*;
import io.github.pigaut.yaml.convert.format.*;

import java.util.*;

public class GatesMenu extends FramedSelectionMenu {

    private final CastleGatesPlugin plugin;
    private final String group;

    public GatesMenu(CastleGatesPlugin plugin, String group) {
        super(CaseFormatter.toTitleCase(group) + " Gates", MenuSize.BIG);
        this.plugin = plugin;
        this.group = group;
    }

    @Override
    public List<Button> createEntries() {
        return plugin.getGateTemplates(group).stream()
                .map(gate -> {
                    String gateName = gate.getName();
                    return Button.builder()
                            .type(gate.getItemType())
                            .name("&6&o" + CaseFormatter.toTitleCase(gateName))
                            .addEmptyLine()
                            .addLine("&eLeft-Click: &fGet Gate")
                            .onLeftClick((menuView, player) ->
                                    player.performCommand("castlegates gate get " + gateName))
                            .buildButton();
                })
                .toList();
    }

    @Override
    public Button getToolbarButton4() {
        return Buttons.MAIN_MENU;
    }

}
