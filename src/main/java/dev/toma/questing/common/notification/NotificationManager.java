package dev.toma.questing.common.notification;

import com.google.common.collect.Queues;
import dev.toma.questing.Questing;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public final class NotificationManager {

    private final Queue<Notification> notifications = Queues.newArrayDeque();
    private final List<NotificationListener> listeners = new ArrayList<>();
    private Notification activeNotification;

    public void enqueue(Notification notification) {
        if (!this.notifications.offer(notification)) {
            Questing.LOGGER.error(Notification.MARKER, "Failed to enqueue notification: {}", notification);
            return;
        }
        notification.setStage(NotificationStage.APPEAR);
        this.listeners.forEach(listener -> listener.notificationEnqueued(notification));
    }

    public void forceEnqueueNextNotification() {
        if (activeNotification != null) {
            this.listeners.forEach(listener -> listener.notificationExpired(this.activeNotification));
            this.activeNotification = null;
            this.enqueueNewNotificationIfPossible();
        }
    }

    public void tick() {
        this.enqueueNewNotificationIfPossible();
        if (this.activeNotification != null) {
            this.activeNotification.tickNotification();
            if (this.activeNotification.isForRemoval()) {
                this.listeners.forEach(listener -> listener.notificationExpired(this.activeNotification));
                this.activeNotification = null;
                this.enqueueNewNotificationIfPossible();
            }
        }
    }

    private void enqueueNewNotificationIfPossible() {
        if (activeNotification == null && notifications.size() > 0) {
            activeNotification = notifications.poll();
            if (activeNotification != null) {
                this.listeners.forEach(listener -> listener.notificationActivated(activeNotification));
            }
        }
    }

    public interface NotificationListener {

        default void notificationEnqueued(Notification notification) {}

        default void notificationActivated(Notification notification) {}

        default void notificationExpired(Notification notification) {}
    }
}
