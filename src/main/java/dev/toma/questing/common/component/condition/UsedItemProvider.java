package dev.toma.questing.common.component.condition;

import dev.toma.questing.common.component.condition.provider.UseItemConditionProvider;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;

@FunctionalInterface
public interface UsedItemProvider {

    UsedItemProvider DEFAULT = src -> {
        Entity entity = src.getEntity();
        if (entity instanceof LivingEntity) {
            return ((LivingEntity) entity).getMainHandItem();
        }
        return ItemStack.EMPTY;
    };

    ItemStack getUsedItem(DamageSource source);
}
