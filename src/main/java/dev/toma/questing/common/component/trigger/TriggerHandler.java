package dev.toma.questing.common.component.trigger;

@FunctionalInterface
public interface TriggerHandler<T> {

    void handleSuccessfullTrigger(T data);

    static <T> TriggerHandler<T> doNothing() {
        return data -> {};
    }
}
