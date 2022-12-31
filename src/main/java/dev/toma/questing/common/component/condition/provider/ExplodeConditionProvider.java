package dev.toma.questing.common.component.condition.provider;

import com.mojang.serialization.Codec;
import dev.toma.questing.common.component.condition.ConditionType;
import dev.toma.questing.common.component.condition.instance.ExplodeCondition;
import dev.toma.questing.common.component.trigger.ResponseType;
import dev.toma.questing.common.init.QuestingRegistries;
import dev.toma.questing.common.quest.Quest;

public class ExplodeConditionProvider extends AbstractDefaultConditionProvider<ExplodeCondition> {

    public static final Codec<ExplodeConditionProvider> CODEC = Codec.STRING.comapFlatMap(ResponseType::fromString, Enum::name)
            .optionalFieldOf("onFail", ResponseType.PASS)
            .xmap(ExplodeConditionProvider::new, AbstractDefaultConditionProvider::getDefaultFailureResponse)
            .codec();

    public ExplodeConditionProvider(ResponseType defaultFailureResponse) {
        super(defaultFailureResponse);
    }

    @Override
    public ExplodeCondition createCondition(Quest quest) {
        return new ExplodeCondition(this);
    }

    @Override
    public ConditionType<ExplodeCondition, ?> getType() {
        return QuestingRegistries.EXPLODE_CONDITION;
    }
}
