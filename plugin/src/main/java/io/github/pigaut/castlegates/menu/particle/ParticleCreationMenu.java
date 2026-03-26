package io.github.pigaut.castlegates.menu.particle;

import io.github.pigaut.voxel.core.menu.*;
import io.github.pigaut.voxel.core.menu.button.*;
import io.github.pigaut.voxel.core.menu.template.menu.*;
import io.github.pigaut.voxel.core.menu.template.button.*;
import io.github.pigaut.yaml.*;
import org.bukkit.*;

public class ParticleCreationMenu extends FramedMenu {

    private final ConfigSection particleSection;
    private final boolean multiParticleButton;

    public ParticleCreationMenu(ConfigSection particleSection, boolean multiParticleButton) {
        super("Particle Creation", MenuSize.MEDIUM);
        this.particleSection = particleSection;
        this.multiParticleButton = multiParticleButton;
    }

    @Override
    public boolean backOnClose() {
        return true;
    }

    @Override
    public Button[] createButtons() {
        final Button[] buttons = super.createButtons();

        buttons[10] = Button.builder()
                .type(Material.CAMPFIRE)
                .name("Basic")
                .addEmptyLine()
                .addLeftClickLine("Create a new basic particle")
                .enchanted(true)
                .onLeftClick((view, player) -> {
//                    final Menu chatMessageEditor = new ChatMessageEditor(messageSection);
//                    player.openMenu(chatMessageEditor, view.getPreviousView());
                })
                .buildButton();

        buttons[11] = Button.builder()
                .type(Material.REDSTONE)
                .name("Dust (colored)")
                .addEmptyLine()
                .addLeftClickLine("Create a new dust particle")
                .enchanted(true)
                .onLeftClick((view, player) -> {
//                    final Menu chatMessageEditor = new ChatMessageEditor(messageSection);
//                    player.openMenu(chatMessageEditor, view.getPreviousView());
                })
                .buildButton();

        buttons[12] = Button.builder()
                .type(Material.ARROW)
                .name("Directional (moving)")
                .addEmptyLine()
                .addLeftClickLine("Create a new directional particle")
                .enchanted(true)
                .onLeftClick((view, player) -> {
//                    final Menu chatMessageEditor = new ChatMessageEditor(messageSection);
//                    player.openMenu(chatMessageEditor, view.getPreviousView());
                })
                .buildButton();

        buttons[13] = Button.builder()
                .type(Material.POTION)
                .name("Spell (colored)")
                .addEmptyLine()
                .addLeftClickLine("Create a new spell particle")
                .enchanted(true)
                .onLeftClick((view, player) -> {
//                    final Menu chatMessageEditor = new ChatMessageEditor(messageSection);
//                    player.openMenu(chatMessageEditor, view.getPreviousView());
                })
                .buildButton();

        buttons[14] = Button.builder()
                .type(Material.NOTE_BLOCK)
                .name("Note (colored)")
                .addEmptyLine()
                .addLeftClickLine("Create a new note particle")
                .enchanted(true)
                .onLeftClick((view, player) -> {
//                    final Menu chatMessageEditor = new ChatMessageEditor(messageSection);
//                    player.openMenu(chatMessageEditor, view.getPreviousView());
                })
                .buildButton();

        buttons[15] = Button.builder()
                .type(Material.STONE)
                .name("Material")
                .addEmptyLine()
                .addLeftClickLine("Create a new material particle")
                .enchanted(true)
                .onLeftClick((view, player) -> {
//                    final Menu chatMessageEditor = new ChatMessageEditor(messageSection);
//                    player.openMenu(chatMessageEditor, view.getPreviousView());
                })
                .buildButton();

        buttons[16] = Button.builder()
                .type(Material.GLOWSTONE_DUST)
                .name("Dust Transition (colored)")
                .addEmptyLine()
                .addLeftClickLine("Create a new dust transition particle")
                .enchanted(true)
                .onLeftClick((view, player) -> {
//                    final Menu chatMessageEditor = new ChatMessageEditor(messageSection);
//                    player.openMenu(chatMessageEditor, view.getPreviousView());
                })
                .buildButton();

        return buttons;
    }

}
