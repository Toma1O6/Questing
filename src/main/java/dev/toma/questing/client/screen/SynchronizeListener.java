package dev.toma.questing.client.screen;

import dev.toma.questing.common.data.PlayerData;
import dev.toma.questing.common.party.Party;
import net.minecraft.entity.player.PlayerEntity;

public interface SynchronizeListener {

    default void onPlayerDataUpdated(PlayerEntity player, PlayerData data) {}

    default void onPartyUpdated(Party party) {}
}
