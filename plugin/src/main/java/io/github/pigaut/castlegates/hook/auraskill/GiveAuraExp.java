package io.github.pigaut.castlegates.hook.auraskill;

import dev.aurelium.auraskills.api.*;
import dev.aurelium.auraskills.api.skill.*;
import dev.aurelium.auraskills.api.user.*;
import io.github.pigaut.voxel.core.function.action.player.*;
import io.github.pigaut.voxel.player.*;
import io.github.pigaut.yaml.amount.*;
import org.jetbrains.annotations.*;

public class GiveAuraExp implements PlayerAction {

    private static final AuraSkillsApi AURA_SKILLS = AuraSkillsApi.get();

    private final Amount exp;
    private final Skills skill;
    private final boolean raw;

    public GiveAuraExp(Amount exp, Skills skill, boolean raw) {
        this.exp = exp;
        this.skill = skill;
        this.raw = raw;
    }

    @Override
    public void execute(@NotNull PlayerState player) {
        if (!skill.isEnabled()) {
            return;
        }

        SkillsUser skillsUser = AURA_SKILLS.getUser(player.getUniqueId());
        if (raw) {
            skillsUser.addSkillXpRaw(skill, exp.intValue());
        }
        else {
            skillsUser.addSkillXp(skill, exp.intValue());
        }
    }

}
