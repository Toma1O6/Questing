package dev.toma.questing.reward;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import dev.toma.questing.init.QuestingRegistries;
import dev.toma.questing.utils.IdentifierHolder;
import dev.toma.questing.utils.JsonHelper;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;

public final class RewardTransformerType<V, T extends IRewardTransformer<V>> implements IdentifierHolder {

    private final ResourceLocation identifier;
    private final Serializer<V, T> serializer;
    private final Class<V> type;

    public RewardTransformerType(ResourceLocation identifier, Serializer<V, T> serializer, Class<V> type) {
        this.identifier = identifier;
        this.serializer = serializer;
        this.type = type;
    }

    @Override
    public ResourceLocation getIdentifier() {
        return identifier;
    }

    public static <V, T extends IRewardTransformer<V>> T fromJson(JsonElement element, Class<V> type) {
        JsonObject data = JsonHelper.requireObject(element);
        ResourceLocation location = new ResourceLocation(JSONUtils.getAsString(data, "type"));
        RewardTransformerType<V, T> transformerType = QuestingRegistries.REWARD_TRANSFORMERS.getValueUnsafe(location);
        if (transformerType == null) {
            throw new JsonSyntaxException("Unknown reward transformer: " + location);
        }
        if (!transformerType.type.equals(type)) {
            throw new JsonSyntaxException("Mismatched reward transformer: " + location + ". Required data type is " + type.getSimpleName() + ", got" + transformerType.type.getSimpleName());
        }
        return transformerType.serializer.transformerFromJson(data);
    }

    public interface Serializer<V, T extends IRewardTransformer<V>> {

        T transformerFromJson(JsonObject data);
    }
}
