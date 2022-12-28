package dev.toma.questing.common.condition.select;

import net.minecraft.item.Item;

import java.util.List;

public interface Selector {

    List<Item> getUseableItems();

    ItemSelectorType<?> getSelectorType();
}
