package dev.toma.questing.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.JsonToNBT;

import java.util.function.Function;

public final class JsonHelper {

    public static JsonObject requireObject(JsonElement element) {
        if (!element.isJsonObject()) {
            throw new JsonSyntaxException(String.format("%s is not a JsonObject", element));
        }
        return element.getAsJsonObject();
    }

    public static <T> T[] mapArray(JsonArray array, Function<Integer, T[]> arrayFactory, Function<JsonElement, T> mapper) {
        int i = 0;
        T[] values = arrayFactory.apply(array.size());
        for (JsonElement element : array) {
            values[i++] = mapper.apply(element);
        }
        return values;
    }

    public static CompoundNBT getNbt(String raw) {
        StringReader reader = new StringReader(raw);
        try {
            return new JsonToNBT(reader).readStruct();
        } catch (CommandSyntaxException e) {
            throw new JsonSyntaxException("Invalid nbt", e);
        }
    }

    private JsonHelper() {}
}
