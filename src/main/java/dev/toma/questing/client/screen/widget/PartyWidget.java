package dev.toma.questing.client.screen.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import dev.toma.questing.Questing;
import dev.toma.questing.client.screen.InviteToPartyScreen;
import dev.toma.questing.client.screen.ManagePartyScreen;
import dev.toma.questing.client.screen.PartyInvitesScreen;
import dev.toma.questing.common.data.PartyData;
import dev.toma.questing.common.data.PlayerDataProvider;
import dev.toma.questing.common.party.Party;
import dev.toma.questing.common.party.PartyInvite;
import dev.toma.questing.common.party.PartyPermission;
import dev.toma.questing.utils.Alignment;
import dev.toma.questing.utils.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public final class PartyWidget extends ContainerWidget {

    public static final ResourceLocation INVITE_ICON = new ResourceLocation(Questing.MODID, "textures/ui/invite.png");
    public static final ResourceLocation INVITES_ICON = new ResourceLocation(Questing.MODID, "textures/ui/invites.png");
    public static final ResourceLocation INVITES_ACTIVE_ICON = new ResourceLocation(Questing.MODID, "textures/ui/invites_active.png");
    public static final ResourceLocation MANAGE_ICON = new ResourceLocation(Questing.MODID, "textures/ui/manage_group.png");
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
        PlayerEntity player = Minecraft.getInstance().player;
        List<UUID> members = party.getMembersSortedByRoles();
        UUID owner = party.getOwner();
        boolean canInvite = party.isAuthorized(PartyPermission.INVITE_PLAYERS, player.getUUID()) && party.canAddNewMember();
        int buttonIndex = 0;
        ImageButton myInvitesButton = addWidget(new ImageButton(x + width - (buttonIndex++ * 20), y, 20, 20, INVITES_ICON, this::partyInvitesClicked));
        addWidget(new ImageButton(x + width - (buttonIndex++ * 20), y, 20, 20, MANAGE_ICON, this::managePartyButtonClicked));
        if (canInvite) {
            addWidget(new ImageButton(x + width - (buttonIndex++ * 20), y, 20, 20, INVITE_ICON, this::inviteButtonClicked));
        }
        if (members.size() > this.maxDisplayedPlayerCount) {
            PlayerProfileWidget widget = addWidget(new PlayerProfileWidget(x + width - (buttonIndex * 20), y, 18, 18, owner));
            widget.setFrame(1, 0xFFFFFF00);
            widget.forceTooltipText(party.getMemberUsername(owner));
            widget.showOnlineStatus(true);
        } else {
            int index = 0;
            for (UUID member : members) {
                int left = x + width - buttonIndex++ * 20 - index++ * 5;
                boolean isOwner = member.equals(owner);
                PlayerProfileWidget widget = addWidget(new PlayerProfileWidget(left, y, 18, 18, member));
                widget.setFrame(1, isOwner ? 0xFFFFFF00 : 0xFF888888);
                widget.forceTooltipText(party.getMemberUsername(member));
                widget.showOnlineStatus(true);
            }
        }
        PlayerDataProvider.getOptional(player).ifPresent(data -> {
            PartyData partyData = data.getPartyData();
            Set<PartyInvite> myInvites = partyData.getMyInvites();
            if (myInvites.size() > 0) {
                myInvitesButton.setImage(INVITES_ACTIVE_ICON);
            }
        });
    }

    private void managePartyButtonClicked(Button button) {
        Minecraft.getInstance().setScreen(new ManagePartyScreen(parentScreen, party));
    }

    private void inviteButtonClicked(Button button) {
        Minecraft.getInstance().setScreen(new InviteToPartyScreen(parentScreen, party));
    }

    private void partyInvitesClicked(Button button) {
        Minecraft.getInstance().setScreen(new PartyInvitesScreen(parentScreen));
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
