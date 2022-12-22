package dev.toma.questing.client.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import dev.toma.questing.client.screen.widget.PlayerProfileWidget;
import dev.toma.questing.client.screen.widget.ScrollableWidgetList;
import dev.toma.questing.client.screen.widget.TextboxWidget;
import dev.toma.questing.common.data.PlayerData;
import dev.toma.questing.common.party.Party;
import dev.toma.questing.common.party.PartyPermission;
import dev.toma.questing.network.Networking;
import dev.toma.questing.network.packet.Packet;
import dev.toma.questing.network.packet.c2s.C2S_LeaveParty;
import dev.toma.questing.network.packet.c2s.C2S_RemovePartyMember;
import dev.toma.questing.network.packet.c2s.C2S_RenameParty;
import dev.toma.questing.utils.Alignment;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class ManagePartyScreen extends OverlayScreen implements SynchronizeListener {

    private Party party;
    private TextFieldWidget nameField;

    public ManagePartyScreen(Screen parentScreen, Party party) {
        super(new TranslationTextComponent("screen.questing.manage_party"), parentScreen);
        this.party = party;
    }

    @Override
    public void onPartyUpdated(Party party) {
        this.party = party;
        this.init(minecraft, width, height);
        this.propagateListenerEvent(l -> l.onPartyUpdated(party));
    }

    @Override
    public void onPlayerDataUpdated(PlayerEntity player, PlayerData data) {
        this.propagateListenerEvent(l -> l.onPlayerDataUpdated(player, data));
    }

    @Override
    protected void init() {
        super.init();
        boolean editingName = party.isAuthorized(PartyPermission.MANAGE_PARTY, minecraft.player.getUUID());
        String partyName = party.getName();
        int margin = 5;
        this.setDimensions(this.innerWidth, 4 * margin + 20 + 20 + 150);
        if (editingName) {
            nameField = addButton(new TextFieldWidget(font, leftPos + margin, topPos + margin, innerWidth - margin * 2, 20, StringTextComponent.EMPTY));
            nameField.setValue(partyName);
        } else {
            TextboxWidget textbox = addButton(new TextboxWidget(leftPos + margin, topPos + margin, innerWidth - margin * 2, 20, new StringTextComponent(partyName), font));
            textbox.setTextRenderer(FontRenderer::drawShadow);
            textbox.setTextAlignment(Alignment.VERTICAL);
        }
        List<UUID> members = this.party.getMembers().stream()
                .sorted(Comparator.comparingInt(party::getMemberSortIndexByRoles))
                .collect(Collectors.toList());
        ScrollableWidgetList<UUID, PlayerProfileWidget> memberList = addButton(new ScrollableWidgetList<>(leftPos + margin, topPos + 2 * margin + 20, innerWidth - margin * 2, 150, members, this::constructPlayerProfileWidget));
        memberList.setEntryHeight(30);

        Button cancel = addButton(new Button(leftPos + margin, topPos + innerHeight - 20 - margin, innerWidth - margin * 2, 20, InviteToPartyScreen.CLOSE, this::close));
        if (editingName) {
            Button confirm = addButton(new Button(0, topPos + innerHeight - 20 - margin, 0, 20, DialogScreen.TEXT_CONFIRM, this::confirm));
            this.spaceEqually(cancel, confirm, margin);
        }

    }

    @Override
    protected void drawContent(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        outlinedFill(stack, 1);
    }

    private PlayerProfileWidget constructPlayerProfileWidget(UUID uuid, int x, int y, int width, int height) {
        PlayerProfileWidget widget = new PlayerProfileWidget(x + 2, y + 2, width, height - 4, uuid);
        widget.forceTooltipText(party.getMemberUsername(uuid));
        widget.setFrame(0, 0x00000000);
        UUID me = minecraft.player.getUUID();
        boolean isMe = uuid.equals(me);
        boolean imOwner = party.isAuthorized(PartyPermission.OWNER, me);
        boolean hasEditRights = party.isAuthorized(PartyPermission.MANAGE_MEMBERS, me);
        boolean isAdministrator = party.hasAnyProfile(uuid, PartyPermission.ADMIN_ROLES);
        int offsetIndex = 0;
        if (isMe || imOwner || (hasEditRights && !isAdministrator)) {
            widget.addWidget(new Button(x + width - 20 - offsetIndex * 25, y + 5, 20, 20, new StringTextComponent("x"), btn -> {
                Packet<?> packet = isMe ? new C2S_LeaveParty() : new C2S_RemovePartyMember(uuid);
                Networking.toServer(packet);
            }));
            ++offsetIndex;
        }
        if (!isMe && (imOwner || (hasEditRights && !isAdministrator))) {
            widget.addWidget(new Button(x + width - 20 - offsetIndex * 25, y + 5, 20, 20, new StringTextComponent("E"), btn -> {
                EditMemberPermissionsScreen permissionsScreen = new EditMemberPermissionsScreen(this, party, uuid);
                minecraft.setScreen(permissionsScreen);
            }));
        }
        return widget;
    }

    private void close(Button button) {
        this.minecraft.setScreen(this.getLayeredScreen());
    }

    private void confirm(Button button) {
        String originalPartyName = party.getName();
        String newName = this.nameField.getValue();
        if (!newName.equals(originalPartyName)) {
            C2S_RenameParty packet = new C2S_RenameParty(newName);
            Networking.toServer(packet);
        }
        this.close(button);
    }
}
