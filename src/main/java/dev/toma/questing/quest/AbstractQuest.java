package dev.toma.questing.quest;

import dev.toma.questing.trigger.ITriggerHandler;
import dev.toma.questing.trigger.ITriggerRegisterHandler;
import dev.toma.questing.trigger.ITriggerResponder;
import dev.toma.questing.trigger.Trigger;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractQuest {

    private final TriggerContainer triggerStore = new TriggerContainer();
    private QuestStatus status = QuestStatus.NEW;

    public AbstractQuest() {
        this.registerTriggers(triggerStore::register);
    }

    public abstract void registerTriggers(ITriggerRegisterHandler registerHandler);

    private static final class TriggerContainer {

        private final Map<Trigger<?>, TriggerData<?>> map = new HashMap<>();

        @Nullable
        @SuppressWarnings("unchecked")
        <T> TriggerData<T> getData(Trigger<T> trigger) {
            return (TriggerData<T>) map.get(trigger);
        }

        <T> void register(Trigger<T> trigger, ITriggerResponder<T> responder, ITriggerHandler<T> handler) {
            this.map.put(trigger, new TriggerData<>(responder, handler));
        }
    }

    private static final class TriggerData<T> {

        private final ITriggerResponder<T> responder;
        private final ITriggerHandler<T> handler;

        private TriggerData(ITriggerResponder<T> responder, ITriggerHandler<T> handler) {
            this.responder = responder;
            this.handler = handler;
        }
    }
}
