package dev.toma.questing.common.notification;

import dev.toma.questing.Questing;
import dev.toma.questing.client.QuestingClient;
import dev.toma.questing.network.Networking;
import dev.toma.questing.network.packet.s2c.S2C_SendNotification;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Objects;

public final class NotificationsHelper {

    public static void sendNotification(PlayerEntity player, Notification notification) {
        Questing.LOGGER.debug(Notification.MARKER, "Sending notification {} to {}", notification, player);
        if (player.level.isClientSide) {
            displayClientNotification(notification);
        } else {
            ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity) player;
            Networking.toClient(serverPlayerEntity, new S2C_SendNotification(notification));
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static void displayClientNotification(Notification notification) {
        NotificationManager manager = QuestingClient.CLIENT.notificationManager;
        manager.enqueue(Objects.requireNonNull(notification));
    }
}
