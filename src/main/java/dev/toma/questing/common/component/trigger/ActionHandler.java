package dev.toma.questing.common.component.trigger;

import dev.toma.questing.common.quest.instance.Quest;
import net.minecraft.world.World;

@FunctionalInterface
public interface ActionHandler<T> {

    void handleAction(T data, World level, Quest quest);

    static <T> ActionHandler<T> none() {
        return (data, level, quest) -> {};
    }
}
