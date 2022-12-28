package dev.toma.questing.common.quest;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import dev.toma.questing.common.condition.Condition;
import dev.toma.questing.common.party.Party;
import dev.toma.questing.common.trigger.*;
import net.minecraft.world.World;

import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public abstract class Quest {

    private final QuestActionHandler actionHandler = new QuestActionHandler();
    private Party party;
    private List<Condition> conditions;
    private QuestStatus status = QuestStatus.NEW;

    public Quest() { // TODO require quest type as input parameter
        this.registerTriggers(actionHandler::registerTrigger);
    }

    public abstract void registerTriggers(TriggerRegisterHandler registerHandler);

    public final void assignQuest(Party party, World world) {
        this.party = party;
        // TODO init conditions
        this.conditions.forEach(condition -> {
            condition.onConditionConstructing(party, this, world);
            condition.registerTriggerResponders(actionHandler::registerConditionHandler);
        });
    }

    @SuppressWarnings("unchecked")
    public final <T, V> void trigger(Trigger<T> trigger, T triggerData) {
        ResponseType responseType = ResponseType.OK;
        Map<EventType<?>, Object> eventDataMap = new IdentityHashMap<>();
        this.remapEventsAndStore(trigger, triggerData, eventDataMap);
        // TODO first handle all events/conditions
        // Triggers should have event mappings inside so it could be used here
        if (responseType != ResponseType.OK) {
            if (responseType == ResponseType.FAIL) {
                // TODO fail quest
                return;
            }
            if (responseType == ResponseType.PASS) {
                return;
            }
        }
        Collection<TriggerDataHandler<?>> triggers = this.actionHandler.getTriggers(trigger);
        for (TriggerDataHandler<?> handler : triggers) {
            responseType = responseType.transform(this.handleTriggerResponse((TriggerDataHandler<T>)handler, triggerData));
            switch (responseType) {
                case SKIP:
                case OK:
                    continue;
                case PASS:
                    break;
                case FAIL:
                    // TODO fail quest
                    break;
            }
        }
        if (responseType == ResponseType.OK) {
            for (TriggerDataHandler<?> dataHandler : triggers) {
                this.handleTriggerSuccess((TriggerDataHandler<T>) dataHandler, triggerData);
            }
            // TODO trigger success in all event handlers
        }
    }

    public Party getParty() {
        return party;
    }

    private <T> ResponseType handleTriggerResponse(TriggerDataHandler<T> dataHandler, T data) {
        return dataHandler.responder.onTrigger(data);
    }

    private <T> void handleTriggerSuccess(TriggerDataHandler<T> dataHandler, T data) {
        dataHandler.handler.handleSuccessfullTrigger(data);
    }

    @SuppressWarnings("unchecked")
    private <T, V> void remapEventsAndStore(Trigger<T> trigger, T triggerData, Map<EventType<?>, Object> out) {
        Map<EventType<?>, Function<T, ?>> mappers = trigger.getEventMappings();
        for (Map.Entry<EventType<?>, Function<T, ?>> mapper : mappers.entrySet()) {
            EventType<V> eventType = (EventType<V>) mapper.getKey();
            Function<T, V> mapperFunc = (Function<T, V>) mapper.getValue();
            out.put(eventType, mapperFunc.apply(triggerData));
        }
    }

    private static final class QuestActionHandler {

        private final Multimap<Trigger<?>, TriggerDataHandler<?>> triggerHandlers = ArrayListMultimap.create();
        private final Multimap<EventType<?>, EventDataHandler<?>> dataHandlers = ArrayListMultimap.create();

        private <T> void registerTrigger(Trigger<T> trigger, TriggerResponder<T> responder, TriggerHandler<T> handler) {
            this.triggerHandlers.put(trigger, new TriggerDataHandler<>(responder, handler));
        }

        private <T> void registerConditionHandler(EventType<T> eventType, EventResponder<T> responder, EventHandler<T> handler) {
            this.dataHandlers.put(eventType, new EventDataHandler<>(responder, handler));
        }

        private Collection<TriggerDataHandler<?>> getTriggers(Trigger<?> trigger) {
            return this.triggerHandlers.get(trigger);
        }
    }

    private static final class TriggerDataHandler<T> {

        private final TriggerResponder<T> responder;
        private final TriggerHandler<T> handler;

        private TriggerDataHandler(TriggerResponder<T> responder, TriggerHandler<T> handler) {
            this.responder = responder;
            this.handler = handler;
        }
    }

    private static final class EventDataHandler<T> {

        private final EventResponder<T> responder;
        private final EventHandler<T> handler;

        private EventDataHandler(EventResponder<T> responder, EventHandler<T> handler) {
            this.responder = responder;
            this.handler = handler;
        }
    }
}
