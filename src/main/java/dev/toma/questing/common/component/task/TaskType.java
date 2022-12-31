package dev.toma.questing.common.component.task;

import com.mojang.serialization.Codec;
import dev.toma.questing.common.component.task.instance.Task;
import dev.toma.questing.common.component.task.provider.TaskProvider;
import dev.toma.questing.common.init.QuestingRegistries;
import dev.toma.questing.utils.IdentifierHolder;
import net.minecraft.util.ResourceLocation;

public final class TaskType<I extends Task, T extends TaskProvider<I>> implements IdentifierHolder {

    public static final Codec<TaskProvider<?>> CODEC = QuestingRegistries.TASK.dispatch("type", TaskProvider::getType, type -> type.codec);
    public static final Codec<Task> INSTANCE_CODEC = QuestingRegistries.TASK.dispatch("type", i -> i.getProvider().getType(), type -> type.instanceCodec);
    private final ResourceLocation identifier;
    private final Codec<T> codec;
    private final Codec<I> instanceCodec;

    public TaskType(ResourceLocation identifier, Codec<T> codec, Codec<I> instanceCodec) {
        this.identifier = identifier;
        this.codec = codec;
        this.instanceCodec = instanceCodec;
    }

    @Override
    public ResourceLocation getIdentifier() {
        return identifier;
    }
}
