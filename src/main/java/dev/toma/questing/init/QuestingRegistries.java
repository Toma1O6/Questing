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
import dev.toma.questing.reward.*;
import dev.toma.questing.utils.IdentifierHolder;
import net.minecraft.util.ResourceLocation;

public final class QuestingRegistries {

    // REGISTRIES -----------------------------------------------------
    public static final SimpleRegistry<ResourceLocation, RewardDistributionType<?>> REWARD_DISTRIBUTORS = new SimpleRegistry<>();
    public static final SimpleRegistry<ResourceLocation, RewardType<?>> REWARDS = new SimpleRegistry<>();
    public static final SimpleRegistry<ResourceLocation, RewardTransformerType<?, ?>> REWARD_TRANSFORMERS = new SimpleRegistry<>();
    public static final SimpleRegistry<ResourceLocation, AreaType<?>> AREA = new SimpleRegistry<>();
    public static final SimpleRegistry<ResourceLocation, SpawnerType<?>> SPAWNER = new SimpleRegistry<>();
    public static final SimpleRegistry<ResourceLocation, SpawnerProcessorType<?>> SPAWNER_PROCESSOR = new SimpleRegistry<>();

    // ENTRIES --------------------------------------------------------
    // Reward distributors
    public static final RewardDistributionType<SharedRewardDistributor> SHARED_REWARD_DISTRIBUTOR = new RewardDistributionType<>(internalId("shared"), new SharedRewardDistributor.Serializer());
    public static final RewardDistributionType<SplitRewardDistributor> SPLIT_REWARD_DISTRIBUTOR = new RewardDistributionType<>(internalId("split"), new SplitRewardDistributor.Serializer());

    // Reward types
    public static final RewardType<ItemStackReward> ITEMSTACK_REWARD = new RewardType<>(internalId("item"), new ItemStackReward.Serializer());
    public static final RewardType<ItemTagReward> ITEMTAG_REWARD = new RewardType<>(internalId("tag"), new ItemTagReward.Serializer());

    // Reward transformers
    public static final RewardTransformerType<Integer, RewardCountTransformer> REWARD_COUNT_TRANSFORMER = new RewardTransformerType<>(internalId("count"), new RewardCountTransformer.Serializer(), Integer.class);
    public static final RewardTransformerType<AbstractItemReward.ItemList, RewardItemNbtTransformer> REWARD_ITEM_NBT_TRANSFORMER = new RewardTransformerType<>(internalId("item_nbt"), new RewardItemNbtTransformer.Serializer(), AbstractItemReward.ItemList.class);

    // Area types
    public static final AreaType<LandBasedAreaProvider> LAND_AREA = new AreaType<>(internalId("land_area"), new LandBasedAreaProvider.Serializer());

    // Spawner types
    public static final SpawnerType<WaveBasedSpawner> WAVE_BASED_SPAWNER = new SpawnerType<>(internalId("wave_based"), new WaveBasedSpawner.Serializer());
    public static final SpawnerType<RandomizedSpawner> RANDOMIZED_SPAWNER = new SpawnerType<>(internalId("randomized"), new RandomizedSpawner.Serializer());
    public static final SpawnerType<EntitySpawner> ENTITY_SPAWNER = new SpawnerType<>(internalId("entity"), new EntitySpawner.Serializer());

    // Spawner processors
    public static final SpawnerProcessorType<SetEquipmentProcessor> EQUIPMENT_SPAWNER_PROCESSOR = new SpawnerProcessorType<>(internalId("set_equipment"), new SetEquipmentProcessor.Serializer());
    public static final SpawnerProcessorType<SetEffectsProcessor> EFFECTS_SPAWNER_PROCESSOR = new SpawnerProcessorType<>(internalId("set_effects"), new SetEffectsProcessor.Serializer());

    public static void register() {
        register(REWARD_DISTRIBUTORS, SHARED_REWARD_DISTRIBUTOR);
        register(REWARD_DISTRIBUTORS, SPLIT_REWARD_DISTRIBUTOR);

        register(REWARDS, ITEMSTACK_REWARD);
        register(REWARDS, ITEMTAG_REWARD);

        register(REWARD_TRANSFORMERS, REWARD_COUNT_TRANSFORMER);
        register(REWARD_TRANSFORMERS, REWARD_ITEM_NBT_TRANSFORMER);

        register(AREA, LAND_AREA);

        register(SPAWNER, WAVE_BASED_SPAWNER);
        register(SPAWNER, RANDOMIZED_SPAWNER);
        register(SPAWNER, ENTITY_SPAWNER);

        register(SPAWNER_PROCESSOR, EQUIPMENT_SPAWNER_PROCESSOR);
        register(SPAWNER_PROCESSOR, EFFECTS_SPAWNER_PROCESSOR);
    }

    private static <V extends IdentifierHolder> void register(SimpleRegistry<ResourceLocation, V> registry, V value) {
        ResourceLocation identifier = value.getIdentifier();
        registry.register(identifier, value);
    }

    private static ResourceLocation internalId(String path) {
        return new ResourceLocation(Questing.MODID, path);
    }
}
