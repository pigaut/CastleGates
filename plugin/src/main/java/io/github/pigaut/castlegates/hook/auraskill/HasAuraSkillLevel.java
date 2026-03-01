package io.github.pigaut.castlegates.hook.auraskill;

import dev.aurelium.auraskills.api.*;
import dev.aurelium.auraskills.api.skill.*;
import dev.aurelium.auraskills.api.user.*;
import io.github.pigaut.voxel.core.function.condition.player.*;
import io.github.pigaut.voxel.player.*;
import io.github.pigaut.yaml.amount.*;
import org.jetbrains.annotations.*;

public class HasAuraSkillLevel implements PlayerCondition {

    private static final AuraSkillsApi AURA_SKILLS = AuraSkillsApi.get();

    private final Amount level;
    private final Skills skill;

    public HasAuraSkillLevel(Amount level, Skills skill) {
        this.level = level;
        this.skill = skill;
    }

    @Override
    public @Nullable Boolean evaluate(@NotNull PlayerState player) {
        if (!skill.isEnabled()) {
            return null;
        }

        SkillsUser skillsUser = AURA_SKILLS.getUser(player.getUniqueId());
        return level.match(skillsUser.getSkillLevel(skill));
    }

}
