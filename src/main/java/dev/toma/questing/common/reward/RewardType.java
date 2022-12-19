package dev.toma.questing.common.reward;

import com.mojang.serialization.Codec;
import dev.toma.questing.common.init.QuestingRegistries;
import dev.toma.questing.utils.IdentifierHolder;
import net.minecraft.util.ResourceLocation;

public final class RewardType<R extends Reward> implements IdentifierHolder {

    public static final Codec<Reward> CODEC = QuestingRegistries.REWARDS.dispatch("type", Reward::getType, type -> type.codec);
    private final ResourceLocation identifier;
    private final Codec<R> codec;

    public RewardType(ResourceLocation identifier, Codec<R> codec) {
        this.identifier = identifier;
        this.codec = codec;
    }

    @Override
    public ResourceLocation getIdentifier() {
        return identifier;
    }
}
