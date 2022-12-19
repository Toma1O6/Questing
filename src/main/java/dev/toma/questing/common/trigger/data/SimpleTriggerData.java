package dev.toma.questing.common.trigger.data;

import dev.toma.questing.common.quest.Quest;
import dev.toma.questing.common.trigger.TriggerData;
import net.minecraft.entity.player.PlayerEntity;

public class SimpleTriggerData implements TriggerData {

    private final Quest quest;
    private final PlayerEntity player;

    protected SimpleTriggerData(Quest quest, PlayerEntity player) {
        this.quest = quest;
        this.player = player;
    }

    public static SimpleTriggerData createSimple(Quest quest, PlayerEntity player) {
        return new SimpleTriggerData(quest, player);
    }

    @Override
    public Quest getQuest() {
        return quest;
    }

    @Override
    public PlayerEntity getPlayer() {
        return player;
    }
}
