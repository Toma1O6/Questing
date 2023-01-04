package dev.toma.questing.common.quest.instance;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.toma.questing.common.component.area.AreaType;
import dev.toma.questing.common.component.area.instance.Area;
import dev.toma.questing.common.engine.QuestEngine;
import dev.toma.questing.common.party.Party;
import dev.toma.questing.common.quest.provider.SimpleAreaQuestProvider;
import dev.toma.questing.utils.PlayerLookup;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class SimpleAreaQuest extends AbstractAreaQuest {

    public static final Codec<SimpleAreaQuest> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            QuestData.CODEC.fieldOf("data").forGetter(q -> q.data),
            SimpleAreaQuestProvider.CODEC.fieldOf("provider").forGetter(q -> q.provider),
            AreaType.AREA_CODEC.optionalFieldOf("area", null).forGetter(q -> q.area)
    ).apply(instance, SimpleAreaQuest::new));
    private final SimpleAreaQuestProvider provider;
    private Area area;

    public SimpleAreaQuest(QuestData questData, SimpleAreaQuestProvider provider, Area area) {
        super(questData);
        this.provider = provider;
        this.area = area;
    }

    public SimpleAreaQuest(SimpleAreaQuestProvider provider) {
        this.provider = provider;
    }

    @Override
    public void onGenerated(Party party, World level, QuestEngine engine) {
        if (!level.isClientSide) {
            PlayerEntity player = PlayerLookup.findServerPlayer((ServerWorld) level, party.getOwner());
            if (player == null) {
                throw new IllegalStateException("Cannot generate quest when party leader is offline");
            }
            this.area = this.provider.getAreaProvider().generateArea(level, this, player.position());
        }
        super.onGenerated(party, level, engine);
    }

    @Override
    public Area getQuestArea() {
        return this.area;
    }

    @Override
    public SimpleAreaQuestProvider getProvider() {
        return provider;
    }
}
