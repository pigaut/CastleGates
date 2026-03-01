package io.github.pigaut.castlegates.hook.mcmmo;

import com.gmail.nossr50.datatypes.player.*;
import org.jetbrains.annotations.*;

public class ResetMcMMOCooldowns implements McMMOPlayerAction {

    @Override
    public void execute(@NotNull McMMOPlayer player) {
        player.resetCooldowns();
    }

}
