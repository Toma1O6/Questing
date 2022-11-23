package dev.toma.questing.reward;

import dev.toma.questing.quest.Quest;
import net.minecraft.world.World;

public interface IRewardDistributor {

    void distribute(World world, Quest quest);

    RewardDistributionType<?> getType();
}
