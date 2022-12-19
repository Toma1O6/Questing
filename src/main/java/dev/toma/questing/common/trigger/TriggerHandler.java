package dev.toma.questing.common.trigger;

@FunctionalInterface
public interface TriggerHandler<T extends TriggerData> {

    void handleSuccessfullTrigger(Trigger<T> trigger, T data);

    static <T extends TriggerData> TriggerHandler<T> doNothing() {
        return (trigger, data) -> {};
    }
}