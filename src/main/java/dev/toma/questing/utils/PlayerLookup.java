package dev.toma.questing.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.network.play.NetworkPlayerInfo;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerList;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.UUID;

public final class PlayerLookup {

    public static ServerPlayerEntity findServerPlayer(ServerWorld serverLevel, UUID playerId) {
        MinecraftServer server = serverLevel.getServer();
        PlayerList playerList = server.getPlayerList();
        return playerList.getPlayer(playerId);
    }

    @OnlyIn(Dist.CLIENT)
    public static NetworkPlayerInfo findClientPlayerInfo(UUID playerId) {
        Minecraft client = Minecraft.getInstance();
        return client.getConnection().getPlayerInfo(playerId);
    }
}
