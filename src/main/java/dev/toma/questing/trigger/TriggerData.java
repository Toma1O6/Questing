package dev.toma.questing.trigger;

import dev.toma.questing.quest.Quest;
import net.minecraft.entity.player.PlayerEntity;

public interface TriggerData {

    Quest getQuest();

    PlayerEntity getPlayer();
}
