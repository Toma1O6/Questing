package dev.toma.questing.common.component.condition.provider;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.toma.questing.common.component.condition.ConditionType;
import dev.toma.questing.common.component.condition.instance.UseItemCondition;
import dev.toma.questing.common.component.condition.select.ItemSelectorType;
import dev.toma.questing.common.component.condition.select.Selector;
import dev.toma.questing.common.component.trigger.ResponseType;
import dev.toma.questing.common.init.QuestingRegistries;
import dev.toma.questing.common.quest.instance.Quest;

public class UseItemConditionProvider extends AbstractDefaultConditionProvider<UseItemCondition> {

    public static final Codec<UseItemConditionProvider> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.comapFlatMap(ResponseType::fromString, Enum::name).optionalFieldOf("onFail", ResponseType.PASS).forGetter(AbstractDefaultConditionProvider::getDefaultFailureResponse),
            ItemSelectorType.CODEC.fieldOf("selector").forGetter(t -> t.itemSelector)
    ).apply(instance, UseItemConditionProvider::new));
    private final Selector itemSelector;

    public UseItemConditionProvider(ResponseType defaultFailureResponse, Selector itemSelector) {
        super(defaultFailureResponse);
        this.itemSelector = itemSelector;
    }

    @Override
    public UseItemCondition createCondition(Quest quest) {
        return new UseItemCondition(this);
    }

    @Override
    public ConditionType<UseItemCondition, ?> getType() {
        return QuestingRegistries.USE_ITEM_CONDITION;
    }

    public Selector getItemSelector() {
        return itemSelector;
    }
}
