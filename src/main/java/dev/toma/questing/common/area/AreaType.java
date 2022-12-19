package dev.toma.questing.common.area;

import com.mojang.serialization.Codec;
import dev.toma.questing.utils.IdentifierHolder;
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
}
