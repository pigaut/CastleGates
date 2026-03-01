package io.github.pigaut.castlegates;

import io.github.pigaut.castlegates.command.*;
import io.github.pigaut.castlegates.config.*;
import io.github.pigaut.castlegates.gate.*;
import io.github.pigaut.castlegates.gate.template.*;
import io.github.pigaut.castlegates.hook.plotsquared.*;
import io.github.pigaut.castlegates.listener.*;
import io.github.pigaut.castlegates.player.*;
import io.github.pigaut.castlegates.settings.*;
import io.github.pigaut.voxel.command.*;
import io.github.pigaut.voxel.plugin.*;
import io.github.pigaut.voxel.plugin.boot.*;
import io.github.pigaut.voxel.plugin.boot.phase.*;
import io.github.pigaut.voxel.server.Server;
import io.github.pigaut.voxel.version.*;
import io.github.pigaut.yaml.configurator.*;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class CastleGatesPlugin extends EnhancedJavaPlugin {

    private final CastleGatesSettings settings = new CastleGatesSettings(this);
    private final GateTemplateManager templateManager = new GateTemplateManager(this);
    private final GateManager gateManager = new GateManager(this);
    private final GatesPlayerManager playerManager = new GatesPlayerManager(this);
    private final GateOptionsManager gateOptionsManager = new GateOptionsManager(this);
    
    private static CastleGatesPlugin plugin;
    
    public static CastleGatesPlugin getInstance() {
        return plugin;
    }

    @Override
    public void onLoad() {
        plugin = this;
    }

    @Override
    public @NotNull CastleGatesSettings getSettings() {
        return settings;
    }

    @Override
    public boolean isPremium() {
        return true;
    }

    @Override
    public void registerHooks() {
        if (Server.isPluginLoaded("PlotSquared")) {
            registerListener(new PlotBlockBreakListener(this));
        }
    }

    @Override
    public @Nullable String getDatabaseName() {
        return "data";
    }

    @Override
    public @Nullable String getLogo() {
        return """
                
                ┏━╸┏━┓┏━┓╺┳╸╻  ┏━╸┏━╸┏━┓╺┳╸┏━╸┏━┓
                ┃  ┣━┫┗━┓ ┃ ┃  ┣╸ ┃╺┓┣━┫ ┃ ┣╸ ┗━┓
                ┗━╸╹ ╹┗━┛ ╹ ┗━╸┗━╸┗━┛╹ ╹ ╹ ┗━╸┗━┛""";
    }

    @Override
    public @Nullable Integer getMetricsId() {
        return 29824;
    }

    @Override
    public @Nullable Integer getResourceId() {
        return null;
    }

    @Override
    public List<BootPhase> getStartupRequirements() {
        return List.of(
                BootPhase.SERVER_LOADED,
                BootPhase.WORLDS_LOADED,
                BootPhase.ITEMSADDER_DATA_LOADED
        );
    }

    public List<StartupTask> getStartupTasks() {
        List<StartupTask> startupTasks = new ArrayList<>();

        if (Server.isPluginLoaded("PlotSquared")) {
            startupTasks.add(StartupTask.create()
                    .require(BootPhase.pluginEnabled("PlotSquared"))
                    .onReady(() -> registerListener(new PlotBlockDamageListener(this))));
        }

        return startupTasks;
    }

    @Override
    public List<EnhancedCommand> getDefaultCommands() {
        return List.of(new CastleGatesCommand(this));
    }

    @Override
    public List<Listener> getDefaultListeners() {
        List<Listener> listeners = new ArrayList<>();
        listeners.add(new PlayerEventListener(this));
        listeners.add(new BlockEventListener(this));
        listeners.add(new CropEventListener(this));
        return listeners;
    }

    @Override
    public @NotNull List<Integer> getCompatibleVersions() {
        return Version.getVersionsNewerThan(Version.V1_16_5);
    }

    @Override
    public @NotNull List<String> getCompatiblePlugins() {
        return List.of(
                "Vault",
                "PlaceholderAPI",
                "Multiverse-Core",
                "DecentHolograms",
                "AuraSkills",
                "mcMMO",
                "ItemsAdder",
                "Nexo",
                "CraftEngine",
                "PlotSquared",
                "MythicMobs",
                "ExecutableItems",
                "EcoItems"
        );
    }

    @Override
    public @NotNull List<String> getDefaultDirectories() {
        return List.of("items", "gates", "messages", "languages", "functions", "effects/particles", "effects/sounds");
    }

    @Override
    public List<String> getDefaultResources() {
        return List.of("config.yml", "languages/en.yml");
    }

    @Override
    public List<String> getExampleResources() {
        return List.of(
                "items/misc.yml",
                "messages/misc.yml",

                "effects/particles/misc.yml",
                "effects/particles/flame.yml",
                "effects/particles/special.yml",
                "effects/particles/dust/clouds.yml",
                "effects/particles/dust/spores.yml",
                "effects/particles/dust/fall.yml",
                "effects/particles/dust/rise.yml",
                "effects/sounds/misc.yml",

                "functions/misc.yml",

                "gates/simple.yml",
                "gates/simple_auto.yml",
                "gates/grate.yml",
                "gates/locked/simple_locked.yml",
                "gates/boss_hallway.yml",

                "structures/simple/simple_closed.yml",
                "structures/simple/simple_transition.yml",
                "structures/simple/simple_opened.yml",

                "structures/grate/grate_closed.yml",
                "structures/grate/grate_transition_1.yml",
                "structures/grate/grate_transition_2.yml",
                "structures/grate/grate_opened.yml",

                "structures/boss_hallway/hallway_boss_gate_1.yml",
                "structures/boss_hallway/hallway_boss_gate_2.yml",
                "structures/boss_hallway/hallway_boss_gate_3.yml",
                "structures/boss_hallway/hallway_boss_gate_4.yml",
                "structures/boss_hallway/hallway_boss_gate_5.yml",
                "structures/boss_hallway/hallway_boss_gate_6.yml",
                "structures/boss_hallway/hallway_boss_gate_7.yml",
                "structures/boss_hallway/hallway_boss_gate_8.yml",
                "structures/boss_hallway/hallway_boss_gate_9.yml",
                "structures/boss_hallway/hallway_boss_gate_10.yml",
                "structures/boss_hallway/hallway_boss_gate_11.yml",
                "structures/boss_hallway/hallway_boss_gate_12.yml"
        );
    }

    @Override
    public Map<Integer, List<String>> getExamplesByVersion() {
        return Map.of();
    }

    @Override
    public Map<String, List<String>> getExamplesByPlugin() {
        return Map.of();
    }

    @Override
    public @NotNull Configurator createConfigurator() {
        return new CastleGatesConfigurator(this);
    }

    @Override
    public @NotNull GatesPlayerManager getPlayersState() {
        return playerManager;
    }

    @Override
    public @NotNull GatesPlayer getPlayerState(@NotNull Player player) {
        return playerManager.getPlayerState(player);
    }

    @Override
    public @Nullable GatesPlayer getPlayerState(@NotNull UUID playerId) {
        return playerManager.getPlayerState(playerId);
    }

    public @NotNull GateTemplateManager getGateTemplates() {
        return templateManager;
    }

    public @Nullable GateTemplate getGateTemplate(String name) {
        return templateManager.get(name);
    }

    public @NotNull List<GateTemplate> getGateTemplates(String group) {
        return templateManager.getAll(group);
    }

    public @NotNull GateManager getGates() {
        return gateManager;
    }

    public @Nullable Gate getGate(@NotNull Location location) {
        return gateManager.getGate(location);
    }

    public GateOptionsManager getGateOptions() {
        return gateOptionsManager;
    }

}
