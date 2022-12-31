package dev.toma.questing.common.quest;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import dev.toma.questing.common.component.condition.instance.Condition;
import dev.toma.questing.common.component.task.instance.Task;
import dev.toma.questing.common.component.trigger.*;
import dev.toma.questing.common.quest.instance.Quest;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class QuestActionContainer {

    private final TaskActionHolder tasks = new TaskActionHolder();
    private final EventActionHolder conditions = new EventActionHolder();

    public <T> void registerCondition(EventType<T> eventType, ActionResponder<T> responder, ActionHandler<T> handler) {
        this.conditions.register(eventType, responder, handler);
    }

    public <T> void registerTask(Task task, Trigger<T> trigger, ActionResponder<T> responder, ActionHandler<T> handler, Consumer<Condition> initializer) {
        this.tasks.register(trigger, responder, handler);
        List<Condition> conditions = task.getTaskConditions();
        for (Condition condition : conditions) {
            initializer.accept(condition);
            condition.registerTriggerResponders(new ConditionRegisterHandler() {
                @Override
                public <E> void registerWithHandler(EventType<E> eventType, ActionResponder<E> responder, ActionHandler<E> handler) {
                    ActionResponder<E> conditionResponder = (data, level, quest) -> {
                        if (task.getStatus() != ProgressStatus.ACTIVE) {
                            ResponseType responseType = responder.getResponse(data, level, quest);
                            if (responseType == ResponseType.FAIL) {
                                task.setStatus(ProgressStatus.FAILED);
                            }
                            return responseType;
                        }
                        return ResponseType.SKIP;
                    };
                    QuestActionContainer.this.registerCondition(eventType, conditionResponder, handler);
                }
            });
        }
    }

    public Collection<TaskActionHolder.TaskAction<?>> getTaskActions(Trigger<?> trigger) {
        return this.tasks.actions.get(trigger);
    }

    public Collection<EventActionHolder.EventAction<?>> getEvents(EventType<?> eventType) {
        return this.conditions.actions.get(eventType);
    }

    public static final class TriggerContext<T, E> {

        private final List<TaskActionHolder.TaskAction<T>> taskActions;
        private final List<EventActionHolder.EventAction<E>> eventActions;

        public TriggerContext(List<TaskActionHolder.TaskAction<T>> taskActions, List<EventActionHolder.EventAction<E>> eventActions) {
            this.taskActions = taskActions;
            this.eventActions = eventActions;
        }

        @SuppressWarnings("unchecked")
        public static <T, E> TriggerContext<T, E> create(QuestActionContainer container, Trigger<T> trigger, T data) {
            Map<EventType<?>, Function<T, ?>> map = trigger.getEventMappings();
            List<EventActionHolder.EventAction<E>> eventActions = new ArrayList<>();
            for (Map.Entry<EventType<?>, Function<T, ?>> entry : map.entrySet()) {
                EventType<E> eventType = (EventType<E>) entry.getKey();
                Function<T, E> mapper = (Function<T, E>) entry.getValue();
                eventActions.addAll(remapEvents(data, mapper, container.getEvents(eventType)));
            }
            List<TaskActionHolder.TaskAction<T>> triggers = container.getTaskActions(trigger).stream()
                    .map(raw -> (TaskActionHolder.TaskAction<T>) raw)
                    .collect(Collectors.toList());
            return new TriggerContext<>(triggers, eventActions);
        }

        @SuppressWarnings("unchecked")
        private static <T, E> Collection<EventActionHolder.EventAction<E>> remapEvents(T data, Function<T, E> mapper, Collection<EventActionHolder.EventAction<?>> in) {
            return in.stream()
                    .map(raw -> {
                        E e = mapper.apply(data);
                        return ((EventActionHolder.EventAction<E>) raw).of(e);
                    }).collect(Collectors.toList());
        }

        public HandledTriggerContext<T, E> handle(T triggerData, World level, Quest quest) {
            List<EventActionHolder.EventAction<E>> eventHandlers = new ArrayList<>();
            List<TaskActionHolder.TaskAction<T>> taskHandlers = new ArrayList<>();
            ResponseType type = ResponseType.OK;
            for (EventActionHolder.EventAction<E> event : this.eventActions) {
                ActionResponder<E> responder = event.responder;
                E eventData = event.eventData;
                ResponseType responseType = responder.getResponse(eventData, level, quest);
                if (responseType == ResponseType.OK) {
                    eventHandlers.add(event);
                }
                type = type.transform(responseType);
                if (type.shouldInterrupt()) {
                    break;
                }
            }
            if (!type.shouldInterrupt()) {
                for (TaskActionHolder.TaskAction<T> action : this.taskActions) {
                    ActionResponder<T> responder = action.responder;
                    ResponseType responseType = responder.getResponse(triggerData, level, quest);
                    if (responseType == ResponseType.OK) {
                        taskHandlers.add(action);
                    }
                    type = type.transform(responseType);
                    if (type.shouldInterrupt()) {
                        break;
                    }
                }
            }
            return new HandledTriggerContext<>(type, taskHandlers, eventHandlers);
        }
    }

    public static final class HandledTriggerContext<T, E> {

        private final ResponseType responseType;
        private final List<TaskActionHolder.TaskAction<T>> taskHandlers;
        private final List<EventActionHolder.EventAction<E>> eventHandlers;

        public HandledTriggerContext(ResponseType responseType, List<TaskActionHolder.TaskAction<T>> taskHandlers, List<EventActionHolder.EventAction<E>> eventHandlers) {
            this.responseType = responseType;
            this.taskHandlers = taskHandlers;
            this.eventHandlers = eventHandlers;
        }

        public ResponseType getResponseType() {
            return responseType;
        }

        public void handleSuccess(T triggerData, World level, Quest quest) {
            this.eventHandlers.forEach(e -> e.handler.handleAction(e.eventData, level, quest));
            this.taskHandlers.forEach(t -> t.handler.handleAction(triggerData, level, quest));
        }
    }

    public final static class TaskActionHolder {

        private final Multimap<Trigger<?>, TaskAction<?>> actions = ArrayListMultimap.create();

        public <T> void register(Trigger<T> trigger, ActionResponder<T> responder, ActionHandler<T> handler) {
            this.actions.put(trigger, new TaskAction<>(responder, handler));
        }

        public static class TaskAction<T> {

            final ActionResponder<T> responder;
            final ActionHandler<T> handler;

            public TaskAction(ActionResponder<T> responder, ActionHandler<T> handler) {
                this.responder = responder;
                this.handler = handler;
            }
        }
    }

    public static final class EventActionHolder {

        private final Multimap<EventType<?>, EventAction<?>> actions = ArrayListMultimap.create();

        public <E> void register(EventType<E> eventType, ActionResponder<E> responder, ActionHandler<E> handler) {
            this.actions.put(eventType, new EventAction<>(responder, handler));
        }

        public static final class EventAction<E> extends TaskActionHolder.TaskAction<E> {

            private final E eventData;

            public EventAction(ActionResponder<E> responder, ActionHandler<E> handler) {
                this(responder, handler, null);
            }

            public EventAction(ActionResponder<E> responder, ActionHandler<E> handler, E eventData) {
                super(responder, handler);
                this.eventData = eventData;
            }

            public EventAction<E> of(E eventData) {
                return new EventAction<>(this.responder, this.handler, eventData);
            }
        }
    }
}
