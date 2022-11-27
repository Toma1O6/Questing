package dev.toma.questing.area.spawner;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import dev.toma.questing.init.QuestingRegistries;
import dev.toma.questing.utils.IdentifierHolder;
import dev.toma.questing.utils.JsonHelper;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;

public class SpawnerType<S extends Spawner> implements IdentifierHolder {

    private final ResourceLocation identifier;
    private final Serializer<S> serializer;

    public SpawnerType(ResourceLocation identifier, Serializer<S> serializer) {
        this.identifier = identifier;
        this.serializer = serializer;
    }

    @Override
    public ResourceLocation getIdentifier() {
        return identifier;
    }

    public static <S extends Spawner> SpawnerProvider<S> fromJson(JsonElement element) {
        JsonObject data = JsonHelper.requireObject(element);
        ResourceLocation id = new ResourceLocation(JSONUtils.getAsString(data, "type"));
        SpawnerType<S> spawnerType = QuestingRegistries.SPAWNER.<SpawnerType<S>>getOptionalValueUnsafe(id)
                .orElseThrow(() -> new JsonSyntaxException("Unknown spawner type: " + id));
        return spawnerType.serializer.spawnerFromJson(data);
    }

    public interface Serializer<S extends Spawner> {

        SpawnerProvider<S> spawnerFromJson(JsonObject data);
    }
}
