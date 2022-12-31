package dev.toma.questing.common.component.condition.provider;

import com.mojang.serialization.Codec;
import dev.toma.questing.common.component.condition.ConditionType;
import dev.toma.questing.common.component.condition.instance.EmptyCondition;
import dev.toma.questing.common.init.QuestingRegistries;
import dev.toma.questing.common.quest.Quest;

public class EmptyConditionProvider implements ConditionProvider<EmptyCondition> {

    public static final EmptyConditionProvider EMPTY = new EmptyConditionProvider();
    public static final Codec<EmptyConditionProvider> CODEC = Codec.unit(EMPTY);

    private EmptyConditionProvider() {}

    public static boolean isEmpty(ConditionProvider<?> provider) {
        return provider == EMPTY;
    }

    @Override
    public EmptyCondition createCondition(Quest quest) {
        return EmptyCondition.EMPTY_CONDITION;
    }

    @Override
    public ConditionType<EmptyCondition, ?> getType() {
        return QuestingRegistries.EMPTY_CONDITION;
    }
}
