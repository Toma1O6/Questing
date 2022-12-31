package dev.toma.questing.common.component.condition.provider;

import com.mojang.serialization.Codec;
import dev.toma.questing.common.component.condition.ConditionType;
import dev.toma.questing.common.component.condition.instance.NoDamageTakenCondition;
import dev.toma.questing.common.component.trigger.ResponseType;
import dev.toma.questing.common.init.QuestingRegistries;
import dev.toma.questing.common.quest.instance.Quest;

public class NoDamageTakenConditionProvider extends AbstractDefaultConditionProvider<NoDamageTakenCondition> {

    public static final Codec<NoDamageTakenConditionProvider> CODEC = Codec.STRING.comapFlatMap(ResponseType::fromString, Enum::name)
            .optionalFieldOf("onFail", ResponseType.PASS).codec()
            .xmap(NoDamageTakenConditionProvider::new, AbstractDefaultConditionProvider::getDefaultFailureResponse);

    public NoDamageTakenConditionProvider(ResponseType response) {
        super(response);
    }

    @Override
    public ConditionType<NoDamageTakenCondition, ?> getType() {
        return QuestingRegistries.NO_DAMAGE_TAKEN_CONDITION;
    }

    @Override
    public NoDamageTakenCondition createCondition(Quest quest) {
        return new NoDamageTakenCondition(this);
    }
}
