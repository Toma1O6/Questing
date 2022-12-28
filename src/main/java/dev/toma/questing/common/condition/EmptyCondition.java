package dev.toma.questing.common.condition;

import com.mojang.serialization.Codec;
import dev.toma.questing.common.init.QuestingRegistries;
import dev.toma.questing.common.quest.Quest;
import dev.toma.questing.common.trigger.TriggerResponse;
import net.minecraft.world.World;

public class EmptyCondition extends ConditionProvider<EmptyCondition.Instance> {

    public static final EmptyCondition EMPTY_PROVIDER = new EmptyCondition();
    public static final Condition EMPTY = new Instance(EMPTY_PROVIDER);
    public static final Codec<EmptyCondition> CODEC = Codec.unit(EMPTY_PROVIDER);

    public EmptyCondition() {
        super(TriggerResponse.SKIP);
    }

    public static boolean isEmpty(Condition condition) {
        return condition == EMPTY;
    }

    @Override
    public ConditionType<?> getType() {
        return QuestingRegistries.EMPTY_CONDITION;
    }

    @Override
    public Instance createConditionInstance(World world, Quest quest) {
        return (Instance) EMPTY;
    }

    static final class Instance extends Condition {

        private Instance(EmptyCondition provider) {
            super(provider);
        }

        @Override
        public void registerTriggerResponders(ConditionRegisterHandler registerHandler) {

        }
    }
}
