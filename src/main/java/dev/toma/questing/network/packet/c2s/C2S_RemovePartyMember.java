package dev.toma.questing.network.packet.c2s;

import dev.toma.questing.Questing;
import dev.toma.questing.common.data.PartyData;
import dev.toma.questing.common.data.PlayerDataProvider;
import dev.toma.questing.common.data.PlayerDataSynchronizationFlags;
import dev.toma.questing.common.party.Party;
import dev.toma.questing.common.party.PartyManager;
import dev.toma.questing.common.party.PartyPermission;
import dev.toma.questing.network.packet.AbstractPacket;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public class C2S_RemovePartyMember extends AbstractPacket<C2S_RemovePartyMember> {

    private final UUID removeMemberId;

    public C2S_RemovePartyMember(UUID memberToRemove) {
        this.removeMemberId = memberToRemove;
    }

    public C2S_RemovePartyMember(PacketBuffer buffer) {
        this(buffer.readUUID());
    }

    @Override
    public void encode(PacketBuffer buffer) {
        buffer.writeUUID(removeMemberId);
    }

    @Override
    public void handle(NetworkEvent.Context context) {
        ServerPlayerEntity player = context.getSender();
        PlayerDataProvider.getOptional(player).ifPresent(data -> {
            PartyData partyData = data.getPartyData();
            PartyManager manager = Questing.PARTY_MANAGER.get();
            manager.getPartyById(partyData.getPartyId()).ifPresent(party -> {
                Set<UUID> members = party.getMembers();
                if (!members.contains(removeMemberId))
                    return;
                boolean isTargetAnAdmin = party.isAuthorized(PartyPermission.MANAGE_MEMBERS, removeMemberId);
                if (isTargetAnAdmin && !party.isAuthorized(PartyPermission.OWNER, player.getUUID()))
                    return;
                party.executeWithAuthorization(PartyPermission.MANAGE_MEMBERS, player.getUUID(), () -> {
                    party.removeMember(player, removeMemberId);
                    manager.sendClientData(player.level, party);
                    PlayerEntity removedPlayer = player.level.getPlayerByUUID(removeMemberId);
                    if (removedPlayer != null) {
                        manager.assignDefaultParty(removedPlayer);
                        PlayerDataProvider.getOptional(removedPlayer).ifPresent(playerData -> {
                            Optional<Party> optional = manager.getPartyById(playerData.getPartyData().getPartyId());
                            optional.ifPresent(newParty -> manager.sendClientData(removedPlayer.level, newParty));
                            playerData.sendDataToClient(PlayerDataSynchronizationFlags.PARTY);
                        });
                    }
                });
            });
        });
    }
}
