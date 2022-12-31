package dev.toma.questing.utils;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.registry.Registry;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public final class Codecs {

    public static final Codec<UUID> UUID_STRING = Codec.STRING.xmap(UUID::fromString, UUID::toString);
    public static final Codec<Vector3d> VECTOR3D = RecordCodecBuilder.create(instance -> instance.group(
            Codec.DOUBLE.fieldOf("x").forGetter(v -> v.x),
            Codec.DOUBLE.fieldOf("y").forGetter(v -> v.y),
            Codec.DOUBLE.fieldOf("z").forGetter(v -> v.z)
    ).apply(instance, Vector3d::new));
    public static final Codec<ItemStack> SIMPLIFIED_ITEMSTACK = RecordCodecBuilder.create(instance -> instance.group(
            Registry.ITEM.fieldOf("item").forGetter(ItemStack::getItem),
            Codec.INT.optionalFieldOf("count", 1).forGetter(ItemStack::getCount),
            CompoundNBT.CODEC.optionalFieldOf("nbt").forGetter(item -> Optional.ofNullable(item.getTag()))
    ).apply(instance, (item, count, optionalTag) -> {
        ItemStack stack = new ItemStack(item, Math.max(1, count));
        optionalTag.ifPresent(stack::setTag);
        return stack;
    }));
    public static final Codec<Pattern> PATTERN_CODEC = Codec.STRING.flatXmap(string -> {
        try {
            Pattern pattern = Pattern.compile(string);
            return DataResult.success(pattern);
        } catch (PatternSyntaxException e) {
            return DataResult.error(e.getMessage());
        }
    }, pattern -> pattern == null ? DataResult.error("Pattern is null") : DataResult.success(pattern.pattern()));

    public static <E extends Enum<E>> Codec<E> enumCodec(Class<E> enumClass) {
        return enumCodec(enumClass, Function.identity());
    }

    public static <E extends Enum<E>> Codec<E> enumCodec(Class<E> enumClass, Function<String, String> inputAdjuster) {
        return enumCodec(enumClass, string -> Enum.valueOf(enumClass, string), Enum::name, inputAdjuster);
    }

    public static <E extends Enum<E>> Codec<E> enumCodec(Class<E> enumClass, Function<String, E> decoder, Function<E, String> encoder) {
        return enumCodec(enumClass, decoder, encoder, Function.identity());
    }

    public static <E extends Enum<E>> Codec<E> enumCodec(Class<E> enumClass, Function<String, E> decoder, Function<E, String> encoder, Function<String, String> inputAdjuster) {
        return Codec.STRING.flatXmap(string -> {
            try {
                String input = inputAdjuster.apply(string);
                return DataResult.success(decoder.apply(input));
            } catch (IllegalArgumentException e) {
                return DataResult.error("Invalid " + enumClass.getSimpleName() + ": " + string);
            }
        }, enumType -> enumType == null ? DataResult.error("Enum is null") : DataResult.success(encoder.apply(enumType)));
    }

    public static <E extends Enum<E>> Codec<E> enumCodecComap(Class<E> enumClass, Function<String, DataResult<E>> decoder, Function<E, String> encoder, Function<String, String> inputAdjuster) {
        return Codec.STRING.comapFlatMap(string -> decoder.apply(inputAdjuster.apply(string)), encoder);
    }
}
