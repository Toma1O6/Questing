package dev.toma.questing.common.reward;

import dev.toma.questing.common.quest.Quest;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

import java.util.List;
import java.util.Map;

public interface RewardDistributor {

    Map<PlayerEntity, List<Reward>> generateDistributedRewards(World world, Quest quest);

    RewardDistributionType<?> getType();
}
