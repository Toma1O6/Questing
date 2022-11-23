package dev.toma.questing.utils;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;

import java.util.Collection;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public final class NbtHelper {

    public static <T> ListNBT saveCollection(Collection<T> collection, BiConsumer<ListNBT, T> saveConsumer) {
        ListNBT listNBT = new ListNBT();
        collection.forEach(t -> saveConsumer.accept(listNBT, t));
        return listNBT;
    }

    public static <T, C extends Collection<T>> C readCollection(Supplier<C> reference, ListNBT source, Function<INBT, T> decoder) {
        C collection = reference.get();
        for (INBT inbt : source) {
            T t = decoder.apply(inbt);
            collection.add(t);
        }
        return collection;
    }

    public static <K, V> ListNBT saveMap(Map<K, V> map, Function<K, INBT> keyEncoder, Function<V, INBT> valueEncoder) {
        ListNBT list = new ListNBT();
        for (Map.Entry<K, V> entry : map.entrySet()) {
            K key = entry.getKey();
            V value = entry.getValue();
            CompoundNBT tag = new CompoundNBT();
            tag.put("key", keyEncoder.apply(key));
            tag.put("value", valueEncoder.apply(value));
            list.add(tag);
        }
        return list;
    }

    public static <K, V, M extends Map<K, V>> M readMap(Supplier<M> reference, ListNBT source, Function<INBT, K> keyDecoder, Function<INBT, V> valueDecoder) {
        M map = reference.get();
        for (int i = 0; i < source.size(); i++) {
            CompoundNBT tag = source.getCompound(i);
            K key = keyDecoder.apply(tag.get("key"));
            V value = valueDecoder.apply(tag.get("value"));
            map.put(key, value);
        }
        return map;
    }

    private NbtHelper() {}
}
