package dev.toma.questing.utils;

import dev.toma.questing.quest.Quest;
import dev.toma.questing.reward.NestedReward;
import dev.toma.questing.reward.Reward;
import net.minecraft.entity.player.PlayerEntity;

public final class Utils {

    public static Reward getAwardableReward(Reward topLevelReward, PlayerEntity player, Quest quest) {
        Reward root = topLevelReward;
        while (root instanceof NestedReward) {
            root = ((NestedReward) root).getActualReward(player, quest);
        }
        return root;
    }

    private Utils() {}
}
