package dev.toma.questing.init;

import dev.toma.questing.Questing;
import dev.toma.questing.reward.*;
import dev.toma.questing.utils.IdentifierHolder;
import net.minecraft.util.ResourceLocation;

public final class QuestingRegistries {

    // REGISTRIES -----------------------------------------------------
    public static final SimpleRegistry<ResourceLocation, RewardDistributionType<?>> REWARD_DISTRIBUTORS = new SimpleRegistry<>();
    public static final SimpleRegistry<ResourceLocation, RewardType<?>> REWARDS = new SimpleRegistry<>();
    public static final SimpleRegistry<ResourceLocation, RewardTransformerType<?, ?>> REWARD_TRANSFORMERS = new SimpleRegistry<>();

    // ENTRIES --------------------------------------------------------
    public static final RewardDistributionType<SharedRewardDistributor> SHARED_REWARD_DISTRIBUTOR = new RewardDistributionType<>(new SharedRewardDistributor.Serializer());
    public static final RewardDistributionType<SplitRewardDistributor> SPLIT_REWARD_DISTRIBUTOR = new RewardDistributionType<>(new SplitRewardDistributor.Serializer());

    public static final RewardType<ItemStackReward> ITEMSTACK_REWARD = new RewardType<>(new ItemStackReward.Serializer());
    public static final RewardType<ItemTagReward> ITEMTAG_REWARD = new RewardType<>(new ItemTagReward.Serializer());

    public static final RewardTransformerType<Integer, RewardCountTransformer> REWARD_COUNT_TRANSFORMER = new RewardTransformerType<>(internalId("count"), new RewardCountTransformer.Serializer(), Integer.class);
    public static final RewardTransformerType<AbstractItemReward.ItemList, RewardItemNbtTransformer> REWARD_ITEM_NBT_TRANSFORMER = new RewardTransformerType<>(internalId("item_nbt"), new RewardItemNbtTransformer.Serializer(), AbstractItemReward.ItemList.class);

    public static void register() {
        internalRegister(REWARD_DISTRIBUTORS, "shared", SHARED_REWARD_DISTRIBUTOR);
        internalRegister(REWARD_DISTRIBUTORS, "split", SPLIT_REWARD_DISTRIBUTOR);

        internalRegister(REWARDS, "item", ITEMSTACK_REWARD);
        internalRegister(REWARDS, "tag", ITEMTAG_REWARD);

        internalRegister(REWARD_TRANSFORMERS, REWARD_COUNT_TRANSFORMER);
        internalRegister(REWARD_TRANSFORMERS, REWARD_ITEM_NBT_TRANSFORMER);
    }

    private static <V> void internalRegister(SimpleRegistry<ResourceLocation, V> registry, String id, V value) {
        registry.register(internalId(id), value);
    }

    private static <V extends IdentifierHolder> void internalRegister(SimpleRegistry<ResourceLocation, V> registry, V value) {
        ResourceLocation identifier = value.getIdentifier();
        registry.register(identifier, value);
    }

    private static ResourceLocation internalId(String path) {
        return new ResourceLocation(Questing.MODID, path);
    }
}
