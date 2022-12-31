package dev.toma.questing.common.init;

import dev.toma.questing.Questing;
import dev.toma.questing.common.component.area.AreaType;
import dev.toma.questing.common.component.area.instance.LandArea;
import dev.toma.questing.common.component.area.provider.LandBasedAreaProvider;
import dev.toma.questing.common.component.area.spawner.EntitySpawner;
import dev.toma.questing.common.component.area.spawner.RandomizedSpawner;
import dev.toma.questing.common.component.area.spawner.SpawnerType;
import dev.toma.questing.common.component.area.spawner.WaveBasedSpawner;
import dev.toma.questing.common.component.area.spawner.processor.SetEffectsProcessor;
import dev.toma.questing.common.component.area.spawner.processor.SetEquipmentProcessor;
import dev.toma.questing.common.component.area.spawner.processor.SpawnerProcessorType;
import dev.toma.questing.common.component.condition.ConditionType;
import dev.toma.questing.common.component.condition.instance.*;
import dev.toma.questing.common.component.condition.provider.*;
import dev.toma.questing.common.component.condition.select.AnyItemSelector;
import dev.toma.questing.common.component.condition.select.ItemSelectorType;
import dev.toma.questing.common.component.condition.select.SingleItemSelector;
import dev.toma.questing.common.component.distributor.NoRewardDistributor;
import dev.toma.questing.common.component.distributor.RewardDistributionType;
import dev.toma.questing.common.component.distributor.SharedRewardDistributor;
import dev.toma.questing.common.component.distributor.SplitRewardDistributor;
import dev.toma.questing.common.component.reward.RewardType;
import dev.toma.questing.common.component.reward.instance.*;
import dev.toma.questing.common.component.reward.provider.*;
import dev.toma.questing.common.component.reward.transformer.RewardCountTransformer;
import dev.toma.questing.common.component.reward.transformer.RewardItemNbtTransformer;
import dev.toma.questing.common.component.reward.transformer.RewardTransformerType;
import dev.toma.questing.common.component.task.TaskType;
import dev.toma.questing.common.component.task.instance.KillEntityTask;
import dev.toma.questing.common.component.task.provider.KillEntityTaskProvider;
import dev.toma.questing.common.component.task.util.AnyEntityFilter;
import dev.toma.questing.common.component.task.util.EntityFilterType;
import dev.toma.questing.common.component.task.util.ExactEntityFilter;
import dev.toma.questing.common.component.task.util.HostileEntityFilter;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public final class QuestingRegistries {

    // REGISTRIES -----------------------------------------------------
    public static final Registry<RewardDistributionType<?>> REWARD_DISTRIBUTORS = new Registry<>("Reward Distributor");
    public static final Registry<RewardType<?, ?>> REWARDS = new Registry<>("Reward Type");
    public static final Registry<RewardTransformerType<?, ?>> REWARD_TRANSFORMERS = new Registry<>("Reward Transformer");
    public static final Registry<AreaType<?, ?>> AREA = new Registry<>("Area Type");
    public static final Registry<SpawnerType<?>> SPAWNER = new Registry<>("Spawner Type");
    public static final Registry<SpawnerProcessorType<?>> SPAWNER_PROCESSOR = new Registry<>("Spawner Processor Type");
    public static final Registry<ConditionType<?, ?>> CONDITION = new Registry<>("Condition Type");
    public static final Registry<ItemSelectorType<?>> ITEM_SELECTOR = new Registry<>("Item Selectors"); // used by UseItemCondition
    public static final Registry<TaskType<?, ?>> TASK = new Registry<>("Tasks");
    public static final Registry<EntityFilterType<?>> ENTITY_FILTER = new Registry<>("Entity filters");

    // ENTRIES --------------------------------------------------------
    // Reward distributors
    public static final RewardDistributionType<NoRewardDistributor> NO_REWARD_DISTRIBUTOR = new RewardDistributionType<>(internalId("no_rewards"), NoRewardDistributor.CODEC);
    public static final RewardDistributionType<SharedRewardDistributor> SHARED_REWARD_DISTRIBUTOR = new RewardDistributionType<>(internalId("shared"), SharedRewardDistributor.CODEC);
    public static final RewardDistributionType<SplitRewardDistributor> SPLIT_REWARD_DISTRIBUTOR = new RewardDistributionType<>(internalId("split"), SplitRewardDistributor.CODEC);

    // Reward types
    public static final RewardType<ItemStackReward, ItemStackRewardProvider> ITEMSTACK_REWARD = new RewardType<>(internalId("item"), ItemStackRewardProvider.CODEC, ItemStackReward.CODEC);
    public static final RewardType<ItemTagReward, ItemTagRewardProvider> ITEMTAG_REWARD = new RewardType<>(internalId("tag"), ItemTagRewardProvider.CODEC, ItemTagReward.CODEC);
    public static final RewardType<MultiReward, MultiRewardProvider> MULTI_REWARD = new RewardType<>(internalId("multi"), MultiRewardProvider.CODEC, MultiReward.CODEC);
    public static final RewardType<ChoiceReward, ChoiceRewardProvider> CHOICE_REWARD = new RewardType<>(internalId("choice"), ChoiceRewardProvider.CODEC, ChoiceReward.CODEC);
    public static final RewardType<RepeatedReward, RepeatedRewardProvider> REPEATED_REWARD = new RewardType<>(internalId("repeated"), RepeatedRewardProvider.CODEC, RepeatedReward.CODEC);

    // Reward transformers
    public static final RewardTransformerType<Integer, RewardCountTransformer> REWARD_COUNT_TRANSFORMER = new RewardTransformerType<>(internalId("count"), RewardCountTransformer.CODEC, Integer.class);
    public static final RewardTransformerType<ItemStack, RewardItemNbtTransformer> REWARD_ITEM_NBT_TRANSFORMER = new RewardTransformerType<>(internalId("item_nbt"), RewardItemNbtTransformer.CODEC, ItemStack.class);

    // Area types
    public static final AreaType<LandArea, LandBasedAreaProvider> LAND_AREA = new AreaType<>(internalId("land_area"), LandBasedAreaProvider.CODEC, LandArea.CODEC);

    // Spawner types
    public static final SpawnerType<WaveBasedSpawner> WAVE_BASED_SPAWNER = new SpawnerType<>(internalId("wave_based"), WaveBasedSpawner.CODEC);
    public static final SpawnerType<RandomizedSpawner> RANDOMIZED_SPAWNER = new SpawnerType<>(internalId("randomized"), RandomizedSpawner.CODEC);
    public static final SpawnerType<EntitySpawner> ENTITY_SPAWNER = new SpawnerType<>(internalId("entity"), EntitySpawner.CODEC);

    // Spawner processors
    public static final SpawnerProcessorType<SetEquipmentProcessor> EQUIPMENT_SPAWNER_PROCESSOR = new SpawnerProcessorType<>(internalId("set_equipment"), SetEquipmentProcessor.CODEC);
    public static final SpawnerProcessorType<SetEffectsProcessor> EFFECTS_SPAWNER_PROCESSOR = new SpawnerProcessorType<>(internalId("set_effects"), SetEffectsProcessor.CODEC);

    // Conditions
    public static final ConditionType<EmptyCondition, EmptyConditionProvider> EMPTY_CONDITION = new ConditionType<>(internalId("empty"), EmptyConditionProvider.CODEC, EmptyCondition.CODEC);
    public static final ConditionType<UseItemCondition, UseItemConditionProvider> USE_ITEM_CONDITION = new ConditionType<>(internalId("use_item"), UseItemConditionProvider.CODEC, UseItemCondition.CODEC);
    public static final ConditionType<ExplodeCondition, ExplodeConditionProvider> EXPLODE_CONDITION = new ConditionType<>(internalId("explode"), ExplodeConditionProvider.CODEC, ExplodeCondition.CODEC);
    public static final ConditionType<DistanceCondition, DistanceConditionProvider> DISTANCE_CONDITION = new ConditionType<>(internalId("distance"), DistanceConditionProvider.CODEC, DistanceCondition.CODEC);
    public static final ConditionType<AggroCondition, AggroConditionProvider> AGGRO_CONDITION = new ConditionType<>(internalId("aggro"), AggroConditionProvider.CODEC, AggroCondition.CODEC);
    public static final ConditionType<NoDamageGivenCondition, NoDamageGivenConditionProvider> NO_DAMAGE_GIVEN_CONDITION = new ConditionType<>(internalId("no_damage_given"), NoDamageGivenConditionProvider.CODEC, NoDamageGivenCondition.CODEC);
    public static final ConditionType<NoDamageTakenCondition, NoDamageTakenConditionProvider> NO_DAMAGE_TAKEN_CONDITION = new ConditionType<>(internalId("no_damage_taken"), NoDamageTakenConditionProvider.CODEC, NoDamageTakenCondition.CODEC);
    public static final ConditionType<NoHealthGainedCondition, NoHealthGainedConditionProvider> NO_HEALTH_GAINED = new ConditionType<>(internalId("no_health_gained"), NoHealthGainedConditionProvider.CODEC, NoHealthGainedCondition.CODEC);
    public static final ConditionType<NoFoodConsumedCondition, NoFoodConsumedConditionProvider> NO_FOOD_CONSUMED = new ConditionType<>(internalId("no_food_consumed"), NoFoodConsumedConditionProvider.CODEC, NoFoodConsumedCondition.CODEC);

    // Conditions - Item selectors
    public static final ItemSelectorType<AnyItemSelector> ANY_ITEM_SELECTOR = new ItemSelectorType<>(internalId("any_item"), AnyItemSelector.CODEC);
    public static final ItemSelectorType<SingleItemSelector> SINGLE_ITEM_SELECTOR = new ItemSelectorType<>(internalId("single_item"), SingleItemSelector.CODEC);

    // Tasks
    public static final TaskType<KillEntityTask, KillEntityTaskProvider> KILL_ENTITY_TASK = new TaskType<>(internalId("kill_entity"), KillEntityTaskProvider.CODEC, KillEntityTask.CODEC);

    // Tasks - Entity filters
    public static final EntityFilterType<AnyEntityFilter> ANY_ENTITY_FILTER = new EntityFilterType<>(internalId("any"), AnyEntityFilter.CODEC);
    public static final EntityFilterType<HostileEntityFilter> HOSTILE_ENTITY_FILTER = new EntityFilterType<>(internalId("hostile"), HostileEntityFilter.CODEC);
    public static final EntityFilterType<ExactEntityFilter> EXACT_ENTITY_FILTER = new EntityFilterType<>(internalId("exact"), ExactEntityFilter.CODEC);

    public static void register() {
        REWARD_DISTRIBUTORS.register(NO_REWARD_DISTRIBUTOR);
        REWARD_DISTRIBUTORS.register(SHARED_REWARD_DISTRIBUTOR);
        REWARD_DISTRIBUTORS.register(SPLIT_REWARD_DISTRIBUTOR);

        REWARDS.register(ITEMSTACK_REWARD);
        REWARDS.register(ITEMTAG_REWARD);
        REWARDS.register(MULTI_REWARD);
        REWARDS.register(CHOICE_REWARD);
        REWARDS.register(REPEATED_REWARD);

        REWARD_TRANSFORMERS.register(REWARD_COUNT_TRANSFORMER);
        REWARD_TRANSFORMERS.register(REWARD_ITEM_NBT_TRANSFORMER);

        AREA.register(LAND_AREA);

        SPAWNER.register(WAVE_BASED_SPAWNER);
        SPAWNER.register(RANDOMIZED_SPAWNER);
        SPAWNER.register(ENTITY_SPAWNER);

        SPAWNER_PROCESSOR.register(EQUIPMENT_SPAWNER_PROCESSOR);
        SPAWNER_PROCESSOR.register(EFFECTS_SPAWNER_PROCESSOR);

        CONDITION.register(EMPTY_CONDITION);
        CONDITION.register(USE_ITEM_CONDITION);
        CONDITION.register(EXPLODE_CONDITION);
        CONDITION.register(DISTANCE_CONDITION);
        CONDITION.register(AGGRO_CONDITION);
        CONDITION.register(NO_DAMAGE_GIVEN_CONDITION);
        CONDITION.register(NO_DAMAGE_TAKEN_CONDITION);
        CONDITION.register(NO_HEALTH_GAINED);

        ITEM_SELECTOR.register(ANY_ITEM_SELECTOR);
        ITEM_SELECTOR.register(SINGLE_ITEM_SELECTOR);

        TASK.register(KILL_ENTITY_TASK);

        ENTITY_FILTER.register(ANY_ENTITY_FILTER);
        ENTITY_FILTER.register(HOSTILE_ENTITY_FILTER);
        ENTITY_FILTER.register(EXACT_ENTITY_FILTER);
    }

    private static ResourceLocation internalId(String path) {
        return new ResourceLocation(Questing.MODID, path);
    }
}
