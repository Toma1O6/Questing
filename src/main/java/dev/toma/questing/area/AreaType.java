package dev.toma.questing.area;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import dev.toma.questing.utils.IdentifierHolder;
import dev.toma.questing.init.QuestingRegistries;
import net.minecraft.util.ResourceLocation;

public final class AreaType<P extends AreaProvider<?>> implements IdentifierHolder {

    private final ResourceLocation identifier;
    private final Codec<P> codec;

    public AreaType(ResourceLocation identifier, Codec<P> codec) {
        this.identifier = identifier;
        this.codec = codec;
    }

    @Override
    public ResourceLocation getIdentifier() {
        return identifier;
    }

    public <R> DataResult<P> decodeData(DynamicOps<R> ops, R data) {
        return codec.parse(ops, data);
    }

    public <R> DataResult<R> encodeData(P type, DynamicOps<R> ops, R data) {
        return codec.encode(type, ops, data);
    }

    @SuppressWarnings("unchecked")
    public static <P extends AreaProvider<?>, T> P decode(DynamicOps<T> ops, T data) {
        DataResult<AreaType<?>> result = QuestingRegistries.AREA.parse(ops, data);
        AreaType<P> type = (AreaType<P>) result.getOrThrow(false, s -> {});
        DataResult<P> providerResult = type.decodeData(ops, data);
        return providerResult.getOrThrow(false, s -> {});
    }
}
