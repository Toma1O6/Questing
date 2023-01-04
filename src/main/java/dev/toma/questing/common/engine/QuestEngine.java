package dev.toma.questing.common.engine;

import dev.toma.questing.common.component.reward.instance.Reward;
import dev.toma.questing.common.party.Party;
import dev.toma.questing.common.quest.instance.Quest;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface QuestEngine {

    boolean canStartQuest(Party party, Quest quest, World level);

    boolean shouldShowInQuestUI();

    boolean canClaimRewardsViaUI();

    List<Quest> generateQuests(Party party, World level);

    ResourceLocation getIndentifier();

    Map<UUID, List<Quest>> getQuestsByParty();

    Map<UUID, List<Reward>> getClaimableRewards();

    void storeRewards(Map<UUID, Reward> map);

    void onLoaded();
}
