package dev.toma.questing.common.component.condition.select;

import com.mojang.serialization.Codec;
import dev.toma.questing.common.init.QuestingRegistries;
import dev.toma.questing.utils.Codecs;
import net.minecraft.item.Item;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

public class AnyItemSelector implements Selector {

    public static final Codec<AnyItemSelector> CODEC = Codecs.forgeRegistryCodec(ForgeRegistries.ITEMS).listOf()
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
