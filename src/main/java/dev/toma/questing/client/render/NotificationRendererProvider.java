package dev.toma.questing.client.render;

import dev.toma.questing.common.notification.Notification;
import dev.toma.questing.common.notification.NotificationManager;
import net.minecraft.client.gui.FontRenderer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class NotificationRendererProvider implements NotificationManager.NotificationListener {

    public static final long RENDERER_TIMEOUT = 100L; // render should never take longer than 2 ticks

    private final List<NotificationRenderer> renderers = new ArrayList<>();
    private Notification notification;

    public void tick() {
        Iterator<NotificationRenderer> iterator = renderers.iterator();
        long ms = System.currentTimeMillis();
        while (iterator.hasNext()) {
            NotificationRenderer renderer = iterator.next();
            long lastRenderCall = renderer.getRenderTime();
            if (ms - lastRenderCall > RENDERER_TIMEOUT) {
                iterator.remove();
            }
        }
    }

    public NotificationRenderer getRendererWithConfiguration(NotificationDockType dockType, NotificationFlowType flowType, int windowX, int windowY, int windowWidth, int windowHeight, int width, int height, FontRenderer font) {
        if (!dockType.isFlowTypeAllowed(flowType)) {
            throw new IllegalArgumentException(String.format("Notification dock type of %s cannot support %s flow type!", dockType, flowType));
        }
        NotificationRenderer renderer = new NotificationRenderer(font, dockType, flowType, windowX, windowY, windowWidth, windowHeight, width, height);
        renderer.setActiveNotification(this.notification);
        this.renderers.add(renderer);
        return renderer;
    }

    @Override
    public void notificationActivated(Notification notification) {
        this.notification = notification;
        this.renderers.forEach(renderer -> renderer.setActiveNotification(notification));
    }

    @Override
    public void notificationExpired(Notification notification) {
        this.notification = null;
        this.renderers.forEach(renderer -> renderer.setActiveNotification(null));
    }
}
