package dev.toma.questing.client.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import dev.toma.questing.common.party.Party;
import dev.toma.questing.common.party.PartyPermission;
import dev.toma.questing.network.Networking;
import dev.toma.questing.network.packet.c2s.C2S_UpdateMemberRoles;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.button.CheckboxButton;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.EnumSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class EditMemberPermissionsScreen extends OverlayScreen implements SynchronizeListener {

    private Party party;
    private final UUID member;
    private Set<PartyPermission> selected;

    public EditMemberPermissionsScreen(Screen parent, Party party, UUID member) {
        super(new TranslationTextComponent("screen.questing.manage_permissions"), parent);
        this.party = party;
        this.member = member;
    }

    @Override
    public void onPartyUpdated(Party party) {
        this.party = party;
        this.init(minecraft, width, height);
        this.propagateListenerEvent(l -> l.onPartyUpdated(party));
    }

    @Override
    protected void init() {
        super.init();
        Set<PartyPermission> permissions = PartyPermission.ADJUSTABLE_PERMISSIONS.stream()
                .filter(perm -> {
                    if (perm == PartyPermission.MANAGE_MEMBERS) {
                        return minecraft.player.getUUID().equals(party.getOwner());
                    }
                    return true;
                })
                .collect(Collectors.toSet());
        int permissionSize = permissions.size();
        this.setDimensions(200, 40 + permissionSize * 25);
        int i = 0;
        this.selected = EnumSet.noneOf(PartyPermission.class);
        for (PartyPermission permission : permissions) {
            boolean active = this.party.isAuthorized(permission, member);
            if (active) {
                this.selected.add(permission);
            }
            ITextComponent label = new TranslationTextComponent("permission.questing." + permission.name().toLowerCase());
            addButton(new CheckboxButton(leftPos + 5, topPos + 15 + i++ * 25, innerWidth - 10, 20, label, active) {
                @Override
                public void onPress() {
                    super.onPress();
                    Set<PartyPermission> set = EditMemberPermissionsScreen.this.selected;
                    if (this.selected()) {
                        set.add(permission);
                    } else {
                        set.remove(permission);
                    }
                }
            });
        }
        Button cancel = new Button(0, topPos + innerHeight - 25, 0, 20, DialogScreen.TEXT_CANCEL, this::cancel);
        Button confirm = new Button(0, topPos + innerHeight - 25, 0, 20, DialogScreen.TEXT_CONFIRM, this::confirm);
        spaceEqually(cancel, confirm, 5);
        addButton(cancel);
        addButton(confirm);
    }

    @Override
    protected void drawContent(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        outlinedFill(stack, 1, 0xFFFFFF00, 0xFF << 24);
        font.draw(stack, title, leftPos + 5, topPos + 5, 0xFFFFFF);
    }

    private void cancel(Button button) {
        minecraft.setScreen(this.getLayeredScreen());
    }

    private void confirm(Button button) {
        this.cancel(button);
        Networking.toServer(new C2S_UpdateMemberRoles(member, selected));
    }
}
