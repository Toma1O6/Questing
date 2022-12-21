package dev.toma.questing.common.data;

import com.mojang.serialization.Codec;
import dev.toma.questing.Questing;
import dev.toma.questing.common.party.Party;
import dev.toma.questing.utils.Codecs;
import net.minecraft.util.Util;

import java.util.Optional;
import java.util.UUID;

public interface PartyData {

    void setActiveParty(Party party);

    UUID getPartyId();

    Optional<Party> getPartyInstance();

    class Impl implements PartyData, Encodeable<Impl> {

        static final Codec<Impl> CODEC = Codecs.UUID_STRING.xmap(uuid -> {
            Impl partyData = new Impl();
            partyData.partyId = uuid;
            return partyData;
        }, PartyData::getPartyId).fieldOf("partyId").codec();
        private UUID partyId = Util.NIL_UUID;

        @Override
        public Codec<Impl> codec() {
            return CODEC;
        }

        @Override
        public void resolve(Impl partyData) {
            this.partyId = partyData.partyId;
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
        public Optional<Party> getPartyInstance() {
            return Questing.PARTY_MANAGER.get().getPartyById(partyId);
        }
    }
}
