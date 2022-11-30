package dev.toma.questing.condition;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import dev.toma.questing.init.QuestingRegistries;
import dev.toma.questing.utils.IdentifierHolder;
import dev.toma.questing.utils.JsonHelper;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;

public class ConditionType<C extends ConditionProvider<?>> implements IdentifierHolder {

    private final ResourceLocation identifier;
    private final Serializer<C> serializer;

    public ConditionType(ResourceLocation identifier, Serializer<C> serializer) {
        this.identifier = identifier;
        this.serializer = serializer;
    }

    @Override
    public ResourceLocation getIdentifier() {
        return identifier;
    }

    public static <C extends ConditionProvider<?>> C fromJson(JsonElement element) {
        JsonObject data = JsonHelper.requireObject(element);
        ResourceLocation id = new ResourceLocation(JSONUtils.getAsString(data, "type"));
        ConditionType<C> type = QuestingRegistries.CONDITION.<ConditionType<C>>getOptionalValueUnsafe(id)
                .orElseThrow(() -> new JsonSyntaxException("Unknown condition type: " + id));
        return type.serializer.providerFromJson(data);
    }

    public interface Serializer<C extends ConditionProvider<?>> {

        C providerFromJson(JsonObject data);
    }
}
