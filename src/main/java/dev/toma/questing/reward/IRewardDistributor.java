package dev.toma.questing.reward;

import dev.toma.questing.quest.Quest;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

import java.util.List;
import java.util.Map;

public interface IRewardDistributor {

    Map<PlayerEntity, List<IReward>> generateDistributedRewards(World world, Quest quest);

    RewardDistributionType<?> getType();
}
