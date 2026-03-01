package io.github.pigaut.castlegates.menu.message.editor;

import io.github.pigaut.castlegates.menu.message.*;
import io.github.pigaut.voxel.bukkit.*;
import io.github.pigaut.voxel.menu.*;
import io.github.pigaut.voxel.menu.button.*;
import io.github.pigaut.voxel.menu.template.button.*;
import io.github.pigaut.voxel.menu.template.menu.editor.*;
import io.github.pigaut.voxel.util.*;
import io.github.pigaut.yaml.*;
import io.github.pigaut.yaml.convert.format.*;
import org.bukkit.*;

import java.util.*;

public class MultiMessageEditor extends FramedSelectionEditor {

    private final ConfigSequence messageSequence;

    public MultiMessageEditor(ConfigSequence messageSequence) {
        super(messageSequence.getRoot(), "Edit Multi-Message", MenuSize.BIG);
        this.messageSequence = messageSequence;
    }

    @Override
    public Button getFrameButton() {
        return Buttons.LIGHT_BLUE_PANEL;
    }

    @Override
    public List<Button> createEntries() {
        final List<Button> buttons = new ArrayList<>();

        for (int i = 0; i < messageSequence.size(); i++) {
            ConfigSection messageSection = messageSequence.getSectionOrCreate(i);

            final int messageIndex = i;
            ButtonBuilder messageButton = Button.builder()
                    .amount(messageIndex + 1)
                    .enchanted(true)
                    .addEmptyLine()
                    .addLine("&eLeft-Click: &fTo edit message")
                    .addLine("&cRight-Click: &fTo remove message")
                    .onRightClick((view, player) -> {
                        messageSequence.remove(messageIndex);
                        view.update();
                    });

            if (messageSection.contains("chat|message|messages")) {
                messageButton.type(Material.BOOK)
                        .name(messageSection.getString("chat|message|messages", StringColor.FORMATTER).orElse("not set"))
                        .onLeftClick((view, player) -> player.openMenu(new ChatMessageEditor(messageSection)));
            }
            else if (messageSection.contains("actionbar|action-bar")) {
                messageButton.type(Material.NAME_TAG)
                        .name(messageSection.getString("message", StringColor.FORMATTER).orElse("not set"))
                        .onLeftClick((view, player) -> player.openMenu(new ActionbarEditor(messageSection)));
            }
            else if (messageSection.contains("title")) {
                messageButton.type(Material.MAP)
                        .name(messageSection.getString("title", StringColor.FORMATTER).orElse("not set"))
                        .onLeftClick((view, player) -> player.openMenu(new TitleEditor(messageSection)));
            }
            else if (messageSection.contains("bossbar|boss-bar")) {
                messageButton.type(Material.DRAGON_HEAD)
                        .name(messageSection.getString("title", StringColor.FORMATTER).orElse("not set"))
                        .onLeftClick((view, player) -> player.openMenu(new BossbarEditor(messageSection)));
            }
            else if (messageSection.contains("hologram")) {
                messageButton.type(Material.BEACON)
                        .name(messageSection.getSequence("hologram.frames")
                                .map(frameSequence -> frameSequence.toStringList(StringColor.FORMATTER).orElse(List.of()).stream()
                                        .max(Comparator.comparingInt(String::length)))
                                .orElse(messageSection.getString("hologram.text", StringColor.FORMATTER).asOptional())
                                .orElse("not set"))
                        .onLeftClick((view, player) -> player.openMenu(new HologramMessageEditor(messageSection)));
            }
            else {
                messageButton.type(Material.BARRIER).name("&4Invalid");
            }

            buttons.add(messageButton.buildButton());
        }

        ButtonBuilder createMessageButton = Button.builder()
                .type(Material.LIME_DYE)
                .enchanted(true)
                .name("&2Create New Message")
                .onLeftClick((view, player) -> {
                    MessageCreationMenu messageCreationMenu = new MessageCreationMenu(messageSequence.addEmptySection(), false);
                    player.openMenu(messageCreationMenu);
                });

        buttons.add(createMessageButton.buildButton());

        return buttons;
    }

}
