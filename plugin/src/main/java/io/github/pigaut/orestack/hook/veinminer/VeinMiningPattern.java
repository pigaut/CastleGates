package io.github.pigaut.orestack.hook.veinminer;

import io.github.pigaut.orestack.*;
import io.github.pigaut.orestack.generator.*;
import io.github.pigaut.orestack.generator.template.*;
import org.jetbrains.annotations.*;

import java.util.*;

/**
 * The default {@link VeinMiningPattern} that mines as many blocks in an arbitrary pattern
 * as possible.
 * <p>Originally developed by {@code Choco} as part of the VeinMiner project.
 * Modified by Pigaut for the Orestack plugin.</p>
 */
public class VeinMiningPattern {

    private static final VeinMiningPattern INSTANCE = new VeinMiningPattern();

    private final OrestackPlugin plugin = OrestackPlugin.getInstance();
    private final List<Generator> buffer = new ArrayList<>(32), recent = new ArrayList<>(32);

    private VeinMiningPattern() {}

    @NotNull
    public List<Generator> allocateGenerators(@NotNull Generator generator, int maxVeinSize) {
        List<Generator> blocks = new ArrayList<>();

        recent.add(generator); // For first iteration
        blocks.add(generator);

        GeneratorTemplate template = generator.getTemplate();
        int currentStage = generator.getState().getCurrentStage();

        // Such loops, much wow! I promise, this is as efficient as it can be
        while (blocks.size() < maxVeinSize) {
            recentSearch:
            for (Generator current : recent) {
                for (int x = -1; x <= 1; x++) {
                    for (int y = -1; y <= 1; y++) {
                        for (int z = -1; z <= 1; z++) {
                            // Ignore self
                            if (x == 0 && y == 0 && z == 0) {
                                continue;
                            }

                            Generator other = plugin.getGenerator(current.getOrigin().add(x, y, z));
                            if (other == null) {
                                continue;
                            }

                            if (blocks.contains(other) || buffer.contains(other)) {
                                continue;
                            }

                            if (!template.equals(other.getTemplate()) || currentStage != other.getState().getCurrentStage()) {
                                continue;
                            }

                            if (blocks.size() + buffer.size() >= maxVeinSize) {
                                break recentSearch;
                            }

                            this.buffer.add(other);
                        }
                    }
                }
            }

            // No more blocks to allocate :D
            if (buffer.isEmpty()) {
                break;
            }

            this.recent.clear();
            this.recent.addAll(buffer);
            blocks.addAll(buffer);

            this.buffer.clear();
        }

        this.recent.clear();

        return blocks;
    }

    /**
     * Get the singleton instance of {@link VeinMiningPattern}.
     *
     * @return this pattern instance
     */
    @NotNull
    public static VeinMiningPattern getInstance() {
        return INSTANCE;
    }

}
