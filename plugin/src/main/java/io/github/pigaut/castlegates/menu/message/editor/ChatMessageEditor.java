package io.github.pigaut.castlegates.menu.message.editor;

import io.github.pigaut.voxel.bukkit.*;
import io.github.pigaut.voxel.core.menu.*;
import io.github.pigaut.voxel.core.menu.button.*;
import io.github.pigaut.voxel.core.menu.template.menu.*;
import io.github.pigaut.voxel.util.*;
import io.github.pigaut.yaml.*;
import org.bukkit.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class ChatMessageEditor extends GenericMessageEditor {

    public ChatMessageEditor(ConfigSection section) {
        super("Edit Chat Message", section);
        if (!section.contains("chat|message|messages")) {
            section.set("chat|message|messages[0]", "not set");
        }
        else if (section.isSet("chat|message|messages")) {
            String chatLine = section.getString("chat|message|messages").orElse("");
            section.set("chat|message|messages[0]", chatLine);
        }
    }

    @Override
    public @Nullable Button[] createButtons() {
        Button[] buttons = super.createButtons();

        ButtonBuilder messageButton = Button.builder()
                .type(Material.OAK_SIGN)
                .name("&f&lMessage")
                .enchanted(true)
                .addEmptyLine()
                .addLines(section.getStringList("chat|message|messages").stream().map(StringUtil::prefixDash).toList())
                .addEmptyLine()
                .addLeftClickLine("To add a message")
                .addRightClickLine("To remove a message")
                .onLeftClick((view, player) -> {
                    player.collectChatInput()
                            .description("Enter message in chat")
                            .onInput(input -> {
                                section.getSequenceOrCreate("chat|message|messages").add(input);
                                view.update();
                            })
                            .start();
                })
                .onRightClick((view, player) -> {
                    ConfigSequence messageSequence = section.getSequenceOrCreate("chat|message|messages");
                    if (!messageSequence.isEmpty()) {
                        messageSequence.remove(messageSequence.size() - 1);
                        view.update();
                    }
                });

        buttons[20] = messageButton.buildButton();

        return buttons;
    }

    @Override
    public void onClose(MenuView view) {
        ConfigSequence messageSequence = section.getSequenceOrCreate("chat|message|messages");
        if (messageSequence.isEmpty()) {
            section.set("chat|message|messages", "not set");
        }
        super.onClose(view);
    }

}
