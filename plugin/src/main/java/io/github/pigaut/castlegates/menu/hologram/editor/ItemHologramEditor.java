package io.github.pigaut.castlegates.menu.hologram.editor;

import io.github.pigaut.voxel.bukkit.*;
import io.github.pigaut.voxel.menu.button.*;
import io.github.pigaut.voxel.util.*;
import io.github.pigaut.yaml.*;
import org.bukkit.*;
import org.jetbrains.annotations.*;

public class ItemHologramEditor extends GenericHologramEditor {

    public ItemHologramEditor(ConfigSection hologramSection) {
        super(hologramSection);
        if (!hologramSection.isSet("item")) {
            section.set("item", Material.IRON_PICKAXE);
        }
    }

    @Override
    public @Nullable Button[] createButtons() {
        Button[] buttons = super.createButtons();

        ButtonBuilder blockButton = Button.builder()
                .type(Material.GRASS_BLOCK)
                .enchanted(true)
                .name("&f&lItem Type")
                .addEmptyLine()
                .addLine(section.getString("item", StringColor.FORMATTER).orElse("not set"))
                .addEmptyLine()
                .addLeftClickLine("To set the hologram item")
                .onLeftClick((view, player) -> {
                    player.collectChatInput(Material.class)
                            .description("Enter item type in chat")
                            .onInput(input -> {
                                section.set("item", input);
                                view.open();
                            })
                            .start();
                });

        buttons[20] = blockButton.buildButton();

        return buttons;
    }

}
