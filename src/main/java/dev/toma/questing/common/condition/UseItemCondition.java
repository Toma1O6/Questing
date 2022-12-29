package dev.toma.questing.common.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.toma.questing.common.condition.select.ItemSelectorType;
import dev.toma.questing.common.condition.select.Selector;
import dev.toma.questing.common.init.QuestingRegistries;
import dev.toma.questing.common.party.Party;
import dev.toma.questing.common.quest.Quest;
import dev.toma.questing.common.trigger.Events;
import dev.toma.questing.common.trigger.ResponseType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.HashSet;

public class UseItemCondition extends AbstractDefaultCondition {

    public static final Codec<UseItemCondition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.comapFlatMap(ResponseType::fromString, Enum::name).optionalFieldOf("onFail", ResponseType.PASS).forGetter(AbstractDefaultCondition::getDefaultFailureResponse),
            ItemSelectorType.CODEC.fieldOf("selector").forGetter(t -> t.itemSelector),
            Registry.ITEM.listOf().xmap(HashSet::new, ArrayList::new).optionalFieldOf("items", new HashSet<>()).forGetter(t -> t.validItems)
    ).apply(instance, UseItemCondition::new));
    private final Selector itemSelector;
    private final HashSet<Item> validItems;

    public UseItemCondition(ResponseType defaultFailureResponse, Selector itemSelector, HashSet<Item> items) {
        super(defaultFailureResponse);
        this.itemSelector = itemSelector;
        this.validItems = items;
    }

    @Override
    public ConditionType<?> getType() {
        return QuestingRegistries.USE_ITEM_CONDITION;
    }

    @Override
    public void onConditionConstructing(Party party, Quest quest, World world) {
        if (this.validItems.isEmpty())
            this.validItems.addAll(this.itemSelector.getUseableItems());
    }

    @Override
    public void registerTriggerResponders(ConditionRegisterHandler registerHandler) {
        registerHandler.register(Events.DEATH_EVENT, (eventData, quest) -> {
            DamageSource source = eventData.getSource();
            Entity damageOrigin = source.getEntity();
            if (Condition.checkIfEntityIsPartyMember(damageOrigin, quest.getParty())) {
                UsedItemProvider provider = source instanceof UsedItemProvider ? (UsedItemProvider) source : UsedItemProvider.DEFAULT;
                ItemStack usedItem = provider.getUsedItem(source);
                return this.validItems.contains(usedItem.getItem()) ? ResponseType.OK : this.getDefaultFailureResponse();
            }
            return ResponseType.SKIP;
        });
    }

    @Override
    public Condition copy() {
        return new UseItemCondition(this.getDefaultFailureResponse(), this.itemSelector, new HashSet<>(this.validItems));
    }

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
}
