package dev.toma.questing.common.component.condition.select;

import com.mojang.serialization.Codec;
import dev.toma.questing.common.init.QuestingRegistries;
import net.minecraft.item.Item;
import net.minecraft.util.registry.Registry;

import java.util.List;

public class AnyItemSelector implements Selector {

    public static final Codec<AnyItemSelector> CODEC = Registry.ITEM.listOf()
            .xmap(AnyItemSelector::new, t -> t.items)
            .fieldOf("items").codec();
    private final List<Item> items;

    public AnyItemSelector(List<Item> items) {
        this.items = items;
    }

    @Override
    public List<Item> getUseableItems() {
        return items;
    }

    @Override
    public ItemSelectorType<?> getSelectorType() {
        return QuestingRegistries.ANY_ITEM_SELECTOR;
    }
}
