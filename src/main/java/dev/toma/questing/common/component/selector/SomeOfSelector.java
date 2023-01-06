package dev.toma.questing.common.component.selector;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.toma.questing.common.init.QuestingRegistries;
import dev.toma.questing.utils.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class SomeOfSelector<T> implements Selector<T> {

    private static final Random RANDOM = new Random();
    private final List<T> elementList;
    private final int selectCount;

    public SomeOfSelector(List<T> elementList, int selectCount) {
        this.elementList = elementList;
        this.selectCount = Math.max(selectCount, 0);
    }

    public static <T> Codec<SomeOfSelector<T>> codec(Codec<T> elementCodec) {
        return RecordCodecBuilder.create(instance -> instance.group(
                elementCodec.listOf().fieldOf("list").forGetter(t -> t.elementList),
                Codec.INT.optionalFieldOf("count", 1).forGetter(t -> t.selectCount)
        ).apply(instance, SomeOfSelector::new));
    }

    @Override
    public SelectorType<?, ?> getType() {
        return QuestingRegistries.SOME_OF_SELECTOR;
    }

    @Override
    public List<T> getElements() {
        List<T> list = new ArrayList<>(selectCount);
        if (selectCount == 0 || elementList.isEmpty()) {
            return Collections.emptyList();
        }
        for (int i = 0; i < selectCount; i++) {
            list.add(Utils.getRandomListElement(elementList, RANDOM));
        }
        return list;
    }
}
