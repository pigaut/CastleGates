package io.github.pigaut.castlegates.hook.mcmmo;

import com.gmail.nossr50.datatypes.experience.*;
import com.gmail.nossr50.datatypes.player.*;
import com.gmail.nossr50.datatypes.skills.*;
import org.jetbrains.annotations.*;

public class LevelUpMcMMOSkill implements McMMOPlayerAction {

    private final PrimarySkillType skill;

    public LevelUpMcMMOSkill(PrimarySkillType skill) {
        this.skill = skill;
    }

    @Override
    public void execute(@NotNull McMMOPlayer player) {
        player.beginXpGain(skill, Math.max(0, player.getXpToLevel(skill) - player.getSkillXpLevel(skill)),
                XPGainReason.UNKNOWN, XPGainSource.CUSTOM);
    }

}
