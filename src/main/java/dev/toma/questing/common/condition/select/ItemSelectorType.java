package dev.toma.questing.common.condition.select;

import com.mojang.serialization.Codec;
import dev.toma.questing.common.init.QuestingRegistries;
import dev.toma.questing.utils.IdentifierHolder;
import net.minecraft.util.ResourceLocation;

public final class ItemSelectorType<S extends Selector> implements IdentifierHolder {

    public static final Codec<Selector> CODEC = QuestingRegistries.ITEM_SELECTOR.dispatch("type", Selector::getSelectorType, type -> type.codec);
    private final ResourceLocation identifier;
    private final Codec<S> codec;

    public ItemSelectorType(ResourceLocation identifier, Codec<S> codec) {
        this.identifier = identifier;
        this.codec = codec;
    }

    @Override
    public ResourceLocation getIdentifier() {
        return identifier;
    }
}
