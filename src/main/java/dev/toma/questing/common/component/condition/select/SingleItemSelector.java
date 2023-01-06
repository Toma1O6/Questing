package dev.toma.questing.common.component.condition.select;

import com.mojang.serialization.Codec;
import dev.toma.questing.common.init.QuestingRegistries;
import dev.toma.questing.utils.Codecs;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Collections;
import java.util.List;
import java.util.Random;

public class SingleItemSelector implements Selector {

    public static final Codec<SingleItemSelector> CODEC = Codecs.forgeRegistryCodec(ForgeRegistries.ITEMS).listOf()
            .xmap(SingleItemSelector::new, t -> t.items)
            .fieldOf("items").codec();
    private static final Random RANDOM = new Random();
    private final List<Item> items;

    public SingleItemSelector(List<Item> items) {
        this.items = items;
    }

    @Override
    public List<Item> getUseableItems() {
        return Collections.singletonList(this.items.size() > 0 ? this.items.get(RANDOM.nextInt(this.items.size())) : Items.AIR);
    }

    @Override
    public ItemSelectorType<?> getSelectorType() {
        return QuestingRegistries.SINGLE_ITEM_SELECTOR;
    }
}
