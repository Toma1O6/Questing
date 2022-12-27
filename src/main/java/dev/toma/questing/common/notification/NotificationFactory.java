package dev.toma.questing.common.notification;

import dev.toma.questing.common.party.Party;
import dev.toma.questing.common.party.PartyInvite;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.UUID;

public final class NotificationFactory {

    // Static components
    private static final ITextComponent INVITE_HEADER = new TranslationTextComponent("text.questing.notification.invited_me.header").withStyle(TextFormatting.BOLD);
    private static final ITextComponent INVITE_SENT_HEADER = new TranslationTextComponent("text.questing.notification.invited.header").withStyle(TextFormatting.BOLD);
    private static final ITextComponent INVITE_CANCELLED_HEADER = new TranslationTextComponent("text.questing.notification.invite_cancelled.header").withStyle(TextFormatting.BOLD);
    private static final ITextComponent PARTY_RENAMED_HEADER = new TranslationTextComponent("text.questing.notification.party_rename.header").withStyle(TextFormatting.BOLD);
    private static final ITextComponent PARTY_DISBANDED_HEADER = new TranslationTextComponent("text.questing.notification.party_disbanded.header").withStyle(TextFormatting.BOLD);
    private static final ITextComponent INVITE_ACCEPTED_HEADER = new TranslationTextComponent("text.questing.notification.invite_accepted.header").withStyle(TextFormatting.BOLD);
    private static final ITextComponent INVITE_DECLINED_HEADER = new TranslationTextComponent("text.questing.notification.invite_declined.header").withStyle(TextFormatting.BOLD);
    private static final ITextComponent MEMBER_LEFT_HEADER = new TranslationTextComponent("text.questing.notification.member_left.header").withStyle(TextFormatting.BOLD);
    private static final ITextComponent MEMBER_KICKED_HEADER = new TranslationTextComponent("text.questing.notification.member_kicked.header").withStyle(TextFormatting.BOLD);
    private static final ITextComponent KICKED_HEADER = new TranslationTextComponent("text.questing.notification.kicked.header").withStyle(TextFormatting.BOLD);

    // Dynamic localization keys
    // Party name, Sender name
    private static final String INVITE_CONTENT = "text.questing.notification.invited_me.content";
    // Invitee name, Sender name
    private static final String INVITE_SENT_CONTENT = "text.questing.notification.invited.content";
    // Party name
    private static final String INVITE_CANCELLED_CONTENT = "text.questing.notification.invite_cancelled.content";
    // Old name, new name
    private static final String PARTY_RENAMED_CONTENT = "text.questing.notification.party_rename.content";
    // Party name
    private static final String PARTY_DISBANDED_CONTENT = "text.questing.notification.party_disbanded.content";
    // Player name
    private static final String INVITE_ACCEPTED_CONTENT = "text.questing.notification.invite_accepted.content";
    // Player name
    private static final String INVITE_DECLINED_CONTENT = "text.questing.notification.invite_declined.content";
    // Player name
    private static final String MEMBER_LEFT_CONTENT = "text.questing.notification.member_left.content";
    // Player name, Kicked by name
    private static final String MEMBER_KICKED_CONTENT = "text.questing.notification.member_kicked.content";
    // Party name
    private static final String KICKED_CONTENT = "text.questing.notification.kicked.content";

    public static Notification getInviteNotification(PartyInvite invite) {
        return new Notification.Builder()
                .icon(NotificationIcon.skin(invite.getInviteSentById()))
                .header(INVITE_HEADER)
                .addContentSlide(new TranslationTextComponent(INVITE_CONTENT, invite.getPartyName(), invite.getSenderName()))
                .buildNotification();
    }

    public static Notification getInviteSentNotification(PartyInvite invite) {
        return new Notification.Builder()
                .icon(NotificationIcon.skin(invite.getInviteSentById()))
                .header(INVITE_SENT_HEADER)
                .addContentSlide(new TranslationTextComponent(INVITE_SENT_CONTENT, invite.getInvitedName(), invite.getSenderName()))
                .buildNotification();
    }

    public static Notification getInviteCancelledNotification(PartyInvite invite) {
        return new Notification.Builder()
                .icon(NotificationIcon.skin(invite.getInviteSentById()))
                .header(INVITE_CANCELLED_HEADER)
                .addContentSlide(new TranslationTextComponent(INVITE_CANCELLED_CONTENT, invite.getPartyName()))
                .buildNotification();
    }

    public static Notification getPartyRenameNotification(String oldName, String newName) {
        return new Notification.Builder()
                .header(PARTY_RENAMED_HEADER)
                .addContentSlide(new TranslationTextComponent(PARTY_RENAMED_CONTENT, oldName, newName))
                .buildNotification();
    }

    public static Notification getPartyDisbandedNotification(String partyName) {
        return new Notification.Builder()
                .header(PARTY_DISBANDED_HEADER)
                .addContentSlide(new TranslationTextComponent(PARTY_DISBANDED_CONTENT, partyName))
                .buildNotification();
    }

    public static Notification getInviteResponseNotification(PartyInvite invite, boolean accepted) {
        return new Notification.Builder()
                .icon(NotificationIcon.skin(invite.getInviteeId()))
                .header(accepted ? INVITE_ACCEPTED_HEADER : INVITE_DECLINED_HEADER)
                .addContentSlide(new TranslationTextComponent(accepted ? INVITE_ACCEPTED_CONTENT : INVITE_DECLINED_CONTENT, invite.getInvitedName()))
                .buildNotification();
    }

    public static Notification getMemberLeftNotification(UUID playerId, String playerName) {
        return new Notification.Builder()
                .icon(NotificationIcon.skin(playerId))
                .header(MEMBER_LEFT_HEADER)
                .addContentSlide(new TranslationTextComponent(MEMBER_LEFT_CONTENT, playerName))
                .buildNotification();
    }

    public static Notification getMemberKickedNotification(Party party, String member, UUID kickedBy) {
        return new Notification.Builder()
                .icon(NotificationIcon.skin(kickedBy))
                .header(MEMBER_KICKED_HEADER)
                .addContentSlide(new TranslationTextComponent(MEMBER_KICKED_CONTENT, member, party.getMemberUsername(kickedBy)))
                .buildNotification();
    }

    public static Notification getKickedNotification(String partyName, UUID kickedBy) {
        return new Notification.Builder()
                .icon(NotificationIcon.skin(kickedBy))
                .header(KICKED_HEADER)
                .addContentSlide(new TranslationTextComponent(KICKED_CONTENT, partyName))
                .buildNotification();
    }
}
