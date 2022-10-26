package dev.toma.questing.searchstore;

import net.minecraft.nbt.CompoundNBT;

public interface IDataStorage {

    CompoundNBT saveData();

    void loadData(CompoundNBT nbt);
}
