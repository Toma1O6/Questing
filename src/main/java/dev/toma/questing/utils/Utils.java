package dev.toma.questing.utils;

import dev.toma.questing.common.party.Party;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

import java.util.*;
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

    public static void giveItemToPlayer(ItemStack stack, PlayerEntity player) {
        if (player.level.isClientSide) return;
        if (!player.addItem(stack)) {
            ItemEntity entity = new ItemEntity(player.level, player.getX(), player.getY(), player.getZ(), stack.copy());
            entity.setNoPickUpDelay();
            player.level.addFreshEntity(entity);
        }
    }

    public static List<ItemStack> createLargeItemStack(ItemStack entry, int growAmount) {
        List<ItemStack> results = new ArrayList<>();
        int stackLimit = entry.getMaxStackSize();
        int remaining = growAmount;
        while (remaining > 0) {
            int toTake = Math.min(stackLimit, remaining);
            ItemStack stack = entry.copy();
            stack.setCount(toTake);
            results.add(stack);
            remaining -= toTake;
        }
        return results;
    }

    public static boolean checkIfEntityIsPartyMember(Entity entity, Party party) {
        if (entity instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) entity;
            UUID playerId = player.getUUID();
            Set<UUID> members = party.getMembers();
            return members.contains(playerId);
        }
        return false;
    }

    public static <T> T getRandomListElement(List<T> list, Random random) {
        return list.get(random.nextInt(list.size()));
    }

    public static double getEntityDistance2(Entity entity1, Entity entity2) {
        return getDistance2(entity1.getX(), entity1.getZ(), entity2.getX(), entity2.getZ());
    }

    public static double getEntityDistance3(Entity entity1, Entity entity2) {
        return getDistance3(entity1.getX(), entity1.getY(), entity1.getZ(), entity2.getX(), entity2.getY(), entity2.getZ());
    }

    public static double getDistance2(double x1, double z1, double x2, double z2) {
        double x = x1 - x2;
        double z = z1 - z2;
        return Math.sqrt(x * x + z * z);
    }

    public static double getDistance3(double x1, double y1, double z1, double x2, double y2, double z2) {
        double x = x1 - x2;
        double y = y1 - y2;
        double z = z1 - z2;
        return Math.sqrt(x * x + y * y + z * z);
    }

    public static boolean isNullOrEmpty(String string) {
        return string == null || string.isEmpty();
    }

    public static boolean isNullOrEmpty(Object[] array) {
        return array == null || array.length == 0;
    }

    public static boolean isNullOrEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    private Utils() {}
}
