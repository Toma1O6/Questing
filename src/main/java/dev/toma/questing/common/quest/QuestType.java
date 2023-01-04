package dev.toma.questing.common.quest;

import com.mojang.serialization.Codec;
import dev.toma.questing.common.init.QuestingRegistries;
import dev.toma.questing.common.quest.instance.Quest;
import dev.toma.questing.common.quest.provider.QuestProvider;
import dev.toma.questing.utils.IdentifierHolder;
import net.minecraft.util.ResourceLocation;

public class QuestType<Q extends Quest, P extends QuestProvider<Q>> implements IdentifierHolder {

    public static final Codec<QuestProvider<?>> PROVIDER_CODEC = QuestingRegistries.QUESTS.dispatch("type", QuestProvider::getType, t -> t.providerCodec);
    public static final Codec<Quest> INSTANCE_CODEC = QuestingRegistries.QUESTS.dispatch("type", p -> p.getProvider().getType(), t -> t.instanceCodec);
    private final ResourceLocation identifier;
    private final Codec<P> providerCodec;
    private final Codec<Q> instanceCodec;

    public QuestType(ResourceLocation identifier, Codec<P> providerCodec, Codec<Q> instanceCodec) {
        this.identifier = identifier;
        this.providerCodec = providerCodec;
        this.instanceCodec = instanceCodec;
    }

    @Override
    public ResourceLocation getIdentifier() {
        return identifier;
    }
}
