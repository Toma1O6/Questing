package dev.toma.questing.common.component.selector;

import com.mojang.serialization.Codec;
import dev.toma.questing.common.init.QuestingRegistries;
import dev.toma.questing.utils.IdentifierHolder;
import net.minecraft.util.ResourceLocation;

import java.util.function.Function;

public final class SelectorType<T, S extends Selector<T>> implements IdentifierHolder {

    private final ResourceLocation identifier;
    private final Function<Codec<T>, Codec<S>> codecFn;

    public SelectorType(ResourceLocation identifier, Function<Codec<T>, Codec<S>> codecFn) {
        this.identifier = identifier;
        this.codecFn = codecFn;
    }

    @Override
    public ResourceLocation getIdentifier() {
        return identifier;
    }

    @SuppressWarnings("unchecked")
    public static <T, S extends Selector<T>> Codec<S> codec(Codec<T> elementCodec) {
        return QuestingRegistries.SELECTORS.dispatch("type", Selector::getType, selectorType -> ((SelectorType<T, S>) selectorType).codecFn.apply(elementCodec));
    }
}
