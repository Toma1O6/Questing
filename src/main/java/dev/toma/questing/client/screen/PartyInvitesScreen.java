package dev.toma.questing.client.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import dev.toma.questing.client.screen.widget.PartyInviteWidget;
import dev.toma.questing.client.screen.widget.ScrollableWidgetList;
import dev.toma.questing.common.data.PartyData;
import dev.toma.questing.common.data.PlayerDataProvider;
import dev.toma.questing.common.party.PartyInvite;
import dev.toma.questing.network.Networking;
import dev.toma.questing.network.packet.c2s.C2S_SendInviteResponse;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.ArrayList;
import java.util.Set;

public class PartyInvitesScreen extends OverlayScreen {

    public PartyInvitesScreen(Screen parentScreen) {
        super(new TranslationTextComponent("screen.questing.invites"), parentScreen);
    }

    @Override
    protected void init() {
        super.init();
        setDimensions(230, 210);

        PlayerDataProvider.getOptional(minecraft.player).ifPresent(data -> {
            PartyData partyData = data.getPartyData();
            Set<PartyInvite> inviteList = partyData.getMyInvites();
            ScrollableWidgetList<PartyInvite, PartyInviteWidget> list = addButton(new ScrollableWidgetList<>(leftPos + 5, topPos + 5, innerWidth - 10, innerHeight - 35, new ArrayList<>(inviteList), this::construct));
            list.setEntryHeight(60);
        });
        addButton(new Button(leftPos + 5, topPos + innerHeight - 25, innerWidth - 10, 20, InviteToPartyScreen.CLOSE, this::closeClicked));
    }

    @Override
    protected void drawContent(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        outlinedFill(stack, 1);
    }

    private PartyInviteWidget construct(PartyInvite invite, int x, int y, int width, int height) {
        PartyInviteWidget inviteWidget = new PartyInviteWidget(x, y, width, height, font, invite);
        inviteWidget.setSenderView(false);
        inviteWidget.addWidget(new Button(x + width - 5 - 50, y + height - 25, 50, 20, PartyInviteWidget.ACCEPT, btn -> {
            C2S_SendInviteResponse packet = new C2S_SendInviteResponse(true, invite.getPartyId());
            Networking.toServer(packet);
            minecraft.setScreen(new QuestsScreen());
        }));
        inviteWidget.addWidget(new Button(x + width - 10 - 100, y + height - 25, 50, 20, PartyInviteWidget.DECLINE, btn -> {
            C2S_SendInviteResponse packet = new C2S_SendInviteResponse(false, invite.getPartyId());
            Networking.toServer(packet);
        }));
        return inviteWidget;
    }

    private void closeClicked(Button button) {
        minecraft.setScreen(this.getLayeredScreen());
    }
}
