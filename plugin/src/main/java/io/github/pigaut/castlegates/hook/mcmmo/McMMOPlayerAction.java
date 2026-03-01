package io.github.pigaut.castlegates.hook.mcmmo;

import com.gmail.nossr50.datatypes.player.*;
import com.gmail.nossr50.util.player.*;
import io.github.pigaut.voxel.core.function.action.player.*;
import io.github.pigaut.voxel.player.*;
import org.jetbrains.annotations.*;

public interface McMMOPlayerAction extends PlayerAction {

    void execute(@NotNull McMMOPlayer player);

    @Override
    default void execute(@NotNull PlayerState playerState) {
        McMMOPlayer player = UserManager.getPlayer(playerState.asPlayer());
        if (player != null) {
            execute(player);
        }
    }

}
