package dev.toma.questing.client;

import dev.toma.questing.Questing;
import dev.toma.questing.client.screen.QuestsScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import org.lwjgl.glfw.GLFW;

public final class QuestingKeyMap {

    public static final String KEYBIND_CATEGORY = "key." + Questing.MODID + ".category";

    public static final KeyBinding VIEW_QUESTS = createKeybind("view_quests", GLFW.GLFW_KEY_P);

    public static void registerKeymappings() {
        ClientRegistry.registerKeyBinding(VIEW_QUESTS);
    }

    static void handle(InputEvent.KeyInputEvent event) {
        Minecraft client = Minecraft.getInstance();
        ClientPlayerEntity player = client.player;

        if (player != null) {
            if (VIEW_QUESTS.consumeClick()) {
                handleViewQuestsKey(client, player);
            }
        }
    }

    private static void handleViewQuestsKey(Minecraft client, ClientPlayerEntity player) {
        QuestsScreen screen = new QuestsScreen();
        client.setScreen(screen);
    }

    private static KeyBinding createKeybind(String key, int keycode) {
        return new KeyBinding("key." + Questing.MODID + "." + key, keycode, KEYBIND_CATEGORY);
    }
}
