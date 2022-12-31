package dev.toma.questing.common.component.condition.instance;

import dev.toma.questing.common.component.condition.ConditionRegisterHandler;
import dev.toma.questing.common.component.condition.provider.ConditionProvider;
import dev.toma.questing.common.party.Party;
import dev.toma.questing.common.quest.Quest;
import net.minecraft.world.World;

public interface Condition {

    ConditionProvider<?> getProvider();

    void registerTriggerResponders(ConditionRegisterHandler registerHandler);

    default void onConditionConstructing(Party party, Quest quest, World world) {}
}
