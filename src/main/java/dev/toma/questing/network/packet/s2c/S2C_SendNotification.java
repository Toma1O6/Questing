package dev.toma.questing.network.packet.s2c;

import dev.toma.questing.common.notification.Notification;
import dev.toma.questing.common.notification.NotificationIcon;
import dev.toma.questing.common.notification.NotificationsHelper;
import dev.toma.questing.network.packet.AbstractPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkEvent;

public class S2C_SendNotification extends AbstractPacket<S2C_SendNotification> {

    private final Notification notification;

    public S2C_SendNotification(Notification notification) {
        this.notification = notification;
    }

    public S2C_SendNotification(PacketBuffer buffer) {
        NotificationIcon<?> icon = NotificationIcon.decodeIcon(buffer);
        ITextComponent header = buffer.readComponent();
        int size = buffer.readInt();
        ITextComponent[] contents = new ITextComponent[size];
        for (int i = 0; i < size; i++) {
            contents[i] = buffer.readComponent();
        }
        int renderTimer = buffer.readInt();
        int appearTimer = buffer.readInt();
        this.notification = new Notification(icon, header, contents, renderTimer, appearTimer);
    }

    @Override
    public void encode(PacketBuffer buffer) {
        notification.getIcon().encodeIcon(buffer);
        buffer.writeComponent(notification.getHeader());
        buffer.writeInt(notification.getAllContents().length);
        for (ITextComponent component : notification.getAllContents()) {
            buffer.writeComponent(component);
        }
        buffer.writeInt(notification.getRenderTimer());
        buffer.writeInt(notification.getAppearTimer());
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void handle(NetworkEvent.Context context) {
        NotificationsHelper.displayClientNotification(notification);
    }
}
