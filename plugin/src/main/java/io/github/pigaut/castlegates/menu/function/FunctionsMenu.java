package io.github.pigaut.castlegates.menu.function;

import io.github.pigaut.voxel.core.context.*;
import io.github.pigaut.voxel.core.menu.*;
import io.github.pigaut.voxel.core.menu.template.menu.*;
import io.github.pigaut.voxel.core.menu.button.*;
import io.github.pigaut.voxel.core.menu.template.button.*;
import io.github.pigaut.voxel.plugin.*;
import io.github.pigaut.yaml.convert.format.*;

import java.util.*;

public class FunctionsMenu extends FramedSelectionMenu {

    private final EnhancedPlugin plugin;
    private final String group;

    public FunctionsMenu(EnhancedPlugin plugin, String group) {
        super(CaseFormatter.toTitleCase(group) + " Functions", MenuSize.BIG);
        this.plugin = plugin;
        this.group = group;
    }

    @Override
    public List<Button> createEntries() {
        return plugin.getFunctions().getAll(group).stream()
                .map(function -> Button.builder()
                        .type(function.getIcon().getType())
                        .name("&8&o" + CaseFormatter.toTitleCase(function.getName()))
                        .addEmptyLine()
                        .addLine("&eLeft-Click: &fRun function")
                        .onLeftClick((menuView, playerState) -> {
                            Context context = Context.fromPlayerAndState(playerState.asPlayer(), playerState);
                            function.run(context);
                        })
                        .buildButton())
                .toList();
    }

    @Override
    public Button getToolbarButton4() {
        return Buttons.MAIN_MENU;
    }

}
