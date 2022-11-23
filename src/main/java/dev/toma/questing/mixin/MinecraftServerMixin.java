package dev.toma.questing.mixin;

import dev.toma.questing.quest.QuestDataFile;
import net.minecraft.command.ICommandSource;
import net.minecraft.profiler.ISnooperInfo;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.concurrent.RecursiveEventLoop;
import net.minecraft.util.concurrent.TickDelayedTask;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.DimensionSavedDataManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin extends RecursiveEventLoop<TickDelayedTask> implements ISnooperInfo, ICommandSource, AutoCloseable {

    @Shadow public abstract ServerWorld overworld();

    public MinecraftServerMixin(String p_i50401_1_) {
        super(p_i50401_1_);
    }

    @Inject(method = "saveAllChunks", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/storage/IServerConfiguration;setCustomBossEvents(Lnet/minecraft/nbt/CompoundNBT;)V"))
    private void questing$saveQuestData(boolean b1, boolean b2, boolean b3, CallbackInfoReturnable<Boolean> cir) {
        ServerWorld overworld = this.overworld();
        DimensionSavedDataManager dataManager = overworld.getDataStorage();
        QuestDataFile.getFile().saveData(dataManager.dataFolder);
    }

    @Inject(method = "loadLevel", at = @At("RETURN"))
    private void questing$loadQuestData(CallbackInfo ci) {
        ServerWorld overworld = this.overworld();
        DimensionSavedDataManager dataManager = overworld.getDataStorage();
        QuestDataFile.getFile().loadData(dataManager.dataFolder);
    }
}
