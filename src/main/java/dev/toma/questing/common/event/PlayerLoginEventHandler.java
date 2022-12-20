package dev.toma.questing.common.event;

import dev.toma.questing.Questing;
import dev.toma.questing.common.data.PlayerDataProvider;
import dev.toma.questing.common.data.PlayerDataSynchronizationFlags;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.event.entity.player.PlayerEvent;

public final class PlayerLoginEventHandler {

    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        ServerPlayerEntity player = (ServerPlayerEntity) event.getPlayer();
        Questing.PARTY_MANAGER.get().onPlayerLoaded(player);

        PlayerDataProvider.getOptional(player).ifPresent(playerData -> playerData.sendDataToClient(PlayerDataSynchronizationFlags.WILDCARD));
    }
}
