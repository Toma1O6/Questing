package dev.toma.questing.trigger;

@FunctionalInterface
public interface TriggerRegisterHandler {

    <T extends TriggerData> void registerTrigger(Trigger<T> trigger, TriggerResponder<T> responder, TriggerHandler<T> handler);

    default <T extends TriggerData> void registerTrigger(Trigger<T> trigger, TriggerResponder<T> responder) {
        registerTrigger(trigger, responder, TriggerHandler.doNothing());
    }
}
