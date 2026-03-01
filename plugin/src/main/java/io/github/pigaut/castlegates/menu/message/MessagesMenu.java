package io.github.pigaut.castlegates.menu.message;

import io.github.pigaut.castlegates.menu.message.editor.*;
import io.github.pigaut.voxel.core.message.*;
import io.github.pigaut.voxel.menu.*;
import io.github.pigaut.voxel.menu.button.*;
import io.github.pigaut.voxel.menu.template.button.*;
import io.github.pigaut.voxel.menu.template.menu.*;
import io.github.pigaut.voxel.player.*;
import io.github.pigaut.voxel.plugin.*;
import io.github.pigaut.voxel.plugin.manager.*;
import io.github.pigaut.yaml.*;
import io.github.pigaut.yaml.convert.format.*;
import io.github.pigaut.yaml.node.section.*;
import org.bukkit.*;
import org.jetbrains.annotations.*;

import java.io.*;
import java.util.*;

public class MessagesMenu extends FramedSelectionMenu {

    private final String group;
    private final RootSection config;

    public MessagesMenu(EnhancedPlugin plugin, String group) {
        super(plugin, CaseFormatter.toTitleCase(group) + " Messages", MenuSize.BIG);
        this.group = group;
        File file = Group.getFile(plugin, "messages", group);
        this.config = YamlConfig.loadSectionOrEmpty(file, plugin.getConfigurator());
    }

    @Override
    public void onOpen(MenuView view) {
        plugin.getMessages().reload(errorsFound -> {
            view.update();
        });
    }

    @Override
    public Button getFrameButton() {
        return Buttons.LIGHT_BLUE_PANEL;
    }

    @Override
    public @Nullable Button[] createButtons() {
        Button[] buttons = super.createButtons();

        buttons[41] = new ConfigLoadButton(config);

        return buttons;
    }

    @Override
    public List<Button> createEntries() {
        final List<Button> entries = new ArrayList<>();

        for (Message message : plugin.getMessages().getAll(group)) {
            String name = message.getName();
            Button messageButton = Button.builder()
                    .type(message.getIcon().getType())
                    .name("&b&o" + CaseFormatter.toTitleCase(name))
                    .addEmptyLine()
                    .addLine("&eLeft-Click: &fTo edit message")
                    .addLine("&6Right-Click: &fTo receive message")
                    .onLeftClick((view, player) -> {
                        PlayerState viewer = view.getViewer();
                        switch (message.getType()) {
                            case CHAT -> viewer.openMenu(new ChatMessageEditor(config.getSectionOrCreate(name)));
                            case ACTIONBAR -> viewer.openMenu(new ActionbarEditor(config.getSectionOrCreate(name)));
                            case TITLE -> viewer.openMenu(new TitleEditor(config.getSectionOrCreate(name)));
                            case BOSSBAR -> viewer.openMenu(new BossbarEditor(config.getSectionOrCreate(name)));
                            case HOLOGRAM -> viewer.openMenu(new HologramMessageEditor(config.getSectionOrCreate(name)));
                            case MULTI -> viewer.openMenu(new MultiMessageEditor(config.getSequenceOrCreate(name)));
                        }
                    })
                    .onRightClick((view, player) -> {
                        view.close();
                        message.send(player);
                        int guiReopenDelay = plugin.getSettings().guiReopenDelay;
                        player.sendMessage(ChatColor.RED + "The menu will reopen in " + (guiReopenDelay / 20) + " seconds...");
                        plugin.getScheduler().runTaskLater(guiReopenDelay, view::open);
                    })
                    .buildButton();
            entries.add(messageButton);
        }

        entries.add(Button.builder()
                .type(Material.LIME_DYE)
                .name("&2Create New Message")
                .enchanted(true)
                .onLeftClick((view, player) -> {
                    player.collectChatInput()
                            .description("Enter message name in chat")
                            .onInput(input -> {
                                player.openMenu(new MessageCreationMenu(config.getSectionOrCreate(input), true), view);
                            })
                            .start();
                })
                .buildButton());

        return entries;
    }

    @Override
    public Button getToolbarButton4() {
        return Buttons.MAIN_MENU;
    }

}
