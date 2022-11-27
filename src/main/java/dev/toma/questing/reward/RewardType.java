package dev.toma.questing.reward;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import dev.toma.questing.init.QuestingRegistries;
import dev.toma.questing.utils.IdentifierHolder;
import dev.toma.questing.utils.JsonHelper;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;

public final class RewardType<R extends Reward> implements IdentifierHolder {

    private final ResourceLocation identifier;
    private final RewardSerializer<R> serializer;

    public RewardType(ResourceLocation identifier, RewardSerializer<R> serializer) {
        this.identifier = identifier;
        this.serializer = serializer;
    }

    @Override
    public ResourceLocation getIdentifier() {
        return identifier;
    }

    public static <R extends Reward> R fromJson(JsonElement element) {
        JsonObject data = JsonHelper.requireObject(element);
        ResourceLocation location = new ResourceLocation(JSONUtils.getAsString(data, "reward"));
        RewardType<R> type = QuestingRegistries.REWARDS.<RewardType<R>>getOptionalValueUnsafe(location)
                .orElseThrow(() -> new JsonSyntaxException("Unknown reward type " + location));
        return type.serializer.rewardFromJson(data);
    }

    public interface RewardSerializer<R extends Reward> {

        R rewardFromJson(JsonObject data);
    }
}
