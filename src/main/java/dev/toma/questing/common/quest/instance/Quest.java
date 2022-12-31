package dev.toma.questing.common.quest.instance;

import dev.toma.questing.common.component.trigger.Trigger;
import dev.toma.questing.common.party.Party;
import dev.toma.questing.common.quest.ProgressStatus;
import dev.toma.questing.common.quest.provider.QuestProvider;
import net.minecraft.world.World;

public interface Quest {

    void onGenerated(Party party, World level);

    void onAssigned(Party party, World level);

    <T, E> void trigger(Trigger<T> trigger, T triggerData, World level);

    ProgressStatus getStatus();

    void setStatus(ProgressStatus status);

    Party getParty();

    QuestProvider<?> getProvider();
}
