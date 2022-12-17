package dev.toma.questing.utils;

import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;

import java.util.function.Supplier;

public final class EffectProvider implements Supplier<EffectInstance> {

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
