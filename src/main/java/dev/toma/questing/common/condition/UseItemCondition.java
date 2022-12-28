package dev.toma.questing.common.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.toma.questing.common.condition.select.ItemSelectorType;
import dev.toma.questing.common.condition.select.Selector;
import dev.toma.questing.common.init.QuestingRegistries;
import dev.toma.questing.common.quest.Quest;
import dev.toma.questing.common.trigger.ResponseType;
import net.minecraft.world.World;

public class UseItemCondition extends ConditionProvider<UseItemCondition.Instance> {

    public static final Codec<UseItemCondition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.comapFlatMap(ResponseType::fromString, Enum::name).optionalFieldOf("onFail", ResponseType.PASS).forGetter(ConditionProvider::getDefaultFailureResponse),
            ItemSelectorType.CODEC.fieldOf("selector").forGetter(t -> t.itemSelector)
    ).apply(instance, UseItemCondition::new));
    private final Selector itemSelector;

    public UseItemCondition(ResponseType defaultFailureResponse, Selector itemSelector) {
        super(defaultFailureResponse);
        this.itemSelector = itemSelector;
    }

    @Override
    public ConditionType<?> getType() {
        return QuestingRegistries.USE_ITEM_CONDITION;
    }

    @Override
    public Instance createConditionInstance(World world, Quest quest) {
        return new Instance(this);
    }

    static final class Instance extends Condition {

        public Instance(UseItemCondition conditionProvider) {
            super(conditionProvider);
        }

        @Override
        public void registerTriggerResponders(ConditionRegisterHandler registerHandler) {

        }
    }
}
