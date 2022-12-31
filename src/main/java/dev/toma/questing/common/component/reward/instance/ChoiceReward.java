package dev.toma.questing.common.component.reward.instance;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.toma.questing.common.component.reward.RewardType;
import dev.toma.questing.common.component.reward.provider.ChoiceRewardProvider;
import dev.toma.questing.common.quest.Quest;
import it.unimi.dsi.fastutil.ints.IntArraySet;
import it.unimi.dsi.fastutil.ints.IntSet;
import net.minecraft.entity.player.PlayerEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ChoiceReward implements Reward, SelectableReward {

    public static final Codec<ChoiceReward> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ChoiceRewardProvider.CODEC.fieldOf("provider").forGetter(t -> t.provider),
            Codec.INT.listOf().xmap(list -> (Set<Integer>) new IntArraySet(list), ArrayList::new).fieldOf("choices").forGetter(t -> t.choices),
            RewardType.REWARD_CODEC.listOf().fieldOf("rewardList").forGetter(t -> t.rewardList)
    ).apply(instance, ChoiceReward::new));
    private final ChoiceRewardProvider provider;
    private final IntSet choices;
    private final List<Reward> rewardList;

    public ChoiceReward(ChoiceRewardProvider provider, PlayerEntity player, Quest quest) {
        this.provider = provider;
        this.rewardList = this.getRewards(player, quest);
        this.choices = new IntArraySet();
    }

    public ChoiceReward(ChoiceRewardProvider provider, Set<Integer> selection, List<Reward> rewards) {
        this.provider = provider;
        this.choices = (IntSet) selection;
        this.rewardList = rewards;
    }

    @Override
    public void award(PlayerEntity player, Quest quest) {
        for (int i : choices) {
            this.rewardList.get(i).award(player, quest);
        }
    }

    @Override
    public List<Reward> getRewards() {
        return this.rewardList;
    }

    @Override
    public void select(int index, PlayerEntity player, Quest quest) {
        int limit = this.getRewardSelectionLimit(player, quest);
        if (limit < 0 || this.choices.size() < limit) {
            this.choices.add(index);
        }
    }

    @Override
    public void deselect(int index, PlayerEntity player, Quest quest) {
        this.choices.remove(index);
    }

    @Override
    public boolean isSelected(int index) {
        return this.choices.contains(index);
    }

    @Override
    public ChoiceRewardProvider getProvider() {
        return this.provider;
    }

    protected List<Reward> getRewards(PlayerEntity player, Quest quest) {
        List<Reward> rewards = this.unwrap(this.provider.getRewardList().stream().map(p -> p.createReward(player, quest)).collect(Collectors.toList()));
        int choiceLimit = this.getChoiceGenerationLimit(player, quest);
        if (choiceLimit > 0 && rewards.size() > choiceLimit) {
            return rewards.subList(0, choiceLimit);
        }
        return rewards;
    }

    protected int getRewardSelectionLimit(PlayerEntity player, Quest quest) {
        return this.getProvider().getChoiceCount();
    }

    protected int getChoiceGenerationLimit(PlayerEntity player, Quest quest) {
        return -1;
    }

    protected List<Reward> unwrap(List<Reward> in) {
        List<Reward> results = new ArrayList<>();
        for (Reward reward : in) {
            if (reward instanceof RewardHolder) {
                List<Reward> unwrapped = ((RewardHolder) reward).getRewards();
                results.addAll(this.unwrap(unwrapped));
            } else {
                results.add(reward);
            }
        }
        return results;
    }
}
