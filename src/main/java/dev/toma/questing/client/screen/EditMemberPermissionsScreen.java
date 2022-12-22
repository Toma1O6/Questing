package dev.toma.questing.client.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import dev.toma.questing.common.party.Party;
import dev.toma.questing.common.party.PartyPermission;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.button.CheckboxButton;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.UUID;

public class EditMemberPermissionsScreen extends OverlayScreen {

    private final Party party;
    private final UUID member;
    private int permissionsValue;

    public EditMemberPermissionsScreen(Screen parent, Party party, UUID member) {
        super(new TranslationTextComponent("screen.questing.manage_permissions"), parent);
        this.party = party;
        this.member = member;
    }

    @Override
    protected void init() {
        super.init();
        int permissionSize = PartyPermission.ADJUSTABLE_PERMISSIONS.size();
        this.setDimensions(200, 40 + permissionSize * 25);
        int i = 0;
        permissionsValue = 0;
        for (PartyPermission permission : PartyPermission.ADJUSTABLE_PERMISSIONS) {
            boolean active = this.party.isAuthorized(permission, member);
            if (active) {
                permissionsValue |= permission.getAsInt();
            }
            ITextComponent label = new TranslationTextComponent("permission.questing." + permission.name().toLowerCase());
            addButton(new CheckboxButton(leftPos + 5, topPos + 15 + i++ * 25, innerWidth - 10, 20, label, active) {
                @Override
                public void onPress() {
                    super.onPress();
                    EditMemberPermissionsScreen.this.permissionsValue |= active ? permission.getAsInt() : -permission.getAsInt();
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
        // TODO send packet
    }
}
