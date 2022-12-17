package dev.toma.questing.reward;

import com.mojang.serialization.Codec;
import dev.toma.questing.init.QuestingRegistries;
import dev.toma.questing.utils.IdentifierHolder;
import net.minecraft.util.ResourceLocation;

import java.util.function.Predicate;

public final class RewardTransformerType<V, T extends RewardTransformer<V>> implements IdentifierHolder, Predicate<Class<?>> {

    public static final Codec<RewardTransformer<?>> CODEC = QuestingRegistries.REWARD_TRANSFORMERS.dispatch("type", RewardTransformer::getType, type -> type.codec);
    private final ResourceLocation identifier;
    private final Codec<T> codec;
    private final Class<V> type;

    public RewardTransformerType(ResourceLocation identifier, Codec<T> codec, Class<V> type) {
        this.identifier = identifier;
        this.codec = codec;
        this.type = type;
    }

    @Override
    public ResourceLocation getIdentifier() {
        return identifier;
    }

    @Override
    public boolean test(Class<?> aClass) {
        return aClass.equals(this.type);
    }
}
