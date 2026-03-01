package io.github.pigaut.castlegates.hook.mcmmo;

import com.gmail.nossr50.datatypes.player.*;
import com.gmail.nossr50.util.player.*;
import io.github.pigaut.voxel.core.function.condition.player.*;
import io.github.pigaut.voxel.player.*;
import org.jetbrains.annotations.*;

@FunctionalInterface
public interface McMMOPlayerCondition extends PlayerCondition {

    @Nullable Boolean evaluate(@NotNull McMMOPlayer player);

    @Override
    default @Nullable Boolean evaluate(@NotNull PlayerState playerState) {
        McMMOPlayer player = UserManager.getPlayer(playerState.asPlayer());
        if (player != null) {
            return evaluate(player);
        }
        return null;
    }

}
