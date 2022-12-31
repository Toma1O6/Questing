package dev.toma.questing.common.component.reward;

import com.mojang.serialization.Codec;
import dev.toma.questing.common.component.reward.instance.Reward;
import dev.toma.questing.common.component.reward.provider.RewardProvider;
import dev.toma.questing.common.init.QuestingRegistries;
import dev.toma.questing.utils.IdentifierHolder;
import net.minecraft.util.ResourceLocation;

public final class RewardType<R extends Reward, P extends RewardProvider<R>> implements IdentifierHolder {

    public static final Codec<RewardProvider<?>> PROVIDER_CODEC = QuestingRegistries.REWARDS.dispatch("type", RewardProvider::getType, type -> type.providerCodec);
    public static final Codec<Reward> REWARD_CODEC = QuestingRegistries.REWARDS.dispatch("type", reward -> reward.getProvider().getType(), type -> type.rewardCodec);
    private final ResourceLocation identifier;
    private final Codec<P> providerCodec;
    private final Codec<R> rewardCodec;

    public RewardType(ResourceLocation identifier, Codec<P> providerCodec, Codec<R> rewardCodec) {
        this.identifier = identifier;
        this.providerCodec = providerCodec;
        this.rewardCodec = rewardCodec;
    }

    @Override
    public ResourceLocation getIdentifier() {
        return identifier;
    }
}
