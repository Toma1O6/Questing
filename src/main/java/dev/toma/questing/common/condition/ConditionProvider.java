package dev.toma.questing.common.condition;

import dev.toma.questing.common.quest.Quest;
import dev.toma.questing.common.trigger.TriggerResponse;
import net.minecraft.world.World;

public abstract class ConditionProvider<C extends Condition> {

    private final TriggerResponse defaultFailureResponse;

    public ConditionProvider(TriggerResponse defaultFailureResponse) {
        this.defaultFailureResponse = defaultFailureResponse;
    }

    public abstract ConditionType<?> getType();

    public abstract C createConditionInstance(World world, Quest quest);

    public TriggerResponse getDefaultFailureResponse() {
        return this.defaultFailureResponse;
    }
}
