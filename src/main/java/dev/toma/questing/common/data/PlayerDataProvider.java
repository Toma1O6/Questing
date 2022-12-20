package dev.toma.questing.common.data;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class PlayerDataProvider implements ICapabilitySerializable<CompoundNBT> {

    @CapabilityInject(PlayerData.class)
    public static final Capability<PlayerData> CAPABILITY = null;
    private final LazyOptional<PlayerData> instanceHolder;

    public PlayerDataProvider(PlayerEntity owner) {
        this.instanceHolder = LazyOptional.of(() -> new PlayerData.Impl(owner));
    }

    public PlayerDataProvider() {
        this.instanceHolder = LazyOptional.of(PlayerData.Impl::new);
    }

    public static LazyOptional<PlayerData> getOptional(PlayerEntity player) {
        return player.getCapability(CAPABILITY, null);
    }

    public static PlayerData getUnsafe(PlayerEntity player) {
        return getOptional(player).orElse(null);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        return CAPABILITY.orEmpty(cap, instanceHolder);
    }

    @Override
    public CompoundNBT serializeNBT() {
        return (CompoundNBT) CAPABILITY.getStorage().writeNBT(CAPABILITY, instanceHolder.orElseThrow(NullPointerException::new), null);
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        CAPABILITY.getStorage().readNBT(CAPABILITY, instanceHolder.orElseThrow(NullPointerException::new), null, nbt);
    }
}
