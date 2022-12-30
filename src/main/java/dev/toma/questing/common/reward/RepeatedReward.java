package dev.toma.questing.common.reward;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.toma.questing.common.init.QuestingRegistries;
import dev.toma.questing.common.quest.Quest;
import net.minecraft.entity.player.PlayerEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RepeatedReward implements Reward, RewardProvider {

    public static final Codec<RepeatedReward> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            RewardType.CODEC.fieldOf("reward").forGetter(t -> t.source),
            Codec.intRange(1, Integer.MAX_VALUE).fieldOf("count").forGetter(t -> t.count),
            RewardType.CODEC.listOf().optionalFieldOf("generated", Collections.emptyList()).forGetter(t -> t.generated)
    ).apply(instance, RepeatedReward::new));
    private final Reward source;
    private final int count;
    private List<Reward> generated;

    protected RepeatedReward(Reward source, int count, List<Reward> generated) {
        this.source = source;
        this.count = count;
        this.generated = generated;
    }

    public RepeatedReward(Reward source, int count) {
        this(source, count, Collections.emptyList());
    }

    @Override
    public RewardType<?> getType() {
        return QuestingRegistries.REPEATED_REWARD;
    }

    @Override
    public Reward copy() {
        return new RepeatedReward(this.source.copy(), count);
    }

    @Override
    public void generate(PlayerEntity player, Quest quest) {
        this.source.generate(player, quest);
        this.generated = new ArrayList<>(this.count);
        for (int i = 0; i < this.count; i++) {
            Reward copy = this.source.copy();
            copy.generate(player, quest);
            this.generated.add(copy);
        }
    }

    @Override
    public void awardPlayer(PlayerEntity player, Quest quest) {
        this.generated.forEach(reward -> reward.awardPlayer(player, quest));
    }

    @Override
    public List<Reward> getRewards() {
        return this.generated;
    }
}
