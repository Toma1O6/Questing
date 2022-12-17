package dev.toma.questing.utils;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;

import java.util.function.Function;

public final class Codecs {

    public static <E extends Enum<E>> Codec<E> enumCodec(Class<E> enumClass) {
        return enumCodec(enumClass, string -> Enum.valueOf(enumClass, string), Enum::name);
    }

    public static <E extends Enum<E>> Codec<E> enumCodec(Class<E> enumClass, Function<String, E> decoder, Function<E, String> encoder) {
        return Codec.STRING.flatXmap(string -> {
            try {
                return DataResult.success(decoder.apply(string));
            } catch (IllegalArgumentException e) {
                return DataResult.error("Invalid " + enumClass.getSimpleName() + ": " + string);
            }
        }, enumType -> enumType == null ? DataResult.error("Enum is null") : DataResult.success(encoder.apply(enumType)));
    }
}
