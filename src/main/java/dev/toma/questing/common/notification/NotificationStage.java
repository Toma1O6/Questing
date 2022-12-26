package dev.toma.questing.common.notification;

public enum NotificationStage {

    READY,
    APPEAR(true),
    DISPLAY,
    DISAPEAR(true),
    COMPLETED;

    private final boolean isAnimationStage;

    NotificationStage() {
        this(false);
    }

    NotificationStage(boolean isAnimationStage) {
        this.isAnimationStage = isAnimationStage;
    }

    public boolean isAnimationStage() {
        return isAnimationStage;
    }
}
