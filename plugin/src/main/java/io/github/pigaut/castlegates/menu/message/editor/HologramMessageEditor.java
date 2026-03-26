package io.github.pigaut.castlegates.menu.message.editor;

import io.github.pigaut.castlegates.menu.hologram.*;
import io.github.pigaut.castlegates.menu.hologram.editor.*;
import io.github.pigaut.voxel.bukkit.*;
import io.github.pigaut.voxel.core.menu.*;
import io.github.pigaut.voxel.core.menu.button.*;
import io.github.pigaut.voxel.core.menu.template.menu.*;
import io.github.pigaut.yaml.*;
import io.github.pigaut.yaml.convert.format.*;
import org.bukkit.*;
import org.jetbrains.annotations.*;

public class HologramMessageEditor extends GenericMessageEditor {

    public HologramMessageEditor(ConfigSection section) {
        super("Edit Hologram Message", section);
        section.set("type", "hologram");
        if (!section.isSection("hologram") && !section.isSequence("hologram")) {
            section.set("hologram.line", "not set");
        }
    }

    @Override
    public @Nullable Button[] createButtons() {
        Button[] buttons = super.createButtons();
        ButtonBuilder hologramButton = Button.builder()
                .enchanted(true)
                .type(Material.BEACON)
                .addEmptyLine()
                .addLeftClickLine("To edit hologram")
                .addRightClickLine("To change hologram type")
                .onRightClick((view, player) -> {
                    ConfigSection hologramSection = section.getSectionOrCreate("hologram");
                    player.openMenu(new HologramCreationMenu(hologramSection, true));
                });

        if (section.isSection("hologram")) {
            ConfigSection hologramSection = section.getSectionOrCreate("hologram");
            if (hologramSection.contains("line|text")) {
                hologramButton.name("&f&lSingle-Line Hologram")
                        .onLeftClick((view, player) -> player.openMenu(new SingleLineHologramEditor(hologramSection)));
            }
            else if (hologramSection.contains("lines")) {
                hologramButton.name("&f&lMulti-Line Hologram")
                        .onLeftClick((view, player) -> player.openMenu(new MultiLineHologramEditor(hologramSection)));
            }
            else if (hologramSection.contains("frames")) {
                hologramButton.name("&f&lAnimated Hologram")
                        .onLeftClick((view, player) -> player.openMenu(new AnimatedHologramEditor(hologramSection)));
            }
            else if (hologramSection.contains("item")) {
                hologramButton.name("&f&lItem Hologram")
                        .onLeftClick((view, player) -> player.openMenu(new ItemHologramEditor(hologramSection)));
            }
            else if (hologramSection.contains("block")) {
                hologramButton.name("&f&lBlock Hologram")
                        .onLeftClick((view, player) -> player.openMenu(new BlockHologramEditor(hologramSection)));
            }
            else {
                hologramButton.type(Material.BARRIER).name("&c&lInvalid");
            }
        }
        else {
            ConfigSequence hologramSequence = section.getSequenceOrCreate("hologram");
            hologramButton.name("&f&lMulti Hologram")
                    .onLeftClick((view, player) -> player.openMenu(new MultiHologramEditor(hologramSequence)));
        }

        ButtonBuilder xRangeButton = Button.builder()
                .type(Material.RED_WOOL)
                .enchanted(true)
                .name("&f&lRange X")
                .addEmptyLine()
                .addLine(section.getString("range|radius.x").orElse("not set"))
                .addEmptyLine()
                .addLeftClickLine("To set hologram x-range")
                .onLeftClick((view, player) -> {
                    player.collectChatInput(Double.class)
                            .description("Enter hologram x-range in chat")
                            .onInput(input -> section.set("range|radius.x", input))
                            .start();
                });

        ButtonBuilder yRangeButton = Button.builder()
                .type(Material.LIME_WOOL)
                .enchanted(true)
                .name("&f&lRange Y")
                .addEmptyLine()
                .addLine(section.getString("range|radius.y").orElse("not set"))
                .addEmptyLine()
                .addLeftClickLine("To set hologram y-range")
                .onLeftClick((view, player) -> {
                    player.collectChatInput(Double.class)
                            .description("Enter hologram y-range in chat")
                            .onInput(input -> section.set("range|radius.y", input))
                            .start();
                });

        ButtonBuilder zRangeButton = Button.builder()
                .type(Material.BLUE_WOOL)
                .enchanted(true)
                .name("&f&lRange Z")
                .addEmptyLine()
                .addLine(section.getString("range|radius.z").orElse("not set"))
                .addEmptyLine()
                .addLeftClickLine("To set hologram z-radius")
                .onLeftClick((view, player) -> {
                    player.collectChatInput(Double.class)
                            .description("Enter hologram z-radius in chat")
                            .onInput(input -> section.set("range|radius.z", input))
                            .start();
                });

        String duration = section.getString("duration").orElse(null);
        ButtonBuilder durationButton = Button.builder()
                .type(Material.GLASS_BOTTLE)
                .enchanted(true)
                .name("&f&lDuration")
                .addEmptyLine()
                .addLine(duration != null ? (duration + " ticks") : "not set")
                .addEmptyLine()
                .addLeftClickLine("To set hologram duration")
                .onLeftClick((view, player) -> {
                    player.collectChatInput(Double.class)
                            .description("Enter duration amount in chat")
                            .onInput(input -> section.set("duration", input))
                            .start();
                });

        buttons[11] = hologramButton.buildButton();
        buttons[19] = xRangeButton.buildButton();
        buttons[20] = yRangeButton.buildButton();
        buttons[21] = zRangeButton.buildButton();
        buttons[29] = durationButton.buildButton();

        return buttons;
    }

}
