package dev.toma.questing.reward;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import dev.toma.questing.init.QuestingRegistries;
import dev.toma.questing.utils.IdentifierHolder;
import dev.toma.questing.utils.JsonHelper;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;

public final class RewardDistributionType<D extends RewardDistributor> implements IdentifierHolder {

    private final ResourceLocation identifier;
    private final Serializer<D> serializer;

    public RewardDistributionType(ResourceLocation identifier, Serializer<D> serializer) {
        this.identifier = identifier;
        this.serializer = serializer;
    }

    @Override
    public ResourceLocation getIdentifier() {
        return identifier;
    }

    public static <D extends RewardDistributor> D fromJson(JsonElement element) {
        JsonObject object = JsonHelper.requireObject(element);
        String distributorType = JSONUtils.getAsString(object, "distributionType");
        ResourceLocation location = new ResourceLocation(distributorType);
        RewardDistributionType<D> type = QuestingRegistries.REWARD_DISTRIBUTORS.<RewardDistributionType<D>>getOptionalValueUnsafe(location)
                .orElseThrow(() -> new JsonSyntaxException("Unknown reward distributor type: " + location));
        return type.serializer.distributorFromJson(object);
    }

    public interface Serializer<D extends RewardDistributor> {

        D distributorFromJson(JsonObject data);
    }
}
