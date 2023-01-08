package dev.toma.questing.config;

import dev.toma.configuration.config.Config;
import dev.toma.configuration.config.Configurable;
import dev.toma.questing.Questing;

@Config(id = Questing.MODID)
public final class QuestingConfig {

    @Configurable
    @Configurable.Comment("Allows you to block notifications")
    public boolean receiveNotifications = true;

    @Configurable
    @Configurable.Synchronized
    @Configurable.Comment("Max party size for quests")
    @Configurable.Range(min = 1)
    public int maxPartySize = 5;

    @Configurable
    @Configurable.Comment("Duration for how long players can be out of quest area without failing the quest")
    @Configurable.Range(min = 0, max = 1199)
    public int areaGracePeriodDuration = 200;
}
