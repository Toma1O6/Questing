package dev.toma.questing.common.init;

import dev.toma.questing.Questing;
import dev.toma.questing.common.area.AreaType;
import dev.toma.questing.common.area.LandBasedAreaProvider;
import dev.toma.questing.common.area.spawner.EntitySpawner;
import dev.toma.questing.common.area.spawner.RandomizedSpawner;
import dev.toma.questing.common.area.spawner.SpawnerType;
import dev.toma.questing.common.area.spawner.WaveBasedSpawner;
import dev.toma.questing.common.area.spawner.processor.SetEffectsProcessor;
import dev.toma.questing.common.area.spawner.processor.SetEquipmentProcessor;
import dev.toma.questing.common.area.spawner.processor.SpawnerProcessorType;
import dev.toma.questing.common.condition.*;
import dev.toma.questing.common.condition.select.AnyItemSelector;
import dev.toma.questing.common.condition.select.ItemSelectorType;
import dev.toma.questing.common.condition.select.SingleItemSelector;
import dev.toma.questing.common.reward.*;
import dev.toma.questing.common.reward.distributor.NoRewardDistributor;
import dev.toma.questing.common.reward.distributor.RewardDistributionType;
import dev.toma.questing.common.reward.distributor.SharedRewardDistributor;
import dev.toma.questing.common.reward.distributor.SplitRewardDistributor;
import net.minecraft.util.ResourceLocation;

public final class QuestingRegistries {

    // REGISTRIES -----------------------------------------------------
    public static final Registry<RewardDistributionType<?>> REWARD_DISTRIBUTORS = new Registry<>("Reward Distributor");
    public static final Registry<RewardType<?>> REWARDS = new Registry<>("Reward Type");
    public static final Registry<RewardTransformerType<?, ?>> REWARD_TRANSFORMERS = new Registry<>("Reward Transformer");
    public static final Registry<AreaType<?>> AREA = new Registry<>("Area Type");
    public static final Registry<SpawnerType<?>> SPAWNER = new Registry<>("Spawner Type");
    public static final Registry<SpawnerProcessorType<?>> SPAWNER_PROCESSOR = new Registry<>("Spawner Processor Type");
    public static final Registry<ConditionType<?>> CONDITION = new Registry<>("Condition Type");
    public static final Registry<ItemSelectorType<?>> ITEM_SELECTOR = new Registry<>("Item Selectors"); // used by UseItemCondition

    // ENTRIES --------------------------------------------------------
    // Reward distributors
    public static final RewardDistributionType<NoRewardDistributor> NO_REWARD_DISTRIBUTOR = new RewardDistributionType<>(internalId("no_rewards"), NoRewardDistributor.CODEC);
    public static final RewardDistributionType<SharedRewardDistributor> SHARED_REWARD_DISTRIBUTOR = new RewardDistributionType<>(internalId("shared"), SharedRewardDistributor.CODEC);
    public static final RewardDistributionType<SplitRewardDistributor> SPLIT_REWARD_DISTRIBUTOR = new RewardDistributionType<>(internalId("split"), SplitRewardDistributor.CODEC);

    // Reward types
    public static final RewardType<ItemStackReward> ITEMSTACK_REWARD = new RewardType<>(internalId("item"), ItemStackReward.CODEC);
    public static final RewardType<ItemTagReward> ITEMTAG_REWARD = new RewardType<>(internalId("tag"), ItemTagReward.CODEC);
    public static final RewardType<MultiReward> MULTI_REWARD = new RewardType<>(internalId("multi"), MultiReward.CODEC);

    // Reward transformers
    public static final RewardTransformerType<Integer, RewardCountTransformer> REWARD_COUNT_TRANSFORMER = new RewardTransformerType<>(internalId("count"), RewardCountTransformer.CODEC, Integer.class);
    public static final RewardTransformerType<AbstractItemReward.ItemList, RewardItemNbtTransformer> REWARD_ITEM_NBT_TRANSFORMER = new RewardTransformerType<>(internalId("item_nbt"), RewardItemNbtTransformer.CODEC, AbstractItemReward.ItemList.class);

    // Area types
    public static final AreaType<LandBasedAreaProvider> LAND_AREA = new AreaType<>(internalId("land_area"), LandBasedAreaProvider.CODEC);

    // Spawner types
    public static final SpawnerType<WaveBasedSpawner> WAVE_BASED_SPAWNER = new SpawnerType<>(internalId("wave_based"), WaveBasedSpawner.CODEC);
    public static final SpawnerType<RandomizedSpawner> RANDOMIZED_SPAWNER = new SpawnerType<>(internalId("randomized"), RandomizedSpawner.CODEC);
    public static final SpawnerType<EntitySpawner> ENTITY_SPAWNER = new SpawnerType<>(internalId("entity"), EntitySpawner.CODEC);

    // Spawner processors
    public static final SpawnerProcessorType<SetEquipmentProcessor> EQUIPMENT_SPAWNER_PROCESSOR = new SpawnerProcessorType<>(internalId("set_equipment"), SetEquipmentProcessor.CODEC);
    public static final SpawnerProcessorType<SetEffectsProcessor> EFFECTS_SPAWNER_PROCESSOR = new SpawnerProcessorType<>(internalId("set_effects"), SetEffectsProcessor.CODEC);

    // Conditions
    public static final ConditionType<EmptyCondition> EMPTY_CONDITION = new ConditionType<>(internalId("empty"), EmptyCondition.CODEC);
    public static final ConditionType<UseItemCondition> USE_ITEM_CONDITION = new ConditionType<>(internalId("use_item"), UseItemCondition.CODEC);
    public static final ConditionType<ExplodeCondition> EXPLODE_CONDITION = new ConditionType<>(internalId("explode"), ExplodeCondition.CODEC);
    public static final ConditionType<DistanceCondition> DISTANCE_CONDITION = new ConditionType<>(internalId("distance"), DistanceCondition.CODEC);
    public static final ConditionType<AggroCondition> AGGRO_CONDITION = new ConditionType<>(internalId("aggro"), AggroCondition.CODEC);
    public static final ConditionType<NoDamageGivenCondition> NO_DAMAGE_GIVEN_CONDITION = new ConditionType<>(internalId("no_damage_given"), NoDamageGivenCondition.CODEC);
    public static final ConditionType<NoDamageTakenCondition> NO_DAMAGE_TAKEN_CONDITION = new ConditionType<>(internalId("no_damage_taken"), NoDamageTakenCondition.CODEC);
    public static final ConditionType<NoHealthGainedCondition> NO_HEALTH_GAINED = new ConditionType<>(internalId("no_health_gained"), NoHealthGainedCondition.CODEC);
    public static final ConditionType<NoFoodConsumedCondition> NO_FOOD_CONSUMED = new ConditionType<>(internalId("no_food_consumed"), NoFoodConsumedCondition.CODEC);

    // Conditions - Item selectors
    public static final ItemSelectorType<AnyItemSelector> ANY_ITEM_SELECTOR = new ItemSelectorType<>(internalId("any_item"), AnyItemSelector.CODEC);
    public static final ItemSelectorType<SingleItemSelector> SINGLE_ITEM_SELECTOR = new ItemSelectorType<>(internalId("single_item"), SingleItemSelector.CODEC);

    public static void register() {
        REWARD_DISTRIBUTORS.register(NO_REWARD_DISTRIBUTOR);
        REWARD_DISTRIBUTORS.register(SHARED_REWARD_DISTRIBUTOR);
        REWARD_DISTRIBUTORS.register(SPLIT_REWARD_DISTRIBUTOR);

        REWARDS.register(ITEMSTACK_REWARD);
        REWARDS.register(ITEMTAG_REWARD);
        REWARDS.register(MULTI_REWARD);

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
    }

    private static ResourceLocation internalId(String path) {
        return new ResourceLocation(Questing.MODID, path);
    }
}
