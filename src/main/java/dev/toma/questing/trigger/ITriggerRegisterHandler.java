package dev.toma.questing.trigger;

@FunctionalInterface
public interface ITriggerRegisterHandler {

    <T> void registerTrigger(Trigger<T> trigger, ITriggerResponder<T> responder, ITriggerHandler<T> handler);

    default <T> void registerTrigger(Trigger<T> trigger, ITriggerResponder<T> responder) {
        registerTrigger(trigger, responder, ITriggerHandler.doNothing());
    }
}
