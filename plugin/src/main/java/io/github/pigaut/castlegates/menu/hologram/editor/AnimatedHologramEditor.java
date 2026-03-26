package io.github.pigaut.castlegates.menu.hologram.editor;

import io.github.pigaut.voxel.bukkit.*;
import io.github.pigaut.voxel.core.menu.*;
import io.github.pigaut.voxel.core.menu.button.*;
import io.github.pigaut.voxel.core.menu.template.menu.*;
import io.github.pigaut.voxel.util.*;
import io.github.pigaut.yaml.*;
import org.bukkit.*;
import org.jetbrains.annotations.*;

public class AnimatedHologramEditor extends GenericHologramEditor {

    private final ConfigSequence frameSequence;

    public AnimatedHologramEditor(ConfigSection hologramSection) {
        super(hologramSection);
        frameSequence = hologramSection.getSequenceOrCreate("frames");
        ensureMinimumAnimationFrames();
    }

    private void ensureMinimumAnimationFrames() {
        if (frameSequence.size() < 2) {
            frameSequence.clear();
            frameSequence.add("n");
            frameSequence.add("no");
            frameSequence.add("not");
            frameSequence.add("not ");
            frameSequence.add("not s");
            frameSequence.add("not se");
            frameSequence.add("not set");
            frameSequence.add("not set");
            frameSequence.add("not set");
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
                .addLines(frameSequence.toStringList(StringColor.FORMATTER).stream().map(StringUtil::prefixDash).toList())
                .addEmptyLine()
                .addLeftClickLine("To add element to list")
                .addLeftClickLine("To remove element from list")
                .onLeftClick((view, player) -> {
                    player.collectChatInput()
                            .description("Enter hologram frame text in chat")
                            .onInput(input -> {
                                frameSequence.add(input);
                                view.open();
                            })
                            .start();
                })
                .onRightClick((view, player) -> {
                    if (!frameSequence.isEmpty()) {
                        frameSequence.remove(frameSequence.size() - 1);
                        view.update();
                    }
                });

        buttons[20] = framesButton.buildButton();
        return buttons;
    }

    @Override
    public void onClose(MenuView view) {
        ensureMinimumAnimationFrames();
        super.onClose(view);
    }

}