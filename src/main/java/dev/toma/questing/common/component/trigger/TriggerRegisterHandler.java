package dev.toma.questing.common.component.trigger;

@FunctionalInterface
public interface TriggerRegisterHandler {

    <T> void registerTrigger(Trigger<T> trigger, TriggerResponder<T> responder, TriggerHandler<T> handler);

    default <T> void registerTrigger(Trigger<T> trigger, TriggerResponder<T> responder) {
        registerTrigger(trigger, responder, TriggerHandler.doNothing());
    }
}
