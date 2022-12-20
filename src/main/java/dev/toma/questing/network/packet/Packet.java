package dev.toma.questing.network.packet;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public interface Packet<T> {

    void encode(PacketBuffer buffer);

    void handle(Supplier<NetworkEvent.Context> supplier);
}
