package io.github.pigaut.castlegates.menu.hologram.editor;

import io.github.pigaut.voxel.menu.button.*;
import io.github.pigaut.yaml.*;
import io.github.pigaut.yaml.convert.format.*;
import org.bukkit.*;
import org.jetbrains.annotations.*;

public class BlockHologramEditor extends GenericHologramEditor {

    public BlockHologramEditor(ConfigSection hologramSection) {
        super(hologramSection);
        if (!hologramSection.isSet("block")) {
            section.set("block", Material.GRASS_BLOCK);
        }
    }

    @Override
    public @Nullable Button[] createButtons() {
        Button[] buttons = super.createButtons();

        ButtonBuilder blockButton = Button.builder()
                .type(Material.GRASS_BLOCK)
                .enchanted(true)
                .name("&f&lBlock Type")
                .addEmptyLine()
                .addLine(section.getString("block", CaseStyle.CONSTANT).orElse("not set"))
                .addEmptyLine()
                .addLeftClickLine("To set the hologram block")
                .onLeftClick((view, player) -> {
                    player.collectChatInput(Material.class)
                            .description("Enter hologram block type in chat")
                            .onInput(input -> section.set("block", input))
                            .start();
                });

        buttons[20] = blockButton.buildButton();

        return buttons;
    }

}
