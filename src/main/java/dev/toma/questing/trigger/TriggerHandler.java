package dev.toma.questing.trigger;

@FunctionalInterface
public interface TriggerHandler<T> {

    void handleSuccessfullTrigger(Trigger<T> trigger, T data);

    static <T> TriggerHandler<T> doNothing() {
        return (trigger, data) -> {};
    }
}
