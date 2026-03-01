package io.github.pigaut.castlegates.menu.hologram;

import io.github.pigaut.castlegates.menu.hologram.editor.*;
import io.github.pigaut.voxel.menu.*;
import io.github.pigaut.voxel.menu.button.*;
import io.github.pigaut.voxel.menu.template.button.*;
import io.github.pigaut.voxel.menu.template.menu.*;
import io.github.pigaut.yaml.*;
import org.bukkit.*;
import org.jetbrains.annotations.*;

public class HologramCreationMenu extends FramedMenu {

    private final ConfigSection hologramSection;
    private final boolean multiHologramButton;

    public HologramCreationMenu(ConfigSection hologramSection, boolean multiHologramButton) {
        super("Hologram Creation", MenuSize.SMALL);
        this.hologramSection = hologramSection;
        this.multiHologramButton = multiHologramButton;
    }

    @Override
    public boolean backOnClose() {
        return true;
    }

    @Override
    public Button getFrameButton() {
        return Buttons.WHITE_PANEL;
    }

    @Override
    public @Nullable Button[] createButtons() {
        Button[] buttons = super.createButtons();

        buttons[10] = Button.builder()
                .type(Material.NAME_TAG)
                .enchanted(true)
                .name("Single-Line Hologram")
                .addEmptyLine()
                .addLeftClickLine("Create a new single-line hologram")
                .onLeftClick((view, player) -> {
                    hologramSection.clear();
                    player.openMenu(new SingleLineHologramEditor(hologramSection), view.getPreviousView());
                })
                .buildButton();

        buttons[11] = Button.builder()
                .type(Material.OAK_SIGN)
                .enchanted(true)
                .name("Multi-Line Hologram")
                .addEmptyLine()
                .addLeftClickLine("Create a new multi-line hologram")
                .onLeftClick((view, player) -> {
                    hologramSection.clear();
                    player.openMenu(new MultiLineHologramEditor(hologramSection), view.getPreviousView());
                })
                .buildButton();

        buttons[12] = Button.builder()
                .type(Material.PAINTING)
                .enchanted(true)
                .name("Animated Hologram")
                .addEmptyLine()
                .addLeftClickLine("Create a new animated hologram")
                .onLeftClick((view, player) -> {
                    hologramSection.clear();
                    player.openMenu(new AnimatedHologramEditor(hologramSection), view.getPreviousView());
                })
                .buildButton();

        buttons[14] = Button.builder()
                .type(Material.IRON_PICKAXE)
                .enchanted(true)
                .name("Item Hologram")
                .addEmptyLine()
                .addLeftClickLine("Create a new item hologram")
                .onLeftClick((view, player) -> {
                    hologramSection.clear();
                    player.openMenu(new ItemHologramEditor(hologramSection), view.getPreviousView());
                })
                .buildButton();

        buttons[15] = Button.builder()
                .type(Material.GRASS_BLOCK)
                .enchanted(true)
                .name("Block Hologram")
                .addEmptyLine()
                .addLeftClickLine("Create a new block hologram")
                .onLeftClick((view, player) -> {
                    hologramSection.clear();
                    player.openMenu(new BlockHologramEditor(hologramSection), view.getPreviousView());
                })
                .buildButton();

        if (multiHologramButton) {
            buttons[16] = Button.builder()
                    .type(Material.BOOKSHELF)
                    .enchanted(true)
                    .name("Multi Hologram")
                    .addEmptyLine()
                    .addLeftClickLine("Create a new multi hologram")
                    .onLeftClick((view, player) -> {
                        ConfigSequence hologramSequence = hologramSection.convertToSequence();
                        hologramSequence.clear();
                        player.openMenu(new MultiHologramEditor(hologramSequence), view.getPreviousView());
                    })
                    .buildButton();
        }

        return buttons;
    }

}
