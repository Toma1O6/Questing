package dev.toma.questing.client.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import dev.toma.questing.client.screen.widget.PartyWidget;
import dev.toma.questing.common.data.PlayerDataProvider;
import dev.toma.questing.common.party.QuestParty;
import dev.toma.questing.utils.Alignment;
import dev.toma.questing.utils.RenderUtils;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public final class QuestsScreen extends Screen {

    private static final int HEADER_HEIGHT = 30;
    private static final int ELEMENT_MARGIN = 5;

    private PartyWidget partyWidget;

    public QuestsScreen() {
        super(new TranslationTextComponent("questing.screen.quest_screen"));
    }

    @Override
    protected void init() {
        // HEADER
        partyWidget = addButton(new PartyWidget(width - ELEMENT_MARGIN - 20 - ELEMENT_MARGIN - 130, ELEMENT_MARGIN, 130, 20, this::getPlayersParty, this));
        partyWidget.setPlayerDisplayLimit(5);

        addButton(new Button(width - ELEMENT_MARGIN - 20, ELEMENT_MARGIN, 20, 20, new StringTextComponent("i"), b -> {}));
    }

    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        renderBackground(stack);
        drawHeader(stack, mouseX, mouseY, partialTicks);
        super.render(stack, mouseX, mouseY, partialTicks);
    }

    private void drawHeader(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        fill(stack, 0, 0, width, HEADER_HEIGHT, 0x66 << 24);
        RenderUtils.drawAlignedText(Alignment.VERTICAL, stack, font, title, ELEMENT_MARGIN, 0, width, HEADER_HEIGHT, 0xFFFFFF, FontRenderer::width, FontRenderer::drawShadow);
    }

    private QuestParty getPlayersParty() {
        PlayerEntity player = minecraft.player;
        return PlayerDataProvider.getOptional(player)
                .map(d -> d.getPartyData().getPartyInstance().orElse(null))
                .orElse(null);
    }
}
