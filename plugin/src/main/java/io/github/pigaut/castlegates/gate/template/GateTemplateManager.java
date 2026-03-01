package io.github.pigaut.castlegates.gate.template;

import io.github.pigaut.castlegates.*;
import io.github.pigaut.voxel.core.structure.*;
import io.github.pigaut.voxel.plugin.*;
import io.github.pigaut.voxel.plugin.boot.*;
import io.github.pigaut.voxel.plugin.manager.*;
import io.github.pigaut.yaml.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class GateTemplateManager extends ConfigBackedManager.Sequence<GateTemplate> {

    private final CastleGatesPlugin plugin;

    public GateTemplateManager(@NotNull CastleGatesPlugin plugin) {
        super(plugin, "gates");
        this.plugin = plugin;
    }

    @Override
    public String getPrefix() {
        return "Gate";
    }

    @Override
    public void loadFromSequence(ConfigSequence sequence) throws InvalidConfigException {
        GateTemplate template = sequence.getRequired(GateTemplate.class);
        try {
            add(template);
        } catch (DuplicateElementException e) {
            throw new InvalidConfigException(sequence, e.getMessage());
        }
    }

    public List<GateTemplate> getPlayerGates() {
        List<GateTemplate> templates = getAll();
        for (GateTemplate template : templates) {
            if (!plugin.getGateOptions().isPlayerConstruction(template)) {
                templates.remove(template);
            }
        }
        return templates;
    }

}
