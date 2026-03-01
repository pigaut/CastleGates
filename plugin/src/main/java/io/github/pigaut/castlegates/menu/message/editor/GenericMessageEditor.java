package io.github.pigaut.castlegates.menu.message.editor;

import io.github.pigaut.voxel.menu.*;
import io.github.pigaut.voxel.menu.button.*;
import io.github.pigaut.voxel.menu.template.button.*;
import io.github.pigaut.voxel.menu.template.menu.editor.*;
import io.github.pigaut.yaml.*;
import org.bukkit.*;
import org.jetbrains.annotations.*;

public class GenericMessageEditor extends FramedEditor {

    public GenericMessageEditor(@NotNull String title, @NotNull ConfigSection section) {
        super(section, title, MenuSize.BIG);
    }

    @Override
    public Button getFrameButton() {
        return Buttons.LIGHT_BLUE_PANEL;
    }

    @Override
    public @Nullable Button[] createButtons() {
        final Button[] buttons = super.createButtons();

        final Integer delay = section.getInteger("delay").orElse(null);
        final ButtonBuilder delayButton = Button.builder()
                .type(Material.CLOCK)
                .name("&f&lDelay")
                .enchanted(true)
                .addEmptyLine()
                .addLine(delay != null ? (delay + " ticks") : "not set")
                .addEmptyLine()
                .addLeftClickLine("To set message delay")
                .onLeftClick((view, player) -> {
                    player.collectChatInput(Integer.class)
                            .description("Enter delay amount in chat")
                            .onInput(input -> {
                                section.set("delay", input);
                                view.open();
                            })
                            .start();
                });

        final ButtonBuilder repetitionsButton = Button.builder()
                .type(Material.REPEATER)
                .name("&f&lRepetitions")
                .enchanted(true)
                .addEmptyLine()
                .addLine(section.getString("repeat|repetitions").orElse("not set"))
                .addEmptyLine()
                .addLeftClickLine("To set message repetitions")
                .onLeftClick((view, player) -> {
                    player.collectChatInput(Integer.class)
                            .description("Enter repetitions amount in chat")
                            .onInput(input -> {
                                section.set("repeat|repetitions", input);
                                view.open();
                            })
                            .start();
                });

        final Integer interval = section.getInteger("interval").orElse(null);
        final ButtonBuilder intervalButton = Button.builder()
                .type(Material.COMPARATOR)
                .name("&f&lInterval")
                .enchanted(true)
                .addEmptyLine()
                .addLine(interval != null ? (interval + " ticks") : "not set")
                .addEmptyLine()
                .addLeftClickLine("To set message interval")
                .onLeftClick((view, player) -> {
                    player.collectChatInput(Integer.class)
                            .description("Enter interval amount in chat")
                            .onInput(input -> {
                                if (!section.isSet("repeat|repetitions")) {
                                    section.set("repeat|repetitions", 2);
                                }
                                section.set("interval", input);
                                view.open();
                            })
                            .start();
                });

        buttons[15] = delayButton.buildButton();
        buttons[24] = repetitionsButton.buildButton();
        buttons[33] = intervalButton.buildButton();

        return buttons;
    }

}
