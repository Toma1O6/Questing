package dev.toma.questing.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import dev.toma.questing.client.QuestingClient;
import dev.toma.questing.common.notification.Notification;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;

public class NotificationRenderer {

    private final FontRenderer font;
    private final NotificationDockType dockType;
    private final NotificationFlowType flowType;
    private final int windowX, windowY, windowWidth, windowHeight;
    private final int width;
    private final int height;
    private Notification notification;
    private long lastRenderTime;

    public NotificationRenderer(FontRenderer font, NotificationDockType dockType, NotificationFlowType flowType, int windowX, int windowY, int windowWidth, int windowHeight, int width, int height) {
        this.font = font;
        this.dockType = dockType;
        this.flowType = flowType;
        this.windowX = windowX;
        this.windowY = windowY;
        this.windowWidth = windowWidth;
        this.windowHeight = windowHeight;
        this.width = width;
        this.height = height;
    }

    public void drawNotification(MatrixStack stack, float partialTicks) {
        this.updateRenderTime();
        if (notification == null)
            return;
        float appearProgress = this.notification.getAppearProgress(partialTicks);
        int notificationX = (int) (this.dockType.getStartingPositionX(this.windowX, this.windowWidth) + this.flowType.getOffsetPositionX(this.width, appearProgress));
        int nofiticationY = (int) (this.dockType.getStartingPositionY(this.windowY, this.windowHeight) + this.flowType.getOffsetPositionY(this.height, appearProgress));
        AbstractGui.fill(stack, notificationX, nofiticationY, notificationX + width, nofiticationY + height, 0xFF777777);
    }

    public void onNotificationClicked() {
        // TODO handle click actions
        QuestingClient.CLIENT.notificationManager.forceEnqueueNextNotification();
    }

    public void setActiveNotification(Notification notification) {
        this.notification = notification;
    }

    public long getRenderTime() {
        return this.lastRenderTime;
    }

    private void updateRenderTime() {
        this.lastRenderTime = System.currentTimeMillis();
    }
}
