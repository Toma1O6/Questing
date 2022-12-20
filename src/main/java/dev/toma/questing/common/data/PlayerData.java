package dev.toma.questing.common.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import dev.toma.questing.Questing;
import dev.toma.questing.network.Networking;
import dev.toma.questing.network.packet.s2c.S2C_SendPlayerData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public interface PlayerData extends INBTSerializable<CompoundNBT> {

    PartyData getPartyData();

    void sendDataToClient(int value);

    void receiveClientData(int value, CompoundNBT data);

    class Impl implements PlayerData {

        private final PlayerEntity player;
        private final PartyData.Impl partyData;
        private final List<SynchronizableObj<?>> synchronizables = new ArrayList<>();

        public Impl(PlayerEntity player) {
            this.player = player;
            this.partyData = registerSynchronizable(PlayerDataSynchronizationFlags.PARTY, new PartyData.Impl());
        }

        public Impl() {
            this(null);
        }

        @Override
        public PartyData getPartyData() {
            return partyData;
        }

        @Override
        public void sendDataToClient(int value) {
            CompoundNBT map = new CompoundNBT();
            this.synchronizables.stream()
                    .filter(obj -> PlayerDataSynchronizationFlags.is(obj.index, value))
                    .forEach(obj -> this.encodeSynchronizable(obj, map));
            S2C_SendPlayerData packet = new S2C_SendPlayerData(this.player.getUUID(), map, value);
            if (player instanceof ServerPlayerEntity) {
                Networking.toClient((ServerPlayerEntity) player, packet);
            }
        }

        @Override
        public void receiveClientData(int value, CompoundNBT data) {
            this.synchronizables.stream()
                    .filter(obj -> PlayerDataSynchronizationFlags.is(obj.index, value))
                    .forEach(obj -> decodeSynchronizable(obj, data));
        }

        @Override
        public CompoundNBT serializeNBT() {
            CompoundNBT nbt = new CompoundNBT();
            serializeObject("partyData", partyData, nbt);
            return nbt;
        }

        @Override
        public void deserializeNBT(CompoundNBT nbt) {
            deserializeObject("partyData", partyData, nbt);
        }

        private <T extends Encodeable<T>> void serializeObject(String key, T t, CompoundNBT out) {
            Codec<T> codec = t.codec();
            DataResult<INBT> dataResult = codec.encodeStart(NBTDynamicOps.INSTANCE, t);
            Optional<INBT> optional = dataResult.resultOrPartial(str -> Questing.LOGGER.error(Questing.MARKER_MAIN, "Unable to serialize save data - {}", str));
            optional.ifPresent(inbt -> out.put(key, inbt));
        }

        private <T extends Encodeable<T>> void deserializeObject(String key, T t, CompoundNBT in) {
            Codec<T> codec = t.codec();
            if (in.contains(key, Constants.NBT.TAG_COMPOUND)) {
                INBT inbt = in.get(key);
                DataResult<T> result = codec.parse(NBTDynamicOps.INSTANCE, inbt);
                Optional<T> optional = result.resultOrPartial(str -> Questing.LOGGER.error(Questing.MARKER_MAIN, "Unable to deserialize save data - {}", str));
                optional.ifPresent(t::resolve);
            }
        }

        private <T extends Encodeable<T>> void encodeSynchronizable(SynchronizableObj<T> obj, CompoundNBT out) {
            T t = obj.value;
            Codec<T> codec = t.codec();
            DataResult<INBT> dataResult = codec.encodeStart(NBTDynamicOps.INSTANCE, t);
            Optional<INBT> optional = dataResult.resultOrPartial(string -> Questing.LOGGER.error(Questing.MARKER_MAIN, "Unable to serialize client data - {}", string));
            optional.filter(inbt -> inbt.getType().equals(CompoundNBT.TYPE)).map(inbt -> (CompoundNBT) inbt).ifPresent(nbt -> {
                String key = String.valueOf(obj.index);
                out.put(key, nbt);
            });
        }

        private <T extends Encodeable<T>> void decodeSynchronizable(SynchronizableObj<T> obj, CompoundNBT data) {
            T t = obj.value;
            Codec<T> codec = t.codec();
            String key = String.valueOf(obj.index);
            if (data.contains(key, Constants.NBT.TAG_COMPOUND)) {
                CompoundNBT nbt = data.getCompound(key);
                DataResult<T> result = codec.parse(NBTDynamicOps.INSTANCE, nbt);
                Optional<T> optional = result.resultOrPartial(string -> Questing.LOGGER.error(Questing.MARKER_MAIN, "Unable to deserialize client data - {}", string));
                optional.ifPresent(t::resolve);
            }
        }

        private <T extends Encodeable<T>> T registerSynchronizable(int index, T t) {
            this.synchronizables.add(new SynchronizableObj<>(index, t));
            return t;
        }

        private static final class SynchronizableObj<T extends Encodeable<T>> {

            private final int index;
            private final T value;

            private SynchronizableObj(int index, T value) {
                this.index = index;
                this.value = value;
            }
        }
    }
}
