package me.Cooltimmetje.Skuddbot.Commands.Admin.SuperAdmin;

import me.Cooltimmetje.Skuddbot.Enums.EmojiEnum;
import me.Cooltimmetje.Skuddbot.Main;
import me.Cooltimmetje.Skuddbot.Utilities.Constants;
import me.Cooltimmetje.Skuddbot.Utilities.MessagesUtils;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;

import java.util.HashMap;

/**
 * http://thecodinglove.com/post/153951828532/git-push-origin-master-force
 *
 * @author Tim (Cooltimmetje)
 * @version v0.4.32-ALPHA
 * @since v0.2-ALPHA
 */
public class SayCommand {

    /**
     * http://thecodinglove.com/post/132938007788/when-i-edit-something-directly-in-production
     */
    private static HashMap<String, String> channels = new HashMap<>();

    /**
     * http://thecodinglove.com/post/133536369541/getting-a-support-call-during-the-middle-of-the
     *
     * @param message MEMES?!
     */
    public static void setChannel(IMessage message){
        if(message.getAuthor().getLongID() == Constants.TIMMY_ID || message.getAuthor().getLongID() == Constants.JASCH_ID){
            String[] args = message.getContent().split(" ");
            if (args.length > 1) {
                IChannel channel = Main.getInstance().getSkuddbot().getChannelByID(Long.parseLong(args[1]));
                if(channel != null){
                    channels.put(message.getAuthor().getStringID(), channel.getStringID());
                    MessagesUtils.addReaction(message,"Channel set to " + channel.mention() + " in **" + channel.getGuild().getName() + "** `(ID: " + channel.getGuild().getStringID() + ")`!\n" +
                            "You may now send messages to that channel using the `!say` command in PM.", EmojiEnum.WHITE_CHECK_MARK);
                } else {
                    MessagesUtils.addReaction(message,"Well... that channel doesn't exist, or I'm not in that server!", EmojiEnum.X);
                }
            } else {
                MessagesUtils.addReaction(message,"You cunt... I need a fucking channel to do this... <.<", EmojiEnum.X);
            }
        }
    }

    /**
     * http://thecodinglove.com/post/132136853097/intern-trying-to-deploy
     *
     * @param message SPICY MEMES!
     */
    public static void sayMessage(IMessage message){
        if(message.getAuthor().getLongID() == Constants.TIMMY_ID || message.getAuthor().getLongID() == Constants.JASCH_ID){
            String[] args = message.getContent().split(" ");
            if (args.length > 1) {
                IChannel channel = Main.getInstance().getSkuddbot().getChannelByID(Long.parseLong(channels.get(message.getAuthor().getStringID())));
                if(channel != null){
                    StringBuilder sb = new StringBuilder();
                    for(int i=1; i<args.length; i++){
                        sb.append(args[i]).append(" ");
                    }
                    MessagesUtils.sendPlain(sb.toString().trim(), channel, true);
                    MessagesUtils.addReaction(message,":mailbox_with_mail: " + channel.mention(), EmojiEnum.MAILBOX_WITH_MAIL);
                } else {
                    MessagesUtils.addReaction(message,"The channel that you set doesn't a exist anymore O.o (ID: " + channels.get(message.getAuthor().getStringID()) + ")", EmojiEnum.X);
                }
            } else {
                MessagesUtils.addReaction(message,"Err... what would you like to say?", EmojiEnum.X);
            }
        }
    }

}
