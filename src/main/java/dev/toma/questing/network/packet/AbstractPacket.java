package dev.toma.questing.network.packet;

import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public abstract class AbstractPacket<T> implements Packet<T> {

    public abstract void handle(NetworkEvent.Context context);

    @Override
    public final void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> handle(context));
        context.setPacketHandled(true);
    }
}
