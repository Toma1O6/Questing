package dev.toma.questing.client.render;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;

public enum NotificationDockType {

    LEFT_UPPER((a, size) -> a, (a, size) -> a, NotificationFlowType.RIGHT, NotificationFlowType.DOWN),
    LEFT_LOWER((a, size) -> a, Float::sum, NotificationFlowType.RIGHT, NotificationFlowType.UP),
    RIGHT_UPPER(Float::sum, (a, size) -> a, NotificationFlowType.LEFT, NotificationFlowType.DOWN),
    RIGHT_LOWER(Float::sum, Float::sum, NotificationFlowType.LEFT, NotificationFlowType.UP);

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

    public float getStartingPositionX(float left, int width) {
        return dockX.getDockingPosition(left, width);
    }

    public float getStartingPositionY(float top, int height) {
        return dockY.getDockingPosition(top, height);
    }

    @FunctionalInterface
    private interface DockFunction {

        float getDockingPosition(float a, int size);
    }
}
