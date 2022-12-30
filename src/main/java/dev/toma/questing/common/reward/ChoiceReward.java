package dev.toma.questing.common.reward;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.toma.questing.common.init.QuestingRegistries;
import dev.toma.questing.common.quest.Quest;
import dev.toma.questing.utils.Utils;
import it.unimi.dsi.fastutil.ints.IntArraySet;
import it.unimi.dsi.fastutil.ints.IntSet;
import net.minecraft.entity.player.PlayerEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChoiceReward extends MultiReward implements SelectableReward {

    public static final Codec<ChoiceReward> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            RewardType.CODEC.listOf().fieldOf("choices").forGetter(ChoiceReward::getRewards),
            Codec.intRange(1, Integer.MAX_VALUE).optionalFieldOf("choiceCount", 1).forGetter(ChoiceReward::getChoiceCount),
            RewardType.CODEC.listOf().optionalFieldOf("generated", Collections.emptyList()).forGetter(t -> t.generated)
    ).apply(instance, ChoiceReward::new));

    private final int choiceCount;
    private final IntSet choices = new IntArraySet();
    private List<Reward> generated = Collections.emptyList();

    public ChoiceReward(List<Reward> rewardList, int choiceCount) {
        super(rewardList);
        this.choiceCount = choiceCount;
    }

    protected ChoiceReward(List<Reward> rewardList, int choiceCount, List<Reward> generatedChoices) {
        this(rewardList, choiceCount);
        this.generated = generatedChoices;
    }

    @Override
    public void generate(PlayerEntity player, Quest quest) {
        super.generate(player, quest);
        this.generated = unwrapRewards(this.rewardList, player, quest);
        int limit = this.getChoiceGenerationLimit(player, quest);
        if (limit > 0 && this.generated.size() > limit) {
            this.generated = this.generated.subList(0, limit);
        }
    }

    @Override
    public RewardType<?> getType() {
        return QuestingRegistries.CHOICE_REWARD;
    }

    @Override
    public Reward copy() {
        return new ChoiceReward(Utils.instantiate(this.rewardList, Reward::copy), this.choiceCount);
    }

    @Override
    public void awardPlayer(PlayerEntity player, Quest quest) {
        for (int i : this.choices) {
            Reward reward = this.generated.get(i);
            reward.awardPlayer(player, quest);
        }
    }

    @Override
    public List<Reward> getRewards() {
        return this.generated;
    }

    @Override
    public void select(int index, PlayerEntity player, Quest quest) {
        int limit = this.getRewardSelectionLimit(player, quest);
        if (limit < 0 || limit > this.generated.size()) {
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

    public int getChoiceCount() {
        return choiceCount;
    }

    protected int getRewardSelectionLimit(PlayerEntity player, Quest quest) {
        return this.getChoiceCount();
    }

    protected int getChoiceGenerationLimit(PlayerEntity player, Quest quest) {
        return -1;
    }

    protected static List<Reward> unwrapRewards(List<Reward> source, PlayerEntity player, Quest quest) {
        List<Reward> results = new ArrayList<>();
        for (Reward src : source) {
            if (src instanceof RewardProvider) {
                List<Reward> nested = ((RewardProvider) src).getRewards();
                nested.forEach(reward -> reward.generate(player, quest));
                results.addAll(unwrapRewards(nested, player, quest));
            } else {
                results.add(src);
            }
        }
        return results;
    }
}
