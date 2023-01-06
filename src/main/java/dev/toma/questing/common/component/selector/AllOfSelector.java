package dev.toma.questing.common.component.selector;

import com.mojang.serialization.Codec;
import dev.toma.questing.common.init.QuestingRegistries;

import java.util.List;

public class AllOfSelector<T> implements Selector<T> {

    private final List<T> elementList;

    public AllOfSelector(List<T> elementList) {
        this.elementList = elementList;
    }

    public static <T> Codec<AllOfSelector<T>> codec(Codec<T> elementCodec) {
        return elementCodec.listOf().xmap(AllOfSelector::new, AllOfSelector::getElements)
                .fieldOf("list").codec();
    }

    @Override
    public SelectorType<?, ?> getType() {
        return QuestingRegistries.ALL_OF_SELECTOR;
    }

    @Override
    public List<T> getElements() {
        return this.elementList;
    }
}
