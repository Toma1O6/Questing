package dev.toma.questing.client.screen;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.TranslationTextComponent;

public final class QuestsScreen extends Screen {

    public QuestsScreen() {
        super(new TranslationTextComponent("questing.screen.quest_screen"));
    }

    @Override
    protected void init() {
        
    }
}
