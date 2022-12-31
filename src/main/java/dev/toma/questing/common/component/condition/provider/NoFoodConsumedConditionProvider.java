package dev.toma.questing.common.component.condition.provider;

import com.mojang.serialization.Codec;
import dev.toma.questing.common.component.condition.ConditionType;
import dev.toma.questing.common.component.condition.instance.NoFoodConsumedCondition;
import dev.toma.questing.common.component.trigger.ResponseType;
import dev.toma.questing.common.init.QuestingRegistries;
import dev.toma.questing.common.quest.Quest;

public class NoFoodConsumedConditionProvider extends AbstractDefaultConditionProvider<NoFoodConsumedCondition> {

    public static final Codec<NoFoodConsumedConditionProvider> CODEC = Codec.STRING
            .comapFlatMap(ResponseType::fromString, Enum::name).optionalFieldOf("onFail", ResponseType.FAIL)
            .xmap(NoFoodConsumedConditionProvider::new, AbstractDefaultConditionProvider::getDefaultFailureResponse)
            .codec();

    public NoFoodConsumedConditionProvider(ResponseType defaultFailureResponse) {
        super(defaultFailureResponse);
    }

    @Override
    public NoFoodConsumedCondition createCondition(Quest quest) {
        return new NoFoodConsumedCondition(this);
    }

    @Override
    public ConditionType<NoFoodConsumedCondition, ?> getType() {
        return QuestingRegistries.NO_FOOD_CONSUMED;
    }
}
