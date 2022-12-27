package dev.toma.questing.client.screen;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.matrix.MatrixStack;
import dev.toma.questing.client.screen.widget.SearchFieldWidget;
import dev.toma.questing.client.screen.widget.TextboxWidget;
import dev.toma.questing.common.party.Party;
import dev.toma.questing.network.Networking;
import dev.toma.questing.network.packet.c2s.C2S_RequestInviteCreation;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.network.play.NetworkPlayerInfo;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class InviteToPartyScreen extends NotificationOverlayScreen implements SynchronizeListener {

    public static final ITextComponent CLOSE = new TranslationTextComponent("text.questing.close");
    private static final ITextComponent INVITE = new TranslationTextComponent("text.questing.send_invite");
    private static final String PLAYER_NOT_FOUND = "text.questing.error.player_not_found";
    private static final String TOO_MANY_PLAYERS_FOUND = "text.questing.error.too_many_players_found";
    private static final String PLAYER_ALREADY_IN_PARTY = "text.questing.error.player_already_in_party";
    private Party currentParty;
    private SearchFieldWidget<NetworkPlayerInfo> searchFieldWidget;
    private TextboxWidget textboxWidget;

    public InviteToPartyScreen(Screen layeredScreen, Party currentParty) {
        super(new TranslationTextComponent("screen.questing.invite_to_party"), layeredScreen);
        this.currentParty = currentParty;
    }

    @Override
    public void onPartyUpdated(Party party) {
        this.currentParty = party;
        this.init(minecraft, width, height);
        this.propagateListenerEvent(l -> l.onPartyUpdated(party));
    }

    @Override
    protected void init() {
        super.init();
        int margin = 5;
        Set<UUID> members = this.currentParty.getMembers();
        searchFieldWidget = addButton(new SearchFieldWidget<>(font, leftPos + margin, topPos + margin, innerWidth - 2 * margin, 20, () -> minecraft.getConnection().getOnlinePlayers().stream()
                .filter(info -> !members.contains(info.getProfile().getId()))
                .collect(Collectors.toList())));
        searchFieldWidget.setTextFormatter(info -> info.getProfile().getName());
        searchFieldWidget.suggests(10);
        searchFieldWidget.assignDefaultValue();
        textboxWidget = addButton(new TextboxWidget(leftPos + margin, topPos + innerHeight - 45, innerWidth - 10, 15, StringTextComponent.EMPTY, font));
        textboxWidget.setTextColor(0xFF4444);
        int btnY = this.topPos + this.innerHeight - margin - 20;
        Button close = new Button(0, btnY, 0, 20, CLOSE, this::closeClicked);
        Button invite = new Button(0, btnY, 0, 20, INVITE, this::inviteClicked);
        this.spaceEqually(close, invite, margin);
        addButton(close);
        addButton(invite);
    }

    @Override
    protected void drawContent(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        outlinedFill(stack, 1);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.searchFieldWidget.isFocused()) {
            return this.searchFieldWidget.keyPressed(keyCode, scanCode, modifiers);
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean changeFocus(boolean reverseOrder) {
        return !this.searchFieldWidget.isFocused() && super.changeFocus(reverseOrder);
    }

    private void closeClicked(Button button) {
        this.minecraft.setScreen(this.getLayeredScreen());
    }

    private void inviteClicked(Button button) {
        this.textboxWidget.setMessage(StringTextComponent.EMPTY);
        List<NetworkPlayerInfo> queryResults = this.searchFieldWidget.getResults();
        if (queryResults.size() == 0) {
            this.textboxWidget.setMessage(new TranslationTextComponent(PLAYER_NOT_FOUND, this.searchFieldWidget.getValue()));
        } else if (queryResults.size() > 1) {
            this.textboxWidget.setMessage(new TranslationTextComponent(TOO_MANY_PLAYERS_FOUND, this.searchFieldWidget.getValue()));
        }
        ITextComponent message = this.textboxWidget.getMessage();
        if (message.equals(StringTextComponent.EMPTY)) {
            Set<UUID> partyMembers = this.currentParty.getMembers();
            NetworkPlayerInfo result = queryResults.get(0);
            GameProfile profile = result.getProfile();
            if (partyMembers.contains(profile.getId())) {
                this.textboxWidget.setMessage(new TranslationTextComponent(PLAYER_ALREADY_IN_PARTY, profile.getName()));
            } else {
                C2S_RequestInviteCreation packet = new C2S_RequestInviteCreation(profile.getId());
                Networking.toServer(packet);
                this.closeClicked(button);
            }
        }
    }
}
