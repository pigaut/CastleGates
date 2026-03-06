package io.github.pigaut.orestack.hook.veinminer;

import io.github.pigaut.orestack.*;
import io.github.pigaut.orestack.generator.*;
import org.bukkit.entity.*;
import org.jetbrains.annotations.*;

public class GeneratorBlockVein {

    private static final OrestackPlugin plugin = OrestackPlugin.getInstance();
    private static final VeinMiningPattern veinMiningPattern = VeinMiningPattern.getInstance();

    private GeneratorBlockVein() {}

    public static void mineBlocks(@NotNull Generator generator, @NotNull Player player, int maxVeinSize, int expToDrop) {
        if (generator.getTemplate().isMultiBlock()) {
            return;
        }

        if (!plugin.getGeneratorOptions().isVeinGenerator(generator)) {
            return;
        }

        for (Generator veinGenerator : veinMiningPattern.allocateGenerators(generator, maxVeinSize)) {
            GeneratorUtil.mineBlock(veinGenerator, player, veinGenerator.getBlock(), expToDrop);
        }
    }

}
