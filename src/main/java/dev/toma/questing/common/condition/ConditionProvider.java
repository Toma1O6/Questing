package dev.toma.questing.common.condition;

import dev.toma.questing.common.quest.Quest;
import dev.toma.questing.common.trigger.ResponseType;
import net.minecraft.world.World;

public abstract class ConditionProvider<C extends Condition> {

    private final ResponseType defaultFailureResponse;

    public ConditionProvider(ResponseType defaultFailureResponse) {
        this.defaultFailureResponse = defaultFailureResponse;
    }

    public abstract ConditionType<?> getType();

    public abstract C createConditionInstance(World world, Quest quest);

    public final ResponseType getDefaultFailureResponse() {
        return this.defaultFailureResponse;
    }
}
