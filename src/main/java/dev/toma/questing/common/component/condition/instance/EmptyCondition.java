package dev.toma.questing.common.component.condition.instance;

import com.mojang.serialization.Codec;
import dev.toma.questing.common.component.condition.ConditionRegisterHandler;
import dev.toma.questing.common.component.condition.provider.EmptyConditionProvider;

public class EmptyCondition implements Condition {

    public static final EmptyCondition EMPTY_CONDITION = new EmptyCondition();
    public static final Codec<EmptyCondition> CODEC = Codec.unit(EMPTY_CONDITION);

    private EmptyCondition() {}

    public static boolean isEmpty(Condition condition) {
        return condition == EMPTY_CONDITION;
    }

    @Override
    public EmptyConditionProvider getProvider() {
        return EmptyConditionProvider.EMPTY;
    }

    @Override
    public void registerTriggerResponders(ConditionRegisterHandler registerHandler) {
    }
}
