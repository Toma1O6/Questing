package dev.toma.questing.common.condition;

import dev.toma.questing.common.quest.Quest;
import net.minecraft.world.World;

public abstract class ConditionProvider<C extends Condition> {

    private final boolean failsQuest;

    public ConditionProvider(boolean failsQuest) {
        this.failsQuest = failsQuest;
    }

    public abstract ConditionType<?> getType();

    public abstract C createConditionInstance(World world, Quest quest);

    public boolean shouldFailQuest() {
        return this.failsQuest;
    }
}
