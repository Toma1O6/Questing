package dev.toma.questing.utils;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public final class EffectProvider implements Supplier<EffectInstance> {

    public static final Codec<EffectProvider> CODEC = RecordCodecBuilder.create(b -> b.group(
            Codecs.forgeRegistryCodec(ForgeRegistries.POTIONS).fieldOf("effect").forGetter(EffectProvider::getEffect),
            Codec.intRange(1, Integer.MAX_VALUE).fieldOf("duration").orElse(100).forGetter(EffectProvider::getDuration),
            Codec.intRange(0, 255).fieldOf("amplifier").orElse(0).forGetter(EffectProvider::getAmplifier),
            Codec.BOOL.optionalFieldOf("ambient", false).forGetter(EffectProvider::isAmbient),
            Codec.BOOL.optionalFieldOf("visible", true).forGetter(EffectProvider::isVisible),
            Codec.BOOL.optionalFieldOf("showIcon", true).forGetter(EffectProvider::showIcon)
    ).apply(b, EffectProvider::new));
    private final Effect effect;
    private final int duration;
    private final int amplifier;
    private final boolean ambient;
    private final boolean visible;
    private final boolean showIcon;

    public EffectProvider(Effect effect, int duration, int amplifier, boolean ambient, boolean visible, boolean showIcon) {
        this.effect = effect;
        this.duration = duration;
        this.amplifier = amplifier;
        this.ambient = ambient;
        this.visible = visible;
        this.showIcon = visible && showIcon;
    }

    @Override
    public EffectInstance get() {
        return new EffectInstance(effect, duration, amplifier, ambient, visible, showIcon);
    }

    public Effect getEffect() {
        return effect;
    }

    public int getDuration() {
        return duration;
    }

    public int getAmplifier() {
        return amplifier;
    }

    public boolean isAmbient() {
        return ambient;
    }

    public boolean isVisible() {
        return visible;
    }

    public boolean showIcon() {
        return showIcon;
    }
}
