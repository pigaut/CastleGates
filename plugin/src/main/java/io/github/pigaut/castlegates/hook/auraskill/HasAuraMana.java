package io.github.pigaut.castlegates.hook.auraskill;

import dev.aurelium.auraskills.api.*;
import dev.aurelium.auraskills.api.user.*;
import io.github.pigaut.voxel.core.function.condition.player.*;
import io.github.pigaut.voxel.player.*;
import io.github.pigaut.yaml.amount.*;
import org.jetbrains.annotations.*;

public class HasAuraMana implements PlayerCondition {

    private static final AuraSkillsApi AURA_SKILLS = AuraSkillsApi.get();

    private final Amount manaAmount;

    public HasAuraMana(Amount manaAmount) {
        this.manaAmount = manaAmount;
    }

    @Override
    public @Nullable Boolean evaluate(@NotNull PlayerState player) {
        SkillsUser skillsUser = AURA_SKILLS.getUser(player.getUniqueId());
        return manaAmount.match(skillsUser.getMana());
    }

}
