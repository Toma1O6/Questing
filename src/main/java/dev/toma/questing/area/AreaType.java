package dev.toma.questing.area;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import dev.toma.questing.init.QuestingRegistries;
import dev.toma.questing.utils.IdentifierHolder;
import dev.toma.questing.utils.JsonHelper;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;

public final class AreaType<P extends AreaProvider<?>> implements IdentifierHolder {

    private final ResourceLocation identifier;
    private final ProviderSerializer<P> serializer;

    public AreaType(ResourceLocation identifier, ProviderSerializer<P> serializer) {
        this.identifier = identifier;
        this.serializer = serializer;
    }

    @Override
    public ResourceLocation getIdentifier() {
        return identifier;
    }

    public static <P extends AreaProvider<?>> P fromJson(JsonElement element) {
        JsonObject data = JsonHelper.requireObject(element);
        ResourceLocation id = new ResourceLocation(JSONUtils.getAsString(data, "type"));
        AreaType<P> type = QuestingRegistries.AREA.<AreaType<P>>getOptionalValueUnsafe(id)
                .orElseThrow(() -> new JsonSyntaxException("Unknown area type: " + id));
        return type.serializer.providerFromJson(data);
    }

    public interface ProviderSerializer<P extends AreaProvider<?>> {

        P providerFromJson(JsonObject data);
    }
}
