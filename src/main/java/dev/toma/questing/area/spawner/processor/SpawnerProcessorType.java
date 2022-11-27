package dev.toma.questing.area.spawner.processor;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import dev.toma.questing.init.QuestingRegistries;
import dev.toma.questing.utils.IdentifierHolder;
import dev.toma.questing.utils.JsonHelper;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;

public class SpawnerProcessorType<P extends SpawnerProcessor> implements IdentifierHolder {

    private final ResourceLocation identifier;
    private final Serializer<P> serializer;

    public SpawnerProcessorType(ResourceLocation identifier, Serializer<P> serializer) {
        this.identifier = identifier;
        this.serializer = serializer;
    }

    @Override
    public ResourceLocation getIdentifier() {
        return identifier;
    }

    public static <P extends SpawnerProcessor> P fromJson(JsonElement element) {
        JsonObject data = JsonHelper.requireObject(element);
        ResourceLocation id = new ResourceLocation(JSONUtils.getAsString(data, "type"));
        SpawnerProcessorType<P> type = QuestingRegistries.SPAWNER_PROCESSOR.<SpawnerProcessorType<P>>getOptionalValueUnsafe(id)
                .orElseThrow(() -> new JsonSyntaxException("Unknown spawn processor type: " + id));
        return type.serializer.processorFromJson(data);
    }

    public interface Serializer<P extends SpawnerProcessor> {

        P processorFromJson(JsonObject data);
    }
}
