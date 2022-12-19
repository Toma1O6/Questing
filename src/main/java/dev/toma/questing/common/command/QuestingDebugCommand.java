package dev.toma.questing.common.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;

public final class QuestingDebugCommand {

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(
                Commands.literal("questing")
                        .requires(src -> src.hasPermission(2))
        );
    }
}
