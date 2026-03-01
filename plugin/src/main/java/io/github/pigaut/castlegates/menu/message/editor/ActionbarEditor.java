package io.github.pigaut.castlegates.menu.message.editor;

import io.github.pigaut.voxel.bukkit.*;
import io.github.pigaut.voxel.menu.button.*;
import io.github.pigaut.voxel.util.*;
import io.github.pigaut.yaml.*;
import org.bukkit.*;
import org.jetbrains.annotations.*;

public class ActionbarEditor extends GenericMessageEditor {

    public ActionbarEditor(ConfigSection section) {
        super("Edit Action Bar", section);
        if (!section.isSet("actionbar|action-bar")) {
            section.set("actionbar|action-bar", "not set");
        }
    }

    @Override
    public @Nullable Button[] createButtons() {
        Button[] buttons = super.createButtons();

        ButtonBuilder messageButton = Button.builder()
                .type(Material.OAK_SIGN)
                .name("&f&lMessage")
                .enchanted(true)
                .onLeftClick((view, player) -> {
                    player.collectChatInput()
                            .description("Enter message in chat")
                            .onInput(input -> section.set("message", input))
                            .start();
                })
                .addEmptyLine()
                .addLine(section.getString("message", StringColor.FORMATTER).orElse("not set"))
                .addEmptyLine()
                .addLine("&eLeft-Click: &fTo set the message");

        buttons[20] = messageButton.buildButton();

        return buttons;
    }

}
