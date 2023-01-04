package dev.toma.questing.common.quest.instance;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.toma.questing.common.quest.provider.SimpleQuestProvider;

public class SimpleQuest extends AbstractQuest {

    public static final Codec<SimpleQuest> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            QuestData.CODEC.fieldOf("data").forGetter(q -> q.data),
            SimpleQuestProvider.CODEC.fieldOf("provider").forGetter(q -> q.provider)
    ).apply(instance, SimpleQuest::new));
    private final SimpleQuestProvider provider;

    public SimpleQuest(QuestData data, SimpleQuestProvider provider) {
        super(data);
        this.provider = provider;
    }

    public SimpleQuest(SimpleQuestProvider provider) {
        this.provider = provider;
    }

    @Override
    public SimpleQuestProvider getProvider() {
        return provider;
    }
}
