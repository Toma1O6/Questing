package dev.toma.questing.common.component.condition.provider;

import dev.toma.questing.common.component.condition.ConditionType;
import dev.toma.questing.common.component.condition.instance.Condition;
import dev.toma.questing.common.quest.instance.Quest;

public interface ConditionProvider<C extends Condition> {

    C createCondition(Quest quest);

    ConditionType<C, ?> getType();
}
