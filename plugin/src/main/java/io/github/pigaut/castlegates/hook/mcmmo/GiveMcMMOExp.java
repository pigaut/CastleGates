package io.github.pigaut.castlegates.hook.mcmmo;

import com.gmail.nossr50.datatypes.experience.*;
import com.gmail.nossr50.datatypes.player.*;
import com.gmail.nossr50.datatypes.skills.*;
import io.github.pigaut.yaml.amount.*;
import org.jetbrains.annotations.*;

public class GiveMcMMOExp implements McMMOPlayerAction {

    private final Amount expAmount;
    private final PrimarySkillType skill;

    public GiveMcMMOExp(Amount expAmount, PrimarySkillType skill) {
        this.expAmount = expAmount;
        this.skill = skill;
    }

    @Override
    public void execute(@NotNull McMMOPlayer player) {
        player.beginXpGain(skill, expAmount.intValue(), XPGainReason.UNKNOWN, XPGainSource.CUSTOM);
    }

}
