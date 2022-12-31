package dev.toma.questing.common.component.area;

import com.mojang.serialization.Codec;
import dev.toma.questing.common.component.area.instance.Area;
import dev.toma.questing.common.component.area.provider.AreaProvider;
import dev.toma.questing.common.init.QuestingRegistries;
import dev.toma.questing.utils.IdentifierHolder;
import net.minecraft.util.ResourceLocation;

public final class AreaType<A extends Area, P extends AreaProvider<A>> implements IdentifierHolder {

    public static final Codec<AreaProvider<?>> PROVIDER_CODEC = QuestingRegistries.AREA.dispatch("type", AreaProvider::getAreaType, t -> t.providerCodec);
    public static final Codec<Area> AREA_CODEC = QuestingRegistries.AREA.dispatch("type", area -> area.getAreaProvider().getAreaType(), t -> t.areaCodec);
    private final ResourceLocation identifier;
    private final Codec<P> providerCodec;
    private final Codec<A> areaCodec;

    public AreaType(ResourceLocation identifier, Codec<P> providerCodec, Codec<A> areaCodec) {
        this.identifier = identifier;
        this.providerCodec = providerCodec;
        this.areaCodec = areaCodec;
    }

    @Override
    public ResourceLocation getIdentifier() {
        return identifier;
    }
}
