package io.github.pigaut.castlegates.player;

import io.github.pigaut.castlegates.*;
import io.github.pigaut.voxel.core.player.*;
import org.jetbrains.annotations.*;

public class GatesPlayerManager extends PlayerStateManager<CastleGatesPlugin, GatesPlayer> {

    public GatesPlayerManager(@NotNull CastleGatesPlugin plugin) {
        super(plugin, GatesPlayer::new);
    }

}
