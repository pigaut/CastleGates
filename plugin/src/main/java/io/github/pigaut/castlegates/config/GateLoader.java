package io.github.pigaut.castlegates.config;

import io.github.pigaut.castlegates.*;
import io.github.pigaut.castlegates.gate.*;
import io.github.pigaut.castlegates.gate.template.*;
import io.github.pigaut.voxel.core.function.*;
import io.github.pigaut.voxel.core.hologram.*;
import io.github.pigaut.voxel.core.structure.*;
import io.github.pigaut.voxel.plugin.manager.*;
import io.github.pigaut.voxel.server.Server;
import io.github.pigaut.yaml.*;
import io.github.pigaut.yaml.amount.*;
import io.github.pigaut.yaml.configurator.load.*;
import io.github.pigaut.yaml.util.*;
import org.bukkit.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class GateLoader implements ConfigLoader<GateTemplate> {

    private final CastleGatesPlugin plugin;

    public GateLoader(CastleGatesPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getErrorDescription() {
        return "invalid gate";
    }

    @Override
    public @NotNull GateTemplate loadFromScalar(ConfigScalar scalar) throws InvalidConfigException {
        String gateName = scalar.toString();
        GateTemplate gateTemplate = plugin.getGateTemplate(gateName);
        if (gateTemplate == null) {
            throw new InvalidConfigException(scalar, "Could not find gate with name: '" + gateName + "'");
        }
        return gateTemplate;
    }

    @Override
    public @NotNull GateTemplate loadFromSequence(@NotNull ConfigSequence sequence) throws InvalidConfigException {
        if (!(sequence instanceof ConfigRoot root)) {
            throw new InvalidConfigException(sequence, "Gate can only be loaded from a root configuration sequence");
        }

        String name = root.getName();
        String group = Group.byFile(root.getFile(), "gates", true);

        List<GateStage> gateStages = new ArrayList<>();
        GateTemplate gate = new GateTemplate(name, group, gateStages);
        for (ConfigSection nestedSection : sequence.getNestedSections()) {
            gateStages.add(loadStage(gate, nestedSection));
        }

        if (gateStages.size() < 2) {
            throw new InvalidConfigException(sequence, "Gate must have at least one closed and one open stage");
        }

        GateStage firstStage = gateStages.get(0);
        if (firstStage.getTransitionFunction() != null) {
            throw new InvalidConfigException(sequence, "The first stage cannot have a growth function");
        }

        if (firstStage.getOpeningDelay() == 0) {
            throw new InvalidConfigException(sequence, "The first stage must have an opening delay set");
        }

        Material mostCommonMaterial = gate.getLastStage().getStructure().getMostCommonMaterial();
        gate.setItemType(mostCommonMaterial);

        return gate;
    }

    private GateStage loadStage(GateTemplate gate, ConfigSection section) throws InvalidConfigException {
        BlockStructure structure = section.contains("structure|blocks") ?
                section.getRequired("structure|blocks", BlockStructure.class) :
                section.getRequired(BlockStructure.class);

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

        int clickCooldown = section.getInteger("click-cooldown")
                .require(Requirements.min(1))
                .withDefault(plugin.getSettings().getClickCooldown());

        Hologram openingHologram = null;
        Hologram closingHologram = null;
        if (Server.isPluginEnabled("DecentHolograms")) {
            Hologram defaultHologram = section.get("hologram", Hologram.class)
                    .withDefault(null);

            openingHologram = section.get("opening-hologram|open-hologram", Hologram.class)
                    .withDefault(defaultHologram);

            closingHologram = section.get("closing-hologram|close-hologram", Hologram.class)
                    .withDefault(defaultHologram);
        }

        Function onBreak = section.get("on-break", Function.class).withDefault(null);
        Function onTransition = section.get("on-transition|on-transit", Function.class).withDefault(null);
        Function onOpening = section.get("on-opening|on-open", Function.class).withDefault(null);
        Function onClosing = section.get("on-closing|on-close", Function.class).withDefault(null);
        Function onClick = section.get("on-click", Function.class).withDefault(null);
        Function onLeftClick = section.get("on-hit|on-left-click", Function.class).withDefault(null);
        Function onRightClick = section.get("on-right-click", Function.class).withDefault(null);

        return new GateStage(gate, structure, decorativeBlocks, openingDelay, closingDelay, clickCooldown,
                openingHologram, closingHologram, onBreak, onTransition, onOpening, onClosing, onClick, onLeftClick, onRightClick);
    }

}