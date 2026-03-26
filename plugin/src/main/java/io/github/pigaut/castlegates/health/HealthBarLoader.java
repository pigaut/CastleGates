package io.github.pigaut.castlegates.health;

import io.github.pigaut.voxel.bukkit.*;
import io.github.pigaut.yaml.*;
import io.github.pigaut.yaml.amount.*;
import io.github.pigaut.yaml.configurator.load.*;
import io.github.pigaut.yaml.node.scalar.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class HealthBarLoader implements ConfigLoader<HealthBar> {

    @Override
    public @Nullable String getErrorDescription() {
        return "invalid health healthBar";
    }

    @Override
    public @NotNull HealthBar loadFromSection(@NotNull ConfigSection section) throws InvalidConfigException {
        String barId = section.getKey();

        Map<Integer, String> barByHealthPercentage = new HashMap<>();
        for (KeyedScalar nestedScalar : section.getNestedScalars()) {
            Amount healthPercent = nestedScalar.getKeyAs(Amount.class)
                    .require(Requirements.amountBetween(0, 100))
                    .orThrow();

            String healthBar = nestedScalar.toString(StringColor.FORMATTER);

            for (int i = 0; i <= 100; i++) {
                if (healthPercent.match(i)) {
                    barByHealthPercentage.put(i, healthBar);
                }
            }
        }

        return new HealthBar(barId, barByHealthPercentage);
    }
}
