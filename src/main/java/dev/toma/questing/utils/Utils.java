package dev.toma.questing.utils;

import dev.toma.questing.quest.Quest;
import dev.toma.questing.reward.INestedReward;
import dev.toma.questing.reward.IReward;
import net.minecraft.entity.player.PlayerEntity;

public final class Utils {

    public static IReward getAwardableReward(IReward topLevelReward, PlayerEntity player, Quest quest) {
        IReward root = topLevelReward;
        while (root instanceof INestedReward) {
            root = ((INestedReward) root).getActualReward(player, quest);
        }
        return root;
    }

    private Utils() {}
}
