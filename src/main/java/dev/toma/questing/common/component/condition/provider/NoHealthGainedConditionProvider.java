package dev.toma.questing.common.component.condition.provider;

import com.mojang.serialization.Codec;
import dev.toma.questing.common.component.condition.ConditionType;
import dev.toma.questing.common.component.condition.instance.NoHealthGainedCondition;
import dev.toma.questing.common.component.trigger.ResponseType;
import dev.toma.questing.common.init.QuestingRegistries;
import dev.toma.questing.common.quest.instance.Quest;

public class NoHealthGainedConditionProvider extends AbstractDefaultConditionProvider<NoHealthGainedCondition> {

    public static final Codec<NoHealthGainedConditionProvider> CODEC = Codec.STRING.
            comapFlatMap(ResponseType::fromString, Enum::name).optionalFieldOf("onFail", ResponseType.FAIL)
            .xmap(NoHealthGainedConditionProvider::new, AbstractDefaultConditionProvider::getDefaultFailureResponse).codec();

    public NoHealthGainedConditionProvider(ResponseType defaultResponseType) {
        super(defaultResponseType);
    }

    @Override
    public NoHealthGainedCondition createCondition(Quest quest) {
        return new NoHealthGainedCondition(this);
    }

    @Override
    public ConditionType<NoHealthGainedCondition, ?> getType() {
        return QuestingRegistries.NO_HEALTH_GAINED;
    }
}
