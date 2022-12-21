package dev.toma.questing.client.screen.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import dev.toma.questing.client.screen.InviteToPartyScreen;
import dev.toma.questing.common.party.QuestParty;
import dev.toma.questing.utils.Alignment;
import dev.toma.questing.utils.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;

public final class PartyWidget extends ContainerWidget {

    private final Supplier<QuestParty> provider;
    private final Screen parentScreen;
    private QuestParty party;
    private int maxDisplayedPlayerCount = 5;

    public PartyWidget(int x, int y, int width, int height, Supplier<QuestParty> provider, Screen parentScreen) {
        super(x, y, width, height, StringTextComponent.EMPTY);
        this.provider = provider;
        this.parentScreen = parentScreen;
        this.refreshCachedData();
    }

    public void setPlayerDisplayLimit(int limit) {
        maxDisplayedPlayerCount = limit;
        this.refreshCachedData();
    }

    private void refreshCachedData() {
        this.clear();
        this.party = provider.get();
        Set<UUID> memberSet = party.getMembers();
        UUID owner = party.getOwner();
        if (memberSet.size() > this.maxDisplayedPlayerCount) {
            PlayerProfileWidget widget = addWidget(new PlayerProfileWidget(x + width - 20, y, 20, 20, owner));
            widget.setFrame(Minecraft.getInstance().player.getUUID().equals(owner) ? 2 : 1, 0xFFFFFF00);
            widget.forceTooltipText(party.getMemberUsername(owner));
            addWidget(new TextButton(x + width - 45, y, 20, 20, new StringTextComponent("..."), this::showPlayerListButtonClicked));
            addWidget(new TextButton(x + width - 70, y, 20, 20, new StringTextComponent("+"), this::inviteButtonClicked));
        } else {
            int index = 0;
            for (UUID uuid : memberSet) {
                int px = this.x + this.width - 20;
                PlayerProfileWidget widget = addWidget(new PlayerProfileWidget(px - index * 25, this.y, 20, 20, uuid));
                widget.setClickResponder(this::showContextMenu, -1);
                boolean isOwner = uuid.equals(owner);
                boolean isMe = Minecraft.getInstance().player.getUUID().equals(uuid);
                widget.setFrame(isMe ? 2 : 1, isOwner ? 0xFFFFFF00 : 0xFF888888);
                widget.forceTooltipText(party.getMemberUsername(uuid));
                ++index;
            }
            addWidget(new TextButton(x + width - 20 - index * 25, y, 20, 20, new StringTextComponent("+"), this::inviteButtonClicked));
        }
    }

    private void showContextMenu(int mouseX, int mouseY, PlayerProfileWidget profileWidget) {

    }

    private void showPlayerListButtonClicked(Button button) {

    }

    private void inviteButtonClicked(Button button) {
        Minecraft.getInstance().setScreen(new InviteToPartyScreen(parentScreen, party));
    }

    public static final class TextButton extends Button {

        public TextButton(int x, int y, int width, int height, ITextComponent text, IPressable onClick) {
            super(x, y, width, height, text, onClick);
        }

        @Override
        public void renderButton(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
            RenderUtils.outline(stack, x, y, x + width, y + height, isHovered ? 0xFFFFFFFF : 0xFF888888, 1, 0);
            ITextComponent message = this.getMessage();
            if (message != null) {
                RenderUtils.drawAlignedText(Alignment.CENTER, stack, message, x, y, width, height, 0xFFFFFF, FontRenderer::width, FontRenderer::drawShadow);
            }
        }
    }
}
