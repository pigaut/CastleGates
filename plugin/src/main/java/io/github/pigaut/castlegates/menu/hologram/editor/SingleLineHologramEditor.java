package io.github.pigaut.castlegates.menu.hologram.editor;

import io.github.pigaut.voxel.bukkit.*;
import io.github.pigaut.voxel.core.menu.*;
import io.github.pigaut.voxel.core.menu.button.*;
import io.github.pigaut.voxel.core.menu.template.menu.*;
import io.github.pigaut.yaml.*;
import org.bukkit.*;
import org.jetbrains.annotations.*;

public class SingleLineHologramEditor extends GenericHologramEditor {

    public SingleLineHologramEditor(ConfigSection section) {
        super(section);
        if (!section.isSet("line")) {
            section.set("line", "not set");
        }
    }

    @Override
    public @Nullable Button[] createButtons() {
        Button[] buttons = super.createButtons();

        ButtonBuilder lineButton = Button.builder()
                .type(Material.OAK_SIGN)
                .enchanted(true)
                .name("&f&lLine")
                .addEmptyLine()
                .addLine(section.getString("line", StringColor.FORMATTER).orElse("not set"))
                .addEmptyLine()
                .addLeftClickLine("To set hologram line text")
                .onLeftClick((view, player) -> player.collectChatInput()
                        .description("Enter hologram line text in chat")
                        .onInput(input -> section.set("line", input))
                        .start());

        buttons[20] = lineButton.buildButton();

        return buttons;
    }

    @Override
    public void onClose(MenuView view) {
        super.onClose(view);
    }
}
