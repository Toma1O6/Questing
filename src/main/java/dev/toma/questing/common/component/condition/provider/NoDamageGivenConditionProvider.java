package dev.toma.questing.common.component.condition.provider;

import com.mojang.serialization.Codec;
import dev.toma.questing.common.component.condition.ConditionType;
import dev.toma.questing.common.component.condition.instance.NoDamageGivenCondition;
import dev.toma.questing.common.component.trigger.ResponseType;
import dev.toma.questing.common.init.QuestingRegistries;
import dev.toma.questing.common.quest.instance.Quest;

public class NoDamageGivenConditionProvider extends AbstractDefaultConditionProvider<NoDamageGivenCondition> {

    public static final Codec<NoDamageGivenConditionProvider> CODEC = Codec.STRING.comapFlatMap(ResponseType::fromString, Enum::name)
            .optionalFieldOf("onFail", ResponseType.PASS).codec()
            .xmap(NoDamageGivenConditionProvider::new, AbstractDefaultConditionProvider::getDefaultFailureResponse);

    public NoDamageGivenConditionProvider(ResponseType response) {
        super(response);
    }

    @Override
    public NoDamageGivenCondition createCondition(Quest quest) {
        return new NoDamageGivenCondition(this);
    }

    @Override
    public ConditionType<NoDamageGivenCondition, ?> getType() {
        return QuestingRegistries.NO_DAMAGE_GIVEN_CONDITION;
    }
}
