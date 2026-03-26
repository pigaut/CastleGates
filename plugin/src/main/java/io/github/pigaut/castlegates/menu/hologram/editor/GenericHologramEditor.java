package io.github.pigaut.castlegates.menu.hologram.editor;

import io.github.pigaut.voxel.core.menu.*;
import io.github.pigaut.voxel.core.menu.template.button.*;
import io.github.pigaut.voxel.core.menu.template.menu.*;
import io.github.pigaut.voxel.core.menu.button.*;
import io.github.pigaut.voxel.core.menu.template.menu.editor.*;
import io.github.pigaut.yaml.*;
import org.bukkit.*;
import org.jetbrains.annotations.*;

public class GenericHologramEditor extends FramedEditor {

    public GenericHologramEditor(ConfigSection hologramSection) {
        super(hologramSection, "Edit HologramTemplate", MenuSize.BIG);
    }

    @Override
    public Button getFrameButton() {
        return Buttons.WHITE_PANEL;
    }

    @Override
    public @Nullable Button[] createButtons() {
        Button[] buttons = super.createButtons();

        ButtonBuilder updateButton = Button.builder()
                .type(Material.REDSTONE_LAMP)
                .enchanted(true)
                .name("&f&lUpdate")
                .addEmptyLine()
                .addLine(section.getString("update").orElse("not set"))
                .addEmptyLine()
                .addLeftClickLine("To set hologram update timer")
                .onLeftClick((view, player) -> {
                    player.collectChatInput(Integer.class)
                            .description("Enter hologram update in chat")
                            .onInput(input -> section.set("update", input))
                            .start();
                });

        ButtonBuilder xOffsetButton = Button.builder()
                .type(Material.RED_WOOL)
                .enchanted(true)
                .name("&f&lOffset X")
                .addEmptyLine()
                .addLine(section.getString("offset.x").orElse("not set"))
                .addEmptyLine()
                .addLeftClickLine("To set hologram x-offset")
                .onLeftClick((view, player) -> {
                    player.collectChatInput(Double.class)
                            .description("Enter hologram x-offset in chat")
                            .onInput(input -> section.set("offset.x", input))
                            .start();
                });

        ButtonBuilder yOffsetButton = Button.builder()
                .type(Material.LIME_WOOL)
                .enchanted(true)
                .name("&f&lOffset Y")
                .addEmptyLine()
                .addLine(section.getString("offset.y").orElse("not set"))
                .addEmptyLine()
                .addLeftClickLine("To set hologram y-offset")
                .onLeftClick((view, player) -> {
                    player.collectChatInput(Double.class)
                            .description("Enter hologram y-offset in chat")
                            .onInput(input -> section.set("offset.y", input))
                            .start();
                });

        ButtonBuilder zOffsetButton = Button.builder()
                .type(Material.BLUE_WOOL)
                .enchanted(true)
                .name("&f&lOffset Z")
                .addEmptyLine()
                .addLine(section.getString("offset.z").orElse("not set"))
                .addEmptyLine()
                .addLeftClickLine("To set hologram z-offset")
                .onLeftClick((view, player) -> {
                    player.collectChatInput(Double.class)
                            .description("Enter hologram z-offset in chat")
                            .onInput(input -> section.set("offset.z", input))
                            .start();
                });

        buttons[21] = updateButton.buildButton();
        buttons[15] = xOffsetButton.buildButton();
        buttons[24] = yOffsetButton.buildButton();
        buttons[33] = zOffsetButton.buildButton();

        return buttons;
    }
}
