package dev.toma.questing.network.packet;

import net.minecraft.network.PacketBuffer;

public abstract class AbstractActionPacket<T> extends AbstractPacket<T> {

    @Override
    public final void encode(PacketBuffer buffer) {}
}
