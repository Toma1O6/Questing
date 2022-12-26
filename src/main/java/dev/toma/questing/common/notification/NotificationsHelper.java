package dev.toma.questing.common.notification;

import dev.toma.questing.client.QuestingClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Objects;

public final class NotificationsHelper {

    public static void sendNotification(PlayerEntity player, Notification notification) {
        if (player.level.isClientSide) {
            displayClientNotification(notification);
        } else {
            // TODO send notification packet
        }
    }

    @OnlyIn(Dist.CLIENT)
    private static void displayClientNotification(Notification notification) {
        NotificationManager manager = QuestingClient.CLIENT.notificationManager;
        manager.enqueue(Objects.requireNonNull(notification));
    }
}
