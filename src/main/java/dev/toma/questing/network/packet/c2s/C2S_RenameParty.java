package dev.toma.questing.network.packet.c2s;

import dev.toma.questing.Questing;
import dev.toma.questing.common.data.PartyData;
import dev.toma.questing.common.data.PlayerDataProvider;
import dev.toma.questing.common.notification.NotificationFactory;
import dev.toma.questing.common.notification.NotificationsHelper;
import dev.toma.questing.common.party.PartyManager;
import dev.toma.questing.common.party.PartyPermission;
import dev.toma.questing.network.Networking;
import dev.toma.questing.network.packet.AbstractPacket;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.UUID;

public class C2S_RenameParty extends AbstractPacket<C2S_RenameParty> {

    private final String partyName;

    public C2S_RenameParty(String partyName) {
        this.partyName = partyName;
    }

    public C2S_RenameParty(PacketBuffer buffer) {
        this.partyName = buffer.readUtf();
    }

    @Override
    public void encode(PacketBuffer buffer) {
        buffer.writeUtf(partyName);
    }

    @Override
    public void handle(NetworkEvent.Context context) {
        ServerPlayerEntity player = context.getSender();
        PlayerDataProvider.getOptional(player).ifPresent(data -> {
            PartyManager manager = Questing.PARTY_MANAGER.get();
            PartyData partyData = data.getPartyData();
            manager.getPartyById(partyData.getPartyId()).ifPresent(party -> {
                UUID senderId = player.getUUID();
                Questing.LOGGER.debug(Networking.MARKER, "Processing rename request for {} by {}", party, player);
                party.executeWithAuthorization(PartyPermission.MANAGE_PARTY, senderId, () -> {
                    String oldPartyName = party.getName();
                    party.setPartyName(partyName);
                    manager.sendClientData(player.level, party);
                    party.forEachOnlineMemberExcept(null, player.level, partyMember -> NotificationsHelper.sendNotification(partyMember, NotificationFactory.getPartyRenameNotification(oldPartyName, partyName)));
                });
            });
        });
    }
}
