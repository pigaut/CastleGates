package io.github.pigaut.castlegates.menu.particle.editor;

import io.github.pigaut.voxel.menu.*;
import io.github.pigaut.voxel.menu.button.*;
import io.github.pigaut.voxel.menu.template.menu.editor.*;
import io.github.pigaut.yaml.*;
import io.github.pigaut.yaml.convert.parse.*;
import org.bukkit.*;
import org.bukkit.attribute.*;
import org.jetbrains.annotations.*;

public class GenericParticleEditor extends FramedEditor {

    public GenericParticleEditor(String title, ConfigSection particleSection) {
        super(particleSection, title, MenuSize.LARGE);
    }

    @Override
    public @Nullable Button[] createButtons() {
        final Button[] buttons = super.createButtons();

        final Integer delay = section.getInteger("delay").orElse(null);
        final ButtonBuilder delayButton = Button.builder()
                .type(Material.CLOCK)
                .name("&f&lDelay")
                .enchanted(true)
                .onLeftClick((view, player) -> {
                    player.collectChatInput()
                            .description("Enter delay amount in chat")
                            .onInput(input -> {
                                final Integer amount = ParseUtil.parseIntegerOrNull(input);
                                if (amount != null) {
                                    section.set("delay", amount);
                                }
                                view.open();
                            })
                            .start();
                })
                .addEmptyLine()
                .addLine(delay != null ? (delay + " ticks") : "not set")
                .addEmptyLine()
                .addLine("&eLeft-Click: &fSet message delay");

        final ButtonBuilder repetitionsButton = Button.builder()
                .type(Material.REPEATER)
                .enchanted(true)
                .name("&f&lRepetitions")
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
                .type(Material.REDSTONE)
                .enchanted(true)
                .name("&f&lInterval")
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

        final ButtonBuilder offsetButton = Button.builder()
                .type(Material.FEATHER)
                .enchanted(true)
                .name("&f&lOffset")
                .addEmptyLine()
                .addLine("&cx: &f" + section.getString("offset.x").orElse("not set"))
                .addLine("&ay: &f" + section.getString("offset.y").orElse("not set"))
                .addLine("&9z: &f" + section.getString("offset.z").orElse("not set"))
                .addEmptyLine()
                .addLeftClickLine("To set &cx &foffset")
                .addRightClickLine("To set &ay &foffset")
                .addLeftClickLine("To set &9z &foffset")
                .onLeftClick((view, player) -> {
                    player.collectChatInput(Double.class)
                            .description("Enter x offset in chat")
                            .onInput(input -> {
                                section.set("offset.x", input);
                                view.open();
                            })
                            .start();
                })
                .onRightClick((view, player) -> {
                    player.collectChatInput(Double.class)
                            .description("Enter y offset in chat")
                            .onInput(input -> {
                                section.set("offset.y", input);
                                view.open();
                            })
                            .start();
                })
                .onShiftLeftClick((view, player) -> {
                    player.collectChatInput(Double.class)
                            .description("Enter z offset in chat")
                            .onInput(input -> {
                                section.set("offset.z", input);
                                view.open();
                            })
                            .start();
                });

        final ButtonBuilder rangeButton = Button.builder()
                .type(Material.FEATHER)
                .enchanted(true)
                .name("&f&lRange")
                .addEmptyLine()
                .addLine("&cx: &f" + section.getString("range.x").orElse("not set"))
                .addLine("&ay: &f" + section.getString("range.y").orElse("not set"))
                .addLine("&9z: &f" + section.getString("range.z").orElse("not set"))
                .addEmptyLine()
                .addLeftClickLine("To set &cx &foffset")
                .addRightClickLine("To set &ay &foffset")
                .addLeftClickLine("To set &9z &foffset")
                .onLeftClick((view, player) -> {
                    player.collectChatInput(Double.class)
                            .description("Enter x offset in chat")
                            .onInput(input -> {
                                section.set("offset.x", input);
                                view.open();
                            })
                            .start();
                })
                .onRightClick((view, player) -> {
                    player.collectChatInput(Double.class)
                            .description("Enter y offset in chat")
                            .onInput(input -> {
                                section.set("offset.y", input);
                                view.open();
                            })
                            .start();
                })
                .onShiftLeftClick((view, player) -> {
                    player.collectChatInput(Double.class)
                            .description("Enter z offset in chat")
                            .onInput(input -> {
                                section.set("offset.z", input);
                                view.open();
                            })
                            .start();
                });

        buttons[15] = delayButton.buildButton();
        buttons[23] = rangeButton.buildButton();
        buttons[24] = repetitionsButton.buildButton();
        buttons[25] = null;
        buttons[33] = intervalButton.buildButton();

        return buttons;
    }

}
