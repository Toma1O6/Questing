package dev.toma.questing.client.render;

public enum NotificationFlowType {

    // right to left
    LEFT((size, progress) -> size - (size * progress), (size, progress) -> 0),

    // top to down
    DOWN((size, progress) -> 0, (size, progress) -> -size + size * progress),

    // bottom to up
    UP((size, progress) -> 0, (size, progress) -> size - size * progress),

    // left to right
    RIGHT((size, progress) -> -size + size * progress, (size, progress) -> 0);

    private final FlowPositionProvider xPosProvider;
    private final FlowPositionProvider yPosProvider;

    NotificationFlowType(FlowPositionProvider xPosProvider, FlowPositionProvider yPosProvider) {
        this.xPosProvider = xPosProvider;
        this.yPosProvider = yPosProvider;
    }

    public float getOffsetPositionX(int notificationSize, float progress) {
        return this.xPosProvider.getPosition(notificationSize, progress);
    }

    public float getOffsetPositionY(int notificationSize, float progress) {
        return this.yPosProvider.getPosition(notificationSize, progress);
    }

    @FunctionalInterface
    private interface FlowPositionProvider {

        float getPosition(int notificationSize, float progress);
    }
}
