package dev.toma.questing.common.condition;

import dev.toma.questing.common.quest.Quest;
import net.minecraft.world.World;

public interface ConditionProvider<C extends Condition> {

    ConditionType<?> getType();

    C createConditionInstance(World world, Quest quest);
}
