package dev.toma.questing.network.packet.s2c;

import com.mojang.serialization.DataResult;
import dev.toma.questing.Questing;
import dev.toma.questing.client.screen.SynchronizeListener;
import dev.toma.questing.common.party.Party;
import dev.toma.questing.common.party.PartyManager;
import dev.toma.questing.network.packet.AbstractPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.Optional;

public class S2C_SynchronizePartyData extends AbstractPacket<S2C_SynchronizePartyData> {

    private final Party party;

    public S2C_SynchronizePartyData(Party party) {
        this.party = party;
    }

    public S2C_SynchronizePartyData(PacketBuffer buffer) {
        CompoundNBT nbt = buffer.readNbt();
        DataResult<Party> result = Party.CODEC.parse(NBTDynamicOps.INSTANCE, nbt);
        Optional<Party> optional = result.result();
        this.party = optional.orElse(null);
    }

    @Override
    public void encode(PacketBuffer buffer) {
        DataResult<INBT> dr = Party.CODEC.encodeStart(NBTDynamicOps.INSTANCE, party);
        Optional<INBT> optional = dr.result();
        CompoundNBT nbt = optional.filter(inbt -> inbt instanceof CompoundNBT).map(inbt -> (CompoundNBT) inbt).orElse(new CompoundNBT());
        buffer.writeNbt(nbt);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void handle(NetworkEvent.Context context) {
        if (party != null) {
            PartyManager manager = Questing.PARTY_MANAGER.get();
            manager.set(party);
            Screen screen = Minecraft.getInstance().screen;
            if (screen instanceof SynchronizeListener) {
                ((SynchronizeListener) screen).onPartyUpdated(party);
            }
        }
    }
}
