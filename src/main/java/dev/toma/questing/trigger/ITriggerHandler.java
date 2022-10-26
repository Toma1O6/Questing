package dev.toma.questing.trigger;

@FunctionalInterface
public interface ITriggerHandler<T> {

    void handleSuccessfullTrigger(Trigger<T> trigger, T data);

    static <T> ITriggerHandler<T> doNothing() {
        return (trigger, data) -> {};
    }
}
