package dev.toma.questing.common.task;

import com.mojang.serialization.Codec;
import dev.toma.questing.common.init.QuestingRegistries;
import dev.toma.questing.utils.IdentifierHolder;
import net.minecraft.util.ResourceLocation;

public final class TaskType<I extends TaskInstance, T extends Task<I>> implements IdentifierHolder {

    public static final Codec<Task<?>> CODEC = QuestingRegistries.TASK.dispatch("type", Task::getType, type -> type.codec);
    public static final Codec<TaskInstance> INSTANCE_CODEC = QuestingRegistries.TASK.dispatch("type", i -> i.getTask().getType(), type -> type.instanceCodec);
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
