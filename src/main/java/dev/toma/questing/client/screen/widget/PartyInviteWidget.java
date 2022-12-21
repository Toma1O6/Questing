package dev.toma.questing.client.screen.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import dev.toma.questing.common.party.PartyInvite;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.List;
import java.util.function.Function;

public class PartyInviteWidget extends ContainerWidget {

    public static final ITextComponent ACCEPT = new TranslationTextComponent("text.questing.accept");
    public static final ITextComponent DECLINE = new TranslationTextComponent("text.questing.decline");

    private static final ITextComponent HEADER = new TranslationTextComponent("text.questing.new_party_invitation").withStyle(TextFormatting.BOLD);
    private static final Function<String, ITextComponent> TEXT = (partyName) -> new TranslationTextComponent("text.questing.invitation_content", partyName);
    private static final Function<String, ITextComponent> SENDER = (senderName) -> new TranslationTextComponent("text.questing.invitation_content.sender", senderName);

    private final FontRenderer font;
    private final PartyInvite invite;
    private boolean senderView;

    public PartyInviteWidget(int x, int y, int width, int height, FontRenderer font, PartyInvite invite) {
        super(x, y, width, height, StringTextComponent.EMPTY);
        this.font = font;
        this.invite = invite;
    }

    public void setSenderView(boolean isSenderView) {
        this.senderView = isSenderView;
    }

    @Override
    public void renderButton(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        if (this.senderView) {
            // TODO
        } else {
            this.font.draw(stack, HEADER, x + 3, y + 3, 0xFFFFFF);
            this.font.draw(stack, SENDER.apply(invite.getSenderName()), x + 3, y + height - 13, 0xFFFFFF);
            List<IReorderingProcessor> content = font.split(TEXT.apply(invite.getPartyName()), width - 10);
            for (int i = 0; i < Math.min(content.size(), 2); i++) {
                IReorderingProcessor processor = content.get(i);
                this.font.draw(stack, processor, x + 3, y + 13 + i * 10, 0xFFFFFF);
            }
        }
        fill(stack, x, y + height - 1, x + width, y + height, 0xFFFFFFFF);
        if (isHovered) {
            fill(stack, x, y, x + width, y + height, 0x44FFFFFF);
        }
        super.renderButton(stack, mouseX, mouseY, partialTicks);
    }
}
