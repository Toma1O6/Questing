package dev.toma.questing.common.component.trigger;

import dev.toma.questing.common.quest.instance.Quest;
import net.minecraft.world.World;

@FunctionalInterface
public interface ActionResponder<T> {

    ResponseType getResponse(T data, World level, Quest quest);

    static <T> ActionResponder<T> constant(ResponseType type) {
        return (data, level, quest) -> type;
    }
}
