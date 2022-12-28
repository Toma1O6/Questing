package dev.toma.questing.common.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.toma.questing.common.condition.select.ItemSelectorType;
import dev.toma.questing.common.condition.select.Selector;
import dev.toma.questing.common.quest.Quest;
import net.minecraft.world.World;

public class UseItemCondition extends ConditionProvider<UseItemCondition.Instance> {

    public static final Codec<UseItemCondition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ItemSelectorType.CODEC.fieldOf("selector").forGetter(t -> t.itemSelector),
            Codec.BOOL.optionalFieldOf("failQuest", true).forGetter(ConditionProvider::shouldFailQuest)
    ).apply(instance, UseItemCondition::new));
    private final Selector itemSelector;

    public UseItemCondition(Selector itemSelector, boolean failsQuest) {
        super(failsQuest);
        this.itemSelector = itemSelector;
    }

    @Override
    public ConditionType<?> getType() {
        return null;
    }

    @Override
    public Instance createConditionInstance(World world, Quest quest) {
        return new Instance(this);
    }

    public static final class Instance extends Condition {

        public Instance(UseItemCondition conditionProvider) {
            super(conditionProvider);
        }

        @Override
        public void registerTriggerResponders(ConditionRegisterHandler registerHandler) {

        }
    }
}
