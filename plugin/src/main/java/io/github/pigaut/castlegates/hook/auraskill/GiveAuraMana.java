package io.github.pigaut.castlegates.hook.auraskill;

import dev.aurelium.auraskills.api.*;
import dev.aurelium.auraskills.api.user.*;
import io.github.pigaut.voxel.core.function.action.player.*;
import io.github.pigaut.voxel.player.*;
import io.github.pigaut.yaml.amount.*;
import org.jetbrains.annotations.*;

public class GiveAuraMana implements PlayerAction {

    private static final AuraSkillsApi AURA_SKILLS = AuraSkillsApi.get();

    private final Amount manaAmount;

    public GiveAuraMana(Amount manaAmount) {
        this.manaAmount = manaAmount;
    }

    @Override
    public void execute(@NotNull PlayerState player) {
        SkillsUser skillsUser = AURA_SKILLS.getUser(player.getUniqueId());

        double totalMana = skillsUser.getMana() + manaAmount.doubleValue();
        skillsUser.setMana(Math.min(totalMana, skillsUser.getMaxMana()));
    }

}
