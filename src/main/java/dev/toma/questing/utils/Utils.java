package dev.toma.questing.utils;

import dev.toma.questing.area.spawner.Spawner;
import dev.toma.questing.area.spawner.SpawnerProvider;
import dev.toma.questing.quest.Quest;
import dev.toma.questing.reward.NestedReward;
import dev.toma.questing.reward.Reward;
import net.minecraft.entity.player.PlayerEntity;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class Utils {

    public static Reward getAwardableReward(Reward topLevelReward, PlayerEntity player, Quest quest) {
        Reward root = topLevelReward;
        while (root instanceof NestedReward) {
            root = ((NestedReward) root).getActualReward(player, quest);
        }
        return root;
    }

    @SuppressWarnings("unchecked")
    public static <S extends Spawner> List<S> getProvidedSpawners(Stream<SpawnerProvider<?>> stream) {
        return stream.map(provider -> (S) provider.get()).collect(Collectors.toList());
    }

    private Utils() {}
}
