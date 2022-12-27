package dev.toma.questing.client.render;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;

public enum NotificationDockType {

    LEFT_UPPER((a, size, ntSize) -> a, (a, size, ntSize) -> a, NotificationFlowType.RIGHT, NotificationFlowType.DOWN),
    LEFT_LOWER((a, size, ntSize) -> a, (a, size, ntSize) -> a + size - ntSize, NotificationFlowType.RIGHT, NotificationFlowType.UP),
    RIGHT_UPPER((a, size, ntSize) -> a + size - ntSize, (a, size, ntSize) -> a, NotificationFlowType.LEFT, NotificationFlowType.DOWN),
    RIGHT_LOWER((a, size, ntSize) -> a + size - ntSize, (a, size, ntSize) -> a + size - ntSize, NotificationFlowType.LEFT, NotificationFlowType.UP);

    private final DockFunction dockX;
    private final DockFunction dockY;
    private final Set<NotificationFlowType> allowedFlows;

    NotificationDockType(DockFunction dockX, DockFunction dockY, NotificationFlowType... flowTypes) {
        this.dockX = dockX;
        this.dockY = dockY;
        this.allowedFlows = EnumSet.copyOf(Arrays.asList(flowTypes));
    }

    public boolean isFlowTypeAllowed(NotificationFlowType flowType) {
        return this.allowedFlows.contains(flowType);
    }

    public float getStartingPositionX(float left, int width, float notificationWidth) {
        return dockX.getDockingPosition(left, width, notificationWidth);
    }

    public float getStartingPositionY(float top, int height, float notificationHeight) {
        return dockY.getDockingPosition(top, height, notificationHeight);
    }

    @FunctionalInterface
    private interface DockFunction {

        float getDockingPosition(float a, int size, float objSize);
    }
}
