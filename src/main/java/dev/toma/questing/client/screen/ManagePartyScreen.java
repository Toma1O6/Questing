package dev.toma.questing.client.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import dev.toma.questing.client.screen.widget.PlayerProfileWidget;
import dev.toma.questing.client.screen.widget.ScrollableWidgetList;
import dev.toma.questing.client.screen.widget.TextboxWidget;
import dev.toma.questing.common.party.Party;
import dev.toma.questing.common.party.PartyPermission;
import dev.toma.questing.utils.Alignment;
import dev.toma.questing.utils.RenderUtils;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ManagePartyScreen extends OverlayScreen {

    private final Party party;
    private boolean editingName;
    private TextFieldWidget nameField;
    private ScrollableWidgetList<UUID, PlayerProfileWidget> memberList;

    public ManagePartyScreen(Screen parentScreen, Party party) {
        super(new TranslationTextComponent("screen.questing.manage_party"), parentScreen);
        this.party = party;
    }

    @Override
    protected void init() {
        super.init();
        editingName = party.isAuthorized(PartyPermission.MANAGE_PARTY, minecraft.player.getUUID());
        String partyName = party.getName();
        int margin = 5;
        if (editingName) {
            nameField = addButton(new TextFieldWidget(font, leftPos + margin, topPos + margin, innerWidth - margin * 2, 20, StringTextComponent.EMPTY));
            nameField.setValue(partyName);
        } else {
            TextboxWidget textbox = addButton(new TextboxWidget(leftPos + margin, topPos + margin, innerWidth - margin * 2, 20, new StringTextComponent(partyName), font));
            textbox.setTextRenderer(FontRenderer::drawShadow);
            textbox.setTextAlignment(Alignment.VERTICAL);
        }
        List<UUID> members = new ArrayList<>(this.party.getMembers());
        memberList = addButton(new ScrollableWidgetList<>(leftPos + margin, topPos + 2 * margin + 20, innerWidth - margin * 2, 150, members, this::constructPlayerProfileWidget));
        memberList.setEntryHeight(30);
    }

    @Override
    protected void drawContent(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        fill(stack, leftPos, topPos, leftPos + innerWidth, topPos + innerHeight, RenderUtils.DEFAULT_DIALOG_BG);
    }

    private PlayerProfileWidget constructPlayerProfileWidget(UUID uuid, int x, int y, int width, int height) {
        PlayerProfileWidget widget = new PlayerProfileWidget(x, y, width, height, uuid);
        widget.forceTooltipText(party.getMemberUsername(uuid));
        widget.setFrame(0, 0x00000000);
        UUID me = minecraft.player.getUUID();
        boolean isMe = uuid.equals(me);
        boolean imOwner = party.isAuthorized(PartyPermission.OWNER, me);
        boolean hasEditRights = party.isAuthorized(PartyPermission.MANAGE_MEMBERS, me);
        boolean isAdministrator = party.hasAnyProfile(uuid, PartyPermission.OWNER, PartyPermission.MANAGE_PARTY, PartyPermission.MANAGE_INVITES, PartyPermission.MANAGE_MEMBERS);
        int offsetIndex = 0;
        if (isMe) {
            widget.addWidget(new Button(x + width - 20 - offsetIndex * 25, y + 5, 20, 20, new StringTextComponent("x"), btn -> {
                // TODO handle leave
            }));
            ++offsetIndex;
        }
        if (!isMe && (imOwner || (hasEditRights && isAdministrator))) {
            widget.addWidget(new Button(x + width - 20 - offsetIndex * 25, y + 5, 20, 20, new StringTextComponent("E"), btn -> {
                // TODO handle edit
            }));
        }
        return widget;
    }
}
