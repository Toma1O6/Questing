package dev.toma.questing.quest;

import dev.toma.questing.party.QuestParty;
import dev.toma.questing.trigger.Trigger;
import dev.toma.questing.trigger.TriggerHandler;
import dev.toma.questing.trigger.TriggerRegisterHandler;
import dev.toma.questing.trigger.TriggerResponder;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public abstract class Quest {

    private final TriggerContainer triggerStore = new TriggerContainer();
    private QuestParty party;
    private QuestStatus status = QuestStatus.NEW;

    public Quest() {
        this.registerTriggers(triggerStore::register);
    }

    public abstract void registerTriggers(TriggerRegisterHandler registerHandler);

    public QuestParty getParty() {
        return party;
    }

    private static final class TriggerContainer {

        private final Map<Trigger<?>, TriggerData<?>> map = new HashMap<>();

        @Nullable
        @SuppressWarnings("unchecked")
        <T> TriggerData<T> getData(Trigger<T> trigger) {
            return (TriggerData<T>) map.get(trigger);
        }

        <T> void register(Trigger<T> trigger, TriggerResponder<T> responder, TriggerHandler<T> handler) {
            this.map.put(trigger, new TriggerData<>(responder, handler));
        }
    }

    private static final class TriggerData<T> {

        private final TriggerResponder<T> responder;
        private final TriggerHandler<T> handler;

        private TriggerData(TriggerResponder<T> responder, TriggerHandler<T> handler) {
            this.responder = responder;
            this.handler = handler;
        }
    }
}
