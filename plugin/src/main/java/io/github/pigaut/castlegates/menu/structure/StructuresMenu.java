package io.github.pigaut.castlegates.menu.structure;

import io.github.pigaut.castlegates.*;
import io.github.pigaut.voxel.core.menu.*;
import io.github.pigaut.voxel.core.menu.template.menu.*;
import io.github.pigaut.voxel.core.menu.button.*;
import io.github.pigaut.voxel.core.menu.template.button.*;
import io.github.pigaut.yaml.convert.format.*;

import java.util.*;

public class StructuresMenu extends FramedSelectionMenu {

    private final CastleGatesPlugin plugin = CastleGatesPlugin.getInstance();
    private final String group;

    public StructuresMenu(String group) {
        super(CaseFormatter.toTitleCase(group) + " Structures", MenuSize.BIG);
        this.group = group;
    }

    @Override
    public List<Button> createEntries() {
        return plugin.getStructures().getAll(group).stream()
                .map(structure -> Button.builder()
                        .type(structure.getIcon().getType())
                        .name("&e&o" + CaseFormatter.toTitleCase(structure.getName()))
                        .buildButton())
                .toList();
    }

    @Override
    public Button getToolbarButton4() {
        return Buttons.MAIN_MENU;
    }

}
