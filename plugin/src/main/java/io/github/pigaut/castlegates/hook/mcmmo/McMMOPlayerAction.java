package io.github.pigaut.castlegates.hook.mcmmo;

import com.gmail.nossr50.datatypes.player.*;
import com.gmail.nossr50.util.player.*;
import io.github.pigaut.voxel.data.function.action.player.*;
import org.bukkit.entity.*;
import org.jetbrains.annotations.*;

public interface McMMOPlayerAction extends PlayerAction {

    void execute(@NotNull McMMOPlayer player);

    @Override
    default void execute(@NotNull Player player) {
        McMMOPlayer mcMMOPlayer = UserManager.getPlayer(player);
        if (mcMMOPlayer != null) {
            execute(mcMMOPlayer);
        }
    }

}
