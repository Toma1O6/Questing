package dev.toma.questing.init;

import dev.toma.questing.Questing;
import dev.toma.questing.area.AreaType;
import dev.toma.questing.area.LandBasedAreaProvider;
import dev.toma.questing.area.spawner.EntitySpawner;
import dev.toma.questing.area.spawner.RandomizedSpawner;
import dev.toma.questing.area.spawner.SpawnerType;
import dev.toma.questing.area.spawner.WaveBasedSpawner;
import dev.toma.questing.area.spawner.processor.SetEffectsProcessor;
import dev.toma.questing.area.spawner.processor.SetEquipmentProcessor;
import dev.toma.questing.area.spawner.processor.SpawnerProcessorType;
import dev.toma.questing.condition.ConditionType;
import dev.toma.questing.reward.*;
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

    // ENTRIES --------------------------------------------------------
    // Reward distributors
    public static final RewardDistributionType<SharedRewardDistributor> SHARED_REWARD_DISTRIBUTOR = new RewardDistributionType<>(internalId("shared"), SharedRewardDistributor.CODEC);
    public static final RewardDistributionType<SplitRewardDistributor> SPLIT_REWARD_DISTRIBUTOR = new RewardDistributionType<>(internalId("split"), SplitRewardDistributor.CODEC);

    // Reward types
    public static final RewardType<ItemStackReward> ITEMSTACK_REWARD = new RewardType<>(internalId("item"), ItemStackReward.CODEC);
    public static final RewardType<ItemTagReward> ITEMTAG_REWARD = new RewardType<>(internalId("tag"), ItemTagReward.CODEC);

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

    public static void register() {
        REWARD_DISTRIBUTORS.register(SHARED_REWARD_DISTRIBUTOR);
        REWARD_DISTRIBUTORS.register(SPLIT_REWARD_DISTRIBUTOR);

        REWARDS.register(ITEMSTACK_REWARD);
        REWARDS.register(ITEMTAG_REWARD);

        REWARD_TRANSFORMERS.register(REWARD_COUNT_TRANSFORMER);
        REWARD_TRANSFORMERS.register(REWARD_ITEM_NBT_TRANSFORMER);

        AREA.register(LAND_AREA);

        SPAWNER.register(WAVE_BASED_SPAWNER);
        SPAWNER.register(RANDOMIZED_SPAWNER);
        SPAWNER.register(ENTITY_SPAWNER);

        SPAWNER_PROCESSOR.register(EQUIPMENT_SPAWNER_PROCESSOR);
        SPAWNER_PROCESSOR.register(EFFECTS_SPAWNER_PROCESSOR);
    }

    private static ResourceLocation internalId(String path) {
        return new ResourceLocation(Questing.MODID, path);
    }
}
