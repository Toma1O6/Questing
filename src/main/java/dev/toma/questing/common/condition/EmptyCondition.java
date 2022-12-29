package dev.toma.questing.common.condition;

import com.mojang.serialization.Codec;
import dev.toma.questing.common.init.QuestingRegistries;

public class EmptyCondition implements Condition {

    public static final EmptyCondition EMPTY = new EmptyCondition();
    public static final Codec<EmptyCondition> CODEC = Codec.unit(EMPTY);

    private EmptyCondition() {}

    public static boolean isEmpty(Condition condition) {
        return condition == EMPTY;
    }

    @Override
    public ConditionType<?> getType() {
        return QuestingRegistries.EMPTY_CONDITION;
    }

    @Override
    public void registerTriggerResponders(ConditionRegisterHandler registerHandler) {
    }

    @Override
    public Condition copy() {
        return this;
    }
}
