package io.github.pigaut.castlegates.config;

import io.github.pigaut.castlegates.*;
import io.github.pigaut.castlegates.gate.*;
import io.github.pigaut.voxel.core.hologram.*;
import io.github.pigaut.voxel.data.function.*;
import io.github.pigaut.voxel.data.structure.*;
import io.github.pigaut.voxel.util.Server;
import io.github.pigaut.yaml.*;
import io.github.pigaut.yaml.configurator.load.*;
import io.github.pigaut.yaml.util.*;
import org.bukkit.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class GatePhaseLoader implements ConfigLoader<GatePhase> {

    private final CastleGatesPlugin plugin;

    public GatePhaseLoader(CastleGatesPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public @Nullable String getErrorDescription() {
        return "invalid gate phase";
    }

    @Override
    public @NotNull GatePhase loadFromSection(@NotNull ConfigSection section) throws InvalidConfigException {
        StructureTemplate structure = section.contains("structure|blocks") ?
                section.getRequired("structure|blocks", StructureTemplate.class) :
                section.getRequired(StructureTemplate.class);

        List<Material> decorativeBlocks = section.getAllRequired("decorative-blocks", Material.class);

        int defaultDelay = section.get("delay|transition-delay", Ticks.class)
                .map(Ticks::getCount)
                .withDefault(0);

        boolean closingOnly = section.getBoolean("closing-only").withDefault(false);
        int openingDelay = section.get("opening-delay|open-delay", Ticks.class)
                .map(Ticks::getCount)
                .require(Requirements.min(0))
                .require(delay -> delay == 0 || !closingOnly, "Cannot set opening delay when closing-only is true")
                .withDefault(closingOnly ? 0 : defaultDelay);

        boolean openingOnly = section.getBoolean("opening-only").withDefault(false);
        int closingDelay = section.get("closing-delay|close-delay", Ticks.class)
                .map(Ticks::getCount)
                .require(Requirements.min(0))
                .require(delay -> delay == 0 || !openingOnly, "Cannot set closing delay when opening-only is true")
                .withDefault(openingOnly ? 0 : defaultDelay);

        Double health = section.getDouble("health")
                .require(Requirements.positive())
                .withDefault(null);

        int clickCooldown = section.getInteger("click-cooldown")
                .require(Requirements.min(1))
                .withDefault(plugin.getSettings().getClickCooldown());

        HologramTemplate openingHologram = null;
        HologramTemplate closingHologram = null;
        if (Server.isPluginEnabled("DecentHolograms")) {
            HologramTemplate defaultHologram = section.get("hologram", HologramTemplate.class)
                    .withDefault(null);

            openingHologram = section.get("opening-hologram|open-hologram", HologramTemplate.class)
                    .withDefault(defaultHologram);

            closingHologram = section.get("closing-hologram|close-hologram", HologramTemplate.class)
                    .withDefault(defaultHologram);
        }

        Function onBreak = section.get("on-break", Function.class).withDefault(null);
        Function onTransition = section.get("on-transition|on-transit", Function.class).withDefault(null);
        Function onOpening = section.get("on-opening|on-open", Function.class).withDefault(null);
        Function onClosing = section.get("on-closing|on-close", Function.class).withDefault(null);
        Function onClick = section.get("on-click", Function.class).withDefault(null);
        Function onLeftClick = section.get("on-hit|on-left-click", Function.class).withDefault(null);
        Function onRightClick = section.get("on-right-click", Function.class).withDefault(null);
        Function onDestroy = section.get("on-destroy", Function.class).withDefault(null);

        return new GatePhase(structure, decorativeBlocks, openingDelay, closingDelay, health, clickCooldown,
                openingHologram, closingHologram, onBreak, onTransition, onOpening, onClosing, onClick,
                onLeftClick, onRightClick, onDestroy);
    }

}
