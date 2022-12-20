package dev.toma.questing.common.quest;

import dev.toma.questing.common.party.QuestParty;
import dev.toma.questing.common.trigger.*;

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

        private final Map<Trigger<?>, TriggerDataHandler<?>> map = new HashMap<>();

        @Nullable
        @SuppressWarnings("unchecked")
        <T extends TriggerData> TriggerDataHandler<T> getData(Trigger<T> trigger) {
            return (TriggerDataHandler<T>) map.get(trigger);
        }

        <T extends TriggerData> void register(Trigger<T> trigger, TriggerResponder<T> responder, TriggerHandler<T> handler) {
            this.map.put(trigger, new TriggerDataHandler<>(responder, handler));
        }
    }

    private static final class TriggerDataHandler<T extends TriggerData> {

        private final TriggerResponder<T> responder;
        private final TriggerHandler<T> handler;

        private TriggerDataHandler(TriggerResponder<T> responder, TriggerHandler<T> handler) {
            this.responder = responder;
            this.handler = handler;
        }
    }
}
