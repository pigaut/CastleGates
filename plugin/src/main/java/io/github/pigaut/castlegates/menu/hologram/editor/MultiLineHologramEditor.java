package io.github.pigaut.castlegates.menu.hologram.editor;

import io.github.pigaut.voxel.menu.*;
import io.github.pigaut.voxel.menu.button.*;
import io.github.pigaut.voxel.util.*;
import io.github.pigaut.yaml.*;
import org.bukkit.*;
import org.jetbrains.annotations.*;

public class MultiLineHologramEditor extends GenericHologramEditor {

    private final ConfigSequence lineSequence;

    public MultiLineHologramEditor(ConfigSection hologramSection) {
        super(hologramSection);
        lineSequence = hologramSection.getSequenceOrCreate("lines");
        ensureMinimumLines();
    }

    private void ensureMinimumLines() {
        if (lineSequence.size() < 2) {
            lineSequence.clear();
            lineSequence.add("not set");
            lineSequence.add("not set");
        }
    }

    @Override
    public @Nullable Button[] createButtons() {
        Button[] buttons = super.createButtons();

        ButtonBuilder framesButton = Button.builder()
                .type(Material.OAK_SIGN)
                .enchanted(true)
                .name("&f&lFrames")
                .addEmptyLine()
                .addLines(lineSequence.toStringList().stream().map(StringUtil::prefixDash).toList())
                .addEmptyLine()
                .addLeftClickLine("To add element to list")
                .addLeftClickLine("To remove element from list")
                .onLeftClick((view, player) -> player.collectChatInput()
                        .description("Enter hologram frame text in chat")
                        .onInput(lineSequence::add)
                        .start())
                .onRightClick((view, player) -> {
                    if (!lineSequence.isEmpty()) {
                        lineSequence.remove(lineSequence.size() - 1);
                        view.update();
                    }
                });

        buttons[20] = framesButton.buildButton();
        return buttons;
    }

    @Override
    public void onClose(MenuView view) {
        ensureMinimumLines();
        super.onClose(view);
    }

}