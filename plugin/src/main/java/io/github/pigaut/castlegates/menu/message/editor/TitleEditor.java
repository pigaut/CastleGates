package io.github.pigaut.castlegates.menu.message.editor;

import io.github.pigaut.voxel.bukkit.*;
import io.github.pigaut.voxel.core.menu.button.*;
import io.github.pigaut.voxel.core.menu.template.menu.*;
import io.github.pigaut.voxel.util.*;
import io.github.pigaut.yaml.*;
import org.bukkit.*;
import org.jetbrains.annotations.*;

public class TitleEditor extends GenericMessageEditor {

    public TitleEditor(ConfigSection messageSection) {
        super("Edit Title", messageSection);
        section.set("type", "title");
    }

    @Override
    public @Nullable Button[] createButtons() {
        final Button[] buttons = super.createButtons();
        final ButtonBuilder titleButton = Button.builder()
                .type(Material.OAK_SIGN)
                .name("&f&lTitle")
                .enchanted(true)
                .addEmptyLine()
                .addLine(section.getString("title", StringColor.FORMATTER).orElse("not set"))
                .addEmptyLine()
                .addLeftClickLine("To set the title")
                .onLeftClick((view, player) -> {
                    player.collectChatInput()
                            .description("Enter title in chat")
                            .onInput(input -> {
                                section.set("title", input);
                                view.open();
                            })
                            .start();
                });

        final ButtonBuilder subtitleButton = Button.builder()
                .type(Material.SPRUCE_SIGN)
                .name("&f&lSubtitle")
                .enchanted(true)
                .addEmptyLine()
                .addLine(section.getString("subtitle", StringColor.FORMATTER).orElse("not set"))
                .addEmptyLine()
                .addLeftClickLine("To set the subtitle")
                .onLeftClick((view, player) -> {
                    player.collectChatInput()
                            .description("Enter subtitle in chat")
                            .onInput(input -> {
                                section.set("subtitle", input);
                                view.open();
                            })
                            .start();
                });

        final Integer fadeIn = section.getInteger("fade-in").orElse(null);
        final ButtonBuilder fadeInButton = Button.builder()
                .type(Material.ANVIL)
                .name("&f&lFade In")
                .enchanted(true)
                .addEmptyLine()
                .addLine(fadeIn != null ? (fadeIn + " ticks") : "not set")
                .addEmptyLine()
                .addLeftClickLine("To set title fade-in amount")
                .onLeftClick((view, player) -> {
                    player.collectChatInput(Integer.class)
                            .description("Enter fade-in amount in chat")
                            .onInput(input -> {
                                section.set("fade-in", input);
                                view.open();
                            })
                            .start();
                });

        final Integer stay = section.getInteger("stay").orElse(null);
        final ButtonBuilder stayButton = Button.builder()
                .type(Material.WHITE_BED)
                .name("&f&lStay")
                .enchanted(true)
                .addEmptyLine()
                .addLine(stay != null ? (stay + " ticks") : "not set")
                .addEmptyLine()
                .addLeftClickLine("To set title stay amount")
                .onLeftClick((view, player) -> {
                    player.collectChatInput(Integer.class)
                            .description("Enter stay amount in chat")
                            .onInput(input -> {
                                section.set("stay", input);
                                view.open();
                            })
                            .start();
                });

        final Integer fadeOut = section.getInteger("fade-out").orElse(null);
        final ButtonBuilder fadeOutButton = Button.builder()
                .type(Material.DAMAGED_ANVIL)
                .name("&f&lFade out")
                .enchanted(true)
                .addEmptyLine()
                .addLine(fadeOut != null ? (fadeOut + " ticks") : "not set")
                .addEmptyLine()
                .addLeftClickLine("To set title fade out")
                .onLeftClick((view, player) -> {
                    player.collectChatInput(Integer.class)
                            .description("Enter fade-out amount in chat")
                            .onInput(input -> {
                                section.set("fade-out", input);
                                view.open();
                            })
                            .start();
                });

        buttons[11] = titleButton.buildButton();
        buttons[20] = subtitleButton.buildButton();
        buttons[19] = fadeInButton.buildButton();
        buttons[29] = stayButton.buildButton();
        buttons[21] = fadeOutButton.buildButton();

        return buttons;
    }


}
