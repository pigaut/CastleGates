package io.github.pigaut.castlegates.gate.template;

import io.github.pigaut.castlegates.*;
import io.github.pigaut.castlegates.gate.*;
import io.github.pigaut.voxel.plugin.manager.*;
import io.github.pigaut.yaml.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class GateOptionsManager extends Manager implements ConfigBacked {

    private final CastleGatesPlugin plugin;

    private List<GateTemplate> playerConstructions;

    public GateOptionsManager(CastleGatesPlugin plugin) {
        super(plugin);
        this.plugin = plugin;
    }

    @Override
    public @NotNull List<ConfigException> loadConfigurationData() {
        List<ConfigException> errorsFound = new ArrayList<>();

        ConfigSection config = plugin.getConfiguration();

        playerConstructions = config.getList("player-constructions", GateTemplate.class)
                .withDefaultOrElse(List.of(), errorsFound::add);

        return errorsFound;
    }

    public boolean isPlayerConstruction(@NotNull GateTemplate construction) {
        return playerConstructions.contains(construction);
    }

}
