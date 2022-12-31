package dev.toma.questing.common.component.condition.instance;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.toma.questing.common.component.condition.UsedItemProvider;
import dev.toma.questing.common.component.condition.provider.UseItemConditionProvider;
import dev.toma.questing.common.component.trigger.Events;
import dev.toma.questing.common.component.trigger.ResponseType;
import dev.toma.questing.common.component.trigger.event.DeathEvent;
import dev.toma.questing.common.quest.ConditionRegisterHandler;
import dev.toma.questing.common.quest.instance.Quest;
import dev.toma.questing.utils.Utils;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.registry.Registry;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class UseItemCondition implements Condition {

    public static final Codec<UseItemCondition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            UseItemConditionProvider.CODEC.fieldOf("provider").forGetter(t -> t.provider),
            Registry.ITEM.listOf().xmap(list -> (Set<Item>) new HashSet<>(list), ArrayList::new).fieldOf("items").forGetter(t -> t.validItems)
    ).apply(instance, UseItemCondition::new));
    private final UseItemConditionProvider provider;
    private final Set<Item> validItems;

    public UseItemCondition(UseItemConditionProvider provider) {
        this.provider = provider;
        this.validItems = new HashSet<>(provider.getItemSelector().getUseableItems());
    }

    public UseItemCondition(UseItemConditionProvider provider, Set<Item> validItems) {
        this.provider = provider;
        this.validItems = validItems;
    }

    @Override
    public UseItemConditionProvider getProvider() {
        return provider;
    }

    @Override
    public void registerTriggerResponders(ConditionRegisterHandler registerHandler) {
        registerHandler.register(Events.DEATH_EVENT, (event, level, quest) -> handle(event, quest));
        registerHandler.register(Events.DAMAGE_EVENT, (event, level, quest) -> handle(event, quest));
    }

    protected ResponseType handle(DeathEvent event, Quest quest) {
        DamageSource source = event.getSource();
        Entity damageOrigin = source.getEntity();
        if (Utils.checkIfEntityIsPartyMember(damageOrigin, quest.getParty())) {
            UsedItemProvider provider = source instanceof UsedItemProvider ? (UsedItemProvider) source : UsedItemProvider.DEFAULT;
            ItemStack usedItem = provider.getUsedItem(source);
            return this.validItems.contains(usedItem.getItem()) ? ResponseType.OK : this.provider.getDefaultFailureResponse();
        }
        return ResponseType.SKIP;
    }
}
