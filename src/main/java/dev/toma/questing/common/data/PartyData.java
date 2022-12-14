package dev.toma.questing.common.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.toma.questing.common.party.Party;
import dev.toma.questing.common.party.PartyInvite;
import dev.toma.questing.utils.Codecs;
import net.minecraft.util.Util;

import java.util.*;

public interface PartyData {

    void setActiveParty(Party party);

    UUID getPartyId();

    Set<PartyInvite> getMyInvites();

    void addInvite(PartyInvite invite);

    void removeInvite(PartyInvite invite);

    class Impl implements PartyData, Encodeable<Impl> {

        static final Codec<Impl> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codecs.UUID_STRING.fieldOf("partyId").forGetter(Impl::getPartyId),
                PartyInvite.CODEC.listOf().xmap(LinkedHashSet::new, ArrayList::new).fieldOf("invites").forGetter(t -> t.invites)
        ).apply(instance, Impl::new));
        private UUID partyId;
        private final LinkedHashSet<PartyInvite> invites = new LinkedHashSet<>();

        public Impl() {
            this(Util.NIL_UUID, Collections.emptySet());
        }

        private Impl(UUID partyId, Set<PartyInvite> invites) {
            this.partyId = partyId;
            this.invites.addAll(invites);
        }

        @Override
        public Codec<Impl> codec() {
            return CODEC;
        }

        @Override
        public void resolve(Impl partyData) {
            this.partyId = partyData.partyId;
            this.invites.clear();
            this.invites.addAll(partyData.invites);
        }

        @Override
        public void setActiveParty(Party party) {
            this.partyId = party.getOwner();
        }

        @Override
        public UUID getPartyId() {
            return partyId;
        }

        @Override
        public void addInvite(PartyInvite invite) {
            this.invites.add(invite);
        }

        @Override
        public void removeInvite(PartyInvite invite) {
            this.invites.remove(invite);
        }

        @Override
        public Set<PartyInvite> getMyInvites() {
            return invites;
        }
    }
}
