package dev.toma.questing.common.party;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.toma.questing.Questing;
import dev.toma.questing.common.data.PlayerData;
import dev.toma.questing.common.data.PlayerDataProvider;
import dev.toma.questing.utils.Codecs;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.Util;
import net.minecraft.world.World;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public final class PartyInvite {

    public static final Codec<PartyInvite> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codecs.UUID_STRING.fieldOf("party").forGetter(inv -> inv.partyId),
            Codecs.UUID_STRING.fieldOf("invited").forGetter(inv -> inv.inviteeId),
            Codecs.UUID_STRING.fieldOf("sentBy").forGetter(inv -> inv.inviteSentById),
            Codec.STRING.fieldOf("partyName").forGetter(inv -> inv.partyName),
            Codec.STRING.fieldOf("senderName").forGetter(inv -> inv.senderName),
            Codec.STRING.fieldOf("invitedName").forGetter(inv -> inv.invitedName)
    ).apply(instance, PartyInvite::new));
    private final UUID partyId;
    private final UUID inviteeId;
    private final UUID inviteSentById;
    private final String partyName;
    private final String senderName;
    private final String invitedName;
    private InviteResponseEvent onAccept;
    private InviteResponseEvent onDecline;

    private PartyInvite(UUID partyId, UUID inviteeId, UUID inviteSentById, String partyName, String senderName, String invitedName) {
        this.partyId = partyId;
        this.inviteeId = inviteeId;
        this.inviteSentById = inviteSentById;
        this.partyName = partyName;
        this.senderName = senderName;
        this.invitedName = invitedName;
        this.onAccept = (party, player) -> {};
        this.onDecline = (party, player) -> {};
    }

    public static PartyInvite dummy(UUID partyId, ServerPlayerEntity player) {
        return new PartyInvite(partyId, player.getUUID(), Util.NIL_UUID, "", "", "");
    }

    public static PartyInvite createInvite(PlayerEntity toInvite, PlayerEntity inviteSource, UUID partyId, String partyName) {
        return new PartyInvite(partyId, toInvite.getUUID(), inviteSource.getUUID(), partyName, inviteSource.getName().getString(), toInvite.getName().getString());
    }

    public static PartyInvite createInvite(PlayerEntity toInvite, PlayerEntity inviteSource) {
        PlayerData data = PlayerDataProvider.getUnsafe(inviteSource);
        UUID partyId = data.getPartyData().getPartyId();
        Optional<Party> partyOptional = Questing.PARTY_MANAGER.get().getPartyById(partyId);
        String partyName = "party";
        String senderName = inviteSource.getName().getString();
        if (partyOptional.isPresent()) {
            partyName = partyOptional.get().getName();
        }
        return new PartyInvite(partyId, toInvite.getUUID(), inviteSource.getUUID(), partyName, senderName, toInvite.getName().getString());
    }

    public void setResponseHandlers(InviteResponseEvent onAccept, InviteResponseEvent onDecline) {
        this.onAccept = Objects.requireNonNull(onAccept);
        this.onDecline = Objects.requireNonNull(onDecline);
    }

    public void acceptInvite(World world) {
        this.getInvitee(world).ifPresent(player -> this.onAccept.onEvent(this, player));
    }

    public void declineInvite(World world) {
        this.getInvitee(world).ifPresent(player -> this.onDecline.onEvent(this, player));
    }

    public UUID getPartyId() {
        return partyId;
    }

    public UUID getInviteeId() {
        return inviteeId;
    }

    public UUID getInviteSentById() {
        return inviteSentById;
    }

    public String getSenderName() {
        return senderName;
    }

    public String getPartyName() {
        return partyName;
    }

    public String getInvitedName() {
        return invitedName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PartyInvite that = (PartyInvite) o;
        return partyId.equals(that.partyId) && inviteeId.equals(that.inviteeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(partyId, inviteeId);
    }

    private Optional<PlayerEntity> getInvitee(World world) {
        PlayerEntity player = world.getPlayerByUUID(this.inviteeId);
        if (player == null) {
            Questing.LOGGER.error(Party.MARKER, "Unable to process invite event, invited player is null and thus couldn't complete the invite. Party {}, Invitee {}", partyId, inviteeId);
            return Optional.empty();
        }
        return Optional.of(player);
    }

    @FunctionalInterface
    public interface InviteResponseEvent {
        void onEvent(PartyInvite invite, PlayerEntity invitee);
    }
}
