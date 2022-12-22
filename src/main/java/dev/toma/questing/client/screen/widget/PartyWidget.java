package dev.toma.questing.client.screen.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import dev.toma.questing.client.screen.InviteToPartyScreen;
import dev.toma.questing.client.screen.ManagePartyScreen;
import dev.toma.questing.common.party.Party;
import dev.toma.questing.utils.Alignment;
import dev.toma.questing.utils.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

import java.util.List;
import java.util.UUID;

public final class PartyWidget extends ContainerWidget {

    private final Screen parentScreen;
    private final Party party;
    private int maxDisplayedPlayerCount = 5;

    public PartyWidget(int x, int y, int width, int height, Party party, Screen parentScreen) {
        super(x, y, width, height, StringTextComponent.EMPTY);
        this.parentScreen = parentScreen;
        this.party = party;
        this.init();
    }

    public void setPlayerDisplayLimit(int limit) {
        maxDisplayedPlayerCount = limit;
        this.init();
    }

    private void init() {
        this.clear();
        List<UUID> members = party.getMembersSortedByRoles();
        UUID owner = party.getOwner();
        if (members.size() > this.maxDisplayedPlayerCount) {
            PlayerProfileWidget widget = addWidget(new PlayerProfileWidget(x + width - 20, y, 18, 18, owner));
            widget.setFrame(1, 0xFFFFFF00);
            widget.forceTooltipText(party.getMemberUsername(owner));
            widget.showOnlineStatus(true);
            addWidget(new TextButton(x + width - 45, y, 20, 20, new StringTextComponent("..."), this::managePartyButtonClicked));
            addWidget(new TextButton(x + width - 70, y, 20, 20, new StringTextComponent("+"), this::inviteButtonClicked));
        } else {
            int index = 0;
            for (UUID uuid : members) {
                int px = this.x + this.width - 20;
                PlayerProfileWidget widget = addWidget(new PlayerProfileWidget(px - index * 25 + 1, this.y + 1, 18, 18, uuid));
                boolean isOwner = uuid.equals(owner);
                widget.setFrame(1, isOwner ? 0xFFFFFF00 : 0xFF888888);
                widget.forceTooltipText(party.getMemberUsername(uuid));
                widget.showOnlineStatus(true);
                ++index;
            }
            addWidget(new TextButton(x + width - 20 - index * 25, y, 20, 20, new StringTextComponent("..."), this::managePartyButtonClicked));
            addWidget(new TextButton(x + width - 20 - (index + 1) * 25, y, 20, 20, new StringTextComponent("+"), this::inviteButtonClicked));
        }
    }

    private void managePartyButtonClicked(Button button) {
        Minecraft.getInstance().setScreen(new ManagePartyScreen(parentScreen, party));
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
