package dev.toma.questing.common.quest;

public enum ProgressStatus {

    CREATED(false),
    GENERATED(false),
    ACTIVE(false),
    COMPLETED(true),
    FAILED(true),
    LOCKED(true);

    private final boolean isFinalStatus;

    ProgressStatus(boolean isFinalStatus) {
        this.isFinalStatus = isFinalStatus;
    }

    public boolean isFinalStatus() {
        return isFinalStatus;
    }
}
