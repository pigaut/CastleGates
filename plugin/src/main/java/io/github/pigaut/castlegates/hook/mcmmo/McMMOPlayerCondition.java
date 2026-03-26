package io.github.pigaut.castlegates.hook.mcmmo;

import com.gmail.nossr50.datatypes.player.*;
import com.gmail.nossr50.util.player.*;
import io.github.pigaut.voxel.data.function.condition.player.*;
import org.bukkit.entity.*;
import org.jetbrains.annotations.*;

@FunctionalInterface
public interface McMMOPlayerCondition extends PlayerCondition {

    @Nullable Boolean evaluate(@NotNull McMMOPlayer player);

    @Override
    default @Nullable Boolean evaluate(@NotNull Player player) {
        McMMOPlayer mcMMOPlayer = UserManager.getPlayer(player);
        if (mcMMOPlayer != null) {
            return evaluate(mcMMOPlayer);
        }
        return null;
    }

}
