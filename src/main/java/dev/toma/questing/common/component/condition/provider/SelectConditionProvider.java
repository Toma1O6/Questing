package dev.toma.questing.common.component.condition.provider;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.toma.questing.common.component.condition.ConditionType;
import dev.toma.questing.common.component.condition.instance.SelectCondition;
import dev.toma.questing.common.component.selector.Selector;
import dev.toma.questing.common.component.selector.SelectorType;
import dev.toma.questing.common.component.trigger.ResponseType;
import dev.toma.questing.common.init.QuestingRegistries;
import dev.toma.questing.common.quest.instance.Quest;
import dev.toma.questing.utils.Codecs;

import java.util.List;

public class SelectConditionProvider extends AbstractDefaultConditionProvider<SelectCondition> {

    public static final Codec<SelectConditionProvider> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codecs.enumCodecComap(ResponseType.class, ResponseType::fromString, Enum::name, String::toUpperCase).optionalFieldOf("onFail", ResponseType.SKIP).forGetter(AbstractDefaultConditionProvider::getDefaultFailureResponse),
            SelectorType.codec(ConditionType.PROVIDER_CODEC).fieldOf("selector").forGetter(t -> t.selector)
    ).apply(instance, SelectConditionProvider::new));
    private final Selector<ConditionProvider<?>> selector;

    public SelectConditionProvider(ResponseType defaultResponseType, Selector<ConditionProvider<?>> selector) {
        super(defaultResponseType);
        this.selector = selector;
    }

    @Override
    public SelectCondition createCondition(Quest quest) {
        return new SelectCondition(this, quest);
    }

    @Override
    public ConditionType<SelectCondition, ?> getType() {
        return QuestingRegistries.SELECT_CONDITION;
    }

    public List<ConditionProvider<?>> getConditions() {
        return this.selector.getElements();
    }
}
