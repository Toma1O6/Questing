package dev.toma.questing.client.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import dev.toma.questing.client.screen.widget.SearchFieldWidget;
import dev.toma.questing.client.screen.widget.TextboxWidget;
import dev.toma.questing.common.party.Party;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class InviteToPartyScreen extends OverlayScreen {

    private static final ITextComponent CLOSE = new TranslationTextComponent("text.questing.close");
    private static final ITextComponent INVITE = new TranslationTextComponent("text.questing.send_invite");
    private static final String PLAYER_NOT_FOUND = "text.questing.error.player_not_found";
    private static final String TOO_MANY_PLAYERS_FOUND = "text.questing.error.too_many_players_found";
    private static final String PLAYER_ALREADY_IN_PARTY = "text.questing.error.player_already_in_party";
    private final Party currentParty;
    private SearchFieldWidget<? extends PlayerEntity> searchFieldWidget;
    private TextboxWidget textboxWidget;

    public InviteToPartyScreen(Screen layeredScreen, Party currentParty) {
        super(new TranslationTextComponent("screen.questing.invite_to_party"), layeredScreen);
        this.currentParty = currentParty;
    }

    @Override
    protected void init() {
        super.init();
        int margin = 5;
        Set<UUID> members = this.currentParty.getMembers();
        List<? extends PlayerEntity> list = minecraft.level.players().stream()
                .filter(player -> !members.contains(player.getUUID()))
                .collect(Collectors.toList());
        PlayerEntity p = minecraft.level.players().get(0);
        List<PlayerEntity> dummyList = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            dummyList.add(p);
        }
        // add search field for players
        searchFieldWidget = addButton(new SearchFieldWidget<>(font, leftPos + margin, topPos + margin, innerWidth - 2 * margin, 20, () -> dummyList));
        searchFieldWidget.setTextFormatter(player -> player.getName().getString());
        searchFieldWidget.suggests(10);
        searchFieldWidget.assignDefaultValue();
        // add textbox for errors
        textboxWidget = addButton(new TextboxWidget(leftPos + margin, topPos + innerHeight - 45, innerWidth - 10, 15, StringTextComponent.EMPTY, font));
        textboxWidget.setTextColor(0xFF4444);
        // add cancel/confirm buttons
        int btnY = this.topPos + this.innerHeight - margin - 20;
        Button close = new Button(0, btnY, 0, 20, CLOSE, this::closeClicked);
        Button invite = new Button(0, btnY, 0, 20, INVITE, this::inviteClicked);
        this.spaceEqually(close, invite, margin);
        addButton(close);
        addButton(invite);
    }

    @Override
    protected void drawContent(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        fill(stack, leftPos, topPos, leftPos + innerWidth, topPos + innerHeight, 0xFF << 24);
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
        List<? extends PlayerEntity> queryResults = this.searchFieldWidget.getResults();
        if (queryResults.size() == 0) {
            this.textboxWidget.setMessage(new TranslationTextComponent(PLAYER_NOT_FOUND, this.searchFieldWidget.getValue()));
        } else if (queryResults.size() > 1) {
            this.textboxWidget.setMessage(new TranslationTextComponent(TOO_MANY_PLAYERS_FOUND, this.searchFieldWidget.getValue()));
        }
        ITextComponent message = this.textboxWidget.getMessage();
        if (message.equals(StringTextComponent.EMPTY)) {
            Set<UUID> partyMembers = this.currentParty.getMembers();
            PlayerEntity result = queryResults.get(0);
            if (partyMembers.contains(result.getUUID())) {
                this.textboxWidget.setMessage(new TranslationTextComponent(PLAYER_ALREADY_IN_PARTY, result.getName().getString()));
            } else {
                // TODO send invite
                // this.currentParty.invite(result.getMapped());
                this.closeClicked(button); // close overlay
            }
        }
    }
}