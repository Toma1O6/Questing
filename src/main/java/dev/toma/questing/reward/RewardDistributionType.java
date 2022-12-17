package dev.toma.questing.reward;

import com.mojang.serialization.Codec;
import dev.toma.questing.init.QuestingRegistries;
import dev.toma.questing.utils.IdentifierHolder;
import net.minecraft.util.ResourceLocation;

public final class RewardDistributionType<D extends RewardDistributor> implements IdentifierHolder {

    public static final Codec<RewardDistributor> CODEC = QuestingRegistries.REWARD_DISTRIBUTORS.dispatch("type", RewardDistributor::getType, type -> type.codec);
    private final ResourceLocation identifier;
    private final Codec<D> codec;

    public RewardDistributionType(ResourceLocation identifier, Codec<D> codec) {
        this.identifier = identifier;
        this.codec = codec;
    }

    @Override
    public ResourceLocation getIdentifier() {
        return identifier;
    }
}
