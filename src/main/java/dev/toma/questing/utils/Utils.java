package dev.toma.questing.utils;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class Utils {

    public static <T> List<T> instantiate(List<T> inputs, Function<T, T> converter) {
        return inputs.stream()
                .map(converter)
                .collect(Collectors.toList());
    }

    public static <E extends Enum<E>> E next(E instance) {
        return next(instance, false);
    }

    public static <E extends Enum<E>> E next(E instance, boolean looping) {
        E[] values = instance.getDeclaringClass().getEnumConstants();
        int current = instance.ordinal();
        int next = (current + 1) % values.length;
        if (!looping && next < current) {
            next = current;
        }
        return values[next];
    }

    private Utils() {}
}
