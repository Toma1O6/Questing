package dev.toma.questing.common.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.toma.questing.common.condition.select.ItemSelectorType;
import dev.toma.questing.common.condition.select.Selector;
import dev.toma.questing.common.init.QuestingRegistries;
import dev.toma.questing.common.quest.Quest;
import dev.toma.questing.common.trigger.Events;
import dev.toma.questing.common.trigger.ResponseType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

import java.util.HashSet;
import java.util.Set;

public class UseItemCondition extends ConditionProvider<UseItemCondition.Instance> {

    public static final Codec<UseItemCondition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.comapFlatMap(ResponseType::fromString, Enum::name).optionalFieldOf("onFail", ResponseType.PASS).forGetter(ConditionProvider::getDefaultFailureResponse),
            ItemSelectorType.CODEC.fieldOf("selector").forGetter(t -> t.itemSelector)
    ).apply(instance, UseItemCondition::new));
    private final Selector itemSelector;

    public UseItemCondition(ResponseType defaultFailureResponse, Selector itemSelector) {
        super(defaultFailureResponse);
        this.itemSelector = itemSelector;
    }

    @Override
    public ConditionType<?> getType() {
        return QuestingRegistries.USE_ITEM_CONDITION;
    }

    @Override
    public Instance createConditionInstance(World world, Quest quest) {
        return new Instance(this);
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

    static final class Instance extends Condition {

        private final Set<Item> validItemList;

        public Instance(UseItemCondition conditionProvider) {
            super(conditionProvider);
            this.validItemList = new HashSet<>(conditionProvider.itemSelector.getUseableItems());
        }

        @Override
        public void registerTriggerResponders(ConditionRegisterHandler registerHandler) {
            registerHandler.register(Events.DEATH_EVENT, (eventData, quest) -> {
                DamageSource source = eventData.getSource();
                Entity damageOrigin = source.getEntity();
                if (checkIfEntityIsPartyMember(damageOrigin, quest.getParty())) {
                    UsedItemProvider provider = source instanceof UsedItemProvider ? (UsedItemProvider) source : UsedItemProvider.DEFAULT;
                    ItemStack usedItem = provider.getUsedItem(source);
                    return this.validItemList.contains(usedItem.getItem()) ? ResponseType.OK : this.getProvider().getDefaultFailureResponse();
                }
                return ResponseType.OK;
            });
        }
    }
}
