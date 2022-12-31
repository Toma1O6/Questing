package dev.toma.questing.common.quest.provider;

import dev.toma.questing.common.component.area.provider.AreaProvider;
import dev.toma.questing.common.quest.instance.AreaQuest;

public interface AreaQuestProvider<Q extends AreaQuest> extends QuestProvider<Q> {

    AreaProvider<?> getAreaProvider();
}
