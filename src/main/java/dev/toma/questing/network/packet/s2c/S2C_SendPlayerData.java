package dev.toma.questing.network.packet.s2c;

import dev.toma.questing.Questing;
import dev.toma.questing.client.screen.SynchronizeListener;
import dev.toma.questing.common.data.PlayerData;
import dev.toma.questing.common.data.PlayerDataProvider;
import dev.toma.questing.network.Networking;
import dev.toma.questing.network.packet.AbstractPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.UUID;

public class S2C_SendPlayerData extends AbstractPacket<S2C_SendPlayerData> {

    private final UUID playerId;
    private final CompoundNBT data;
    private final int values;

    public S2C_SendPlayerData(UUID playerId, CompoundNBT data, int values) {
        this.playerId = playerId;
        this.data = data;
        this.values = values;
    }

    public S2C_SendPlayerData(PacketBuffer buffer) {
        this(buffer.readUUID(), buffer.readNbt(), buffer.readInt());
    }

    @Override
    public void encode(PacketBuffer buffer) {
        buffer.writeUUID(playerId);
        buffer.writeNbt(data);
        buffer.writeInt(values);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void handle(NetworkEvent.Context context) {
        Minecraft client = Minecraft.getInstance();
        PlayerEntity player = client.level.getPlayerByUUID(playerId);
        if (player == null) {
            Questing.LOGGER.error(Networking.MARKER, "Unable to resolve player data for {} player, no such player is present", playerId);
            return;
        }
        PlayerData playerData = PlayerDataProvider.getUnsafe(player);
        if (playerData == null) {
            Questing.LOGGER.error(Networking.MARKER, "Unable to resolve player data for {} player, no capability data are attached", player);
            return;
        }
        playerData.receiveClientData(values, data);
        Screen screen = client.screen;
        if (screen instanceof SynchronizeListener) {
            ((SynchronizeListener) screen).onPlayerDataUpdated(player, playerData);
        }
    }
}
