package dev.toma.questing.common.data;

import com.mojang.serialization.Codec;

public interface Encodeable<T> {

    Codec<T> codec();

    void resolve(T t);
}
