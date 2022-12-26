package dev.toma.questing.utils;

import dev.toma.questing.common.area.spawner.Spawner;
import dev.toma.questing.common.area.spawner.SpawnerProvider;
import dev.toma.questing.common.quest.Quest;
import dev.toma.questing.common.reward.NestedReward;
import dev.toma.questing.common.reward.Reward;
import net.minecraft.entity.player.PlayerEntity;

import java.util.List;
import java.util.function.Function;
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

    public static <T> List<T> instantiate(List<T> inputs, Function<T, T> converter) {
        return inputs.stream()
                .map(converter)
                .collect(Collectors.toList());
    }

    public static <E extends Enum<E>> E next(E instance) {
        return next(instance, false);
    }

    public static <E extends Enum<E>> E next(E instance, boolean looping) {
        E[] values = instance.getDeclaringClass().getEnumConstants();
        int current = instance.ordinal();
        int next = (current + 1) % values.length;
        if (!looping && next < current) {
            next = current;
        }
        return values[next];
    }

    private Utils() {}
}
