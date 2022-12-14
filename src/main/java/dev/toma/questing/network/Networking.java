package dev.toma.questing.network;

import dev.toma.questing.Questing;
import dev.toma.questing.network.packet.Packet;
import dev.toma.questing.network.packet.c2s.*;
import dev.toma.questing.network.packet.s2c.S2C_SendNotification;
import dev.toma.questing.network.packet.s2c.S2C_SendPlayerData;
import dev.toma.questing.network.packet.s2c.S2C_SynchronizePartyData;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.util.function.Function;

public final class Networking {

    public static final Marker MARKER = MarkerManager.getMarker("Network");
    private static final String PROTOCOL_VERSION = "1";
    private static final SimpleChannel CHANNEL = NetworkRegistry.ChannelBuilder.named(new ResourceLocation(Questing.MODID, "network_channel"))
            .networkProtocolVersion(Networking::getProtocolVersion)
            .clientAcceptedVersions(PROTOCOL_VERSION::equals)
            .serverAcceptedVersions(PROTOCOL_VERSION::equals)
            .simpleChannel();

    public static void toClient(ServerPlayerEntity player, Packet<?> packet) {
        CHANNEL.sendTo(packet, player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
    }

    public static void toServer(Packet<?> packet) {
        CHANNEL.sendToServer(packet);
    }

    public static String getProtocolVersion() {
        return PROTOCOL_VERSION;
    }

    public static final class Registry {

        private static int id;

        public static void registerPackets() {
            registerPacket(S2C_SendPlayerData.class, S2C_SendPlayerData::new);
            registerPacket(S2C_SynchronizePartyData.class, S2C_SynchronizePartyData::new);
            registerPacket(S2C_SendNotification.class, S2C_SendNotification::new);

            registerPacket(C2S_RequestInviteCreation.class, C2S_RequestInviteCreation::new);
            registerPacket(C2S_SendInviteResponse.class, C2S_SendInviteResponse::new);
            registerPacket(C2S_LeaveParty.class, pb -> new C2S_LeaveParty());
            registerPacket(C2S_RemovePartyMember.class, C2S_RemovePartyMember::new);
            registerPacket(C2S_UpdateMemberRoles.class, C2S_UpdateMemberRoles::new);
            registerPacket(C2S_RenameParty.class, C2S_RenameParty::new);
            registerPacket(C2S_RequestInviteDelete.class, C2S_RequestInviteDelete::new);
            registerPacket(C2S_CheckMyInvites.class, pb -> new C2S_CheckMyInvites());
        }

        private static <P extends Packet<P>> void registerPacket(Class<P> packetType, Function<PacketBuffer, P> decoder) {
            CHANNEL.registerMessage(id++, packetType, Packet::encode, decoder, Packet::handle);
        }
    }
}
