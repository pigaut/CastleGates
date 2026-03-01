package io.github.pigaut.castlegates.hook.mcmmo;

import com.gmail.nossr50.datatypes.player.*;
import com.gmail.nossr50.datatypes.skills.*;
import io.github.pigaut.yaml.amount.*;
import org.jetbrains.annotations.*;

public class HasMcMMOLevel implements McMMOPlayerCondition {

    private final Amount levelAmount;
    private final PrimarySkillType skill;

    public HasMcMMOLevel(Amount levelAmount, PrimarySkillType skill) {
        this.levelAmount = levelAmount;
        this.skill = skill;
    }

    @Override
    public @Nullable Boolean evaluate(@NotNull McMMOPlayer player) {
        return levelAmount.match(player.getSkillLevel(skill));
    }

}
