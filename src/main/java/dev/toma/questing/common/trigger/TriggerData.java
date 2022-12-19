package dev.toma.questing.common.trigger;

import dev.toma.questing.common.quest.Quest;
import net.minecraft.entity.player.PlayerEntity;

public interface TriggerData {

    Quest getQuest();

    PlayerEntity getPlayer();
}
