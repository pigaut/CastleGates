package io.github.pigaut.castlegates.menu.message;

import io.github.pigaut.castlegates.menu.message.editor.*;
import io.github.pigaut.voxel.core.menu.*;
import io.github.pigaut.voxel.core.menu.template.menu.*;
import io.github.pigaut.voxel.core.menu.button.*;
import io.github.pigaut.voxel.core.menu.template.button.*;
import io.github.pigaut.yaml.*;
import org.bukkit.*;
import org.jetbrains.annotations.*;

public class MessageCreationMenu extends FramedMenu {

    private final ConfigSection messageSection;
    private final boolean multiMessageButton;

    public MessageCreationMenu(ConfigSection messageSection, boolean multiMessageButton) {
        super("Message Creation", MenuSize.SMALL);
        this.messageSection = messageSection;
        this.multiMessageButton = multiMessageButton;
    }

    @Override
    public Button getFrameButton() {
        return Buttons.LIGHT_BLUE_PANEL;
    }

    @Override
    public boolean backOnClose() {
        return true;
    }

    @Override
    public @Nullable Button[] createButtons() {
        final Button[] buttons = super.createButtons();

        buttons[10] = Button.builder()
                .type(Material.BOOK)
                .enchanted(true)
                .name("Chat")
                .addEmptyLine()
                .addLeftClickLine("To create a new chat message")
                .onLeftClick((view, player) -> {
                    final Menu chatMessageEditor = new ChatMessageEditor(messageSection);
                    player.openMenu(chatMessageEditor, view.getPreviousView());
                })
                .buildButton();

        buttons[11] = Button.builder()
                .type(Material.MAP)
                .enchanted(true)
                .name("Action Bar")
                .addEmptyLine()
                .addLeftClickLine("To create a new action bar message")
                .onLeftClick((view, player) -> {
                    final Menu actionbarEditor = new ActionbarEditor(messageSection);
                    player.openMenu(actionbarEditor, view.getPreviousView());
                })
                .buildButton();

        buttons[12] = Button.builder()
                .type(Material.NAME_TAG)
                .enchanted(true)
                .name("Title")
                .addEmptyLine()
                .addLeftClickLine("To create a new title message")
                .onLeftClick((view, player) -> {
                    final Menu titleEditor = new TitleEditor(messageSection);
                    player.openMenu(titleEditor, view.getPreviousView());
                })
                .buildButton();

        buttons[14] = Button.builder()
                .type(Material.DRAGON_HEAD)
                .enchanted(true)
                .name("Boss Bar")
                .addEmptyLine()
                .addLeftClickLine("To create a new boss bar message")
                .onLeftClick((view, player) -> {
                    final Menu bossbarEditor = new BossbarEditor(messageSection);
                    player.openMenu(bossbarEditor, view.getPreviousView());
                })
                .buildButton();

        buttons[15] = Button.builder()
                .type(Material.BEACON)
                .enchanted(true)
                .name("Hologram")
                .addLine("&c&o[!] Requires DecentHolograms")
                .addEmptyLine()
                .addLeftClickLine("To create a new hologram message")
                .onLeftClick((view, player) -> {
                    final Menu hologramMessageEditor = new HologramMessageEditor(messageSection);
                    player.openMenu(hologramMessageEditor, view.getPreviousView());
                })
                .buildButton();

        if (multiMessageButton) {
            buttons[16] = Button.builder()
                    .type(Material.BOOKSHELF)
                    .enchanted(true)
                    .name("Multi Message")
                    .addEmptyLine()
                    .addLeftClickLine("To create a new multi message")
                    .onLeftClick((view, player) -> {
                        final Menu multiMessageEditor = new MultiMessageEditor(messageSection.convertToSequence());
                        player.openMenu(multiMessageEditor, view.getPreviousView());
                    })
                    .buildButton();
        }

        return buttons;
    }
}
