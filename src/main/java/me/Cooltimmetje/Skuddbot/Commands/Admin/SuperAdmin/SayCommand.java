package me.Cooltimmetje.Skuddbot.Commands.Admin.SuperAdmin;

import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.MessageChannel;
import discord4j.core.object.entity.TextChannel;
import discord4j.core.object.util.Snowflake;
import me.Cooltimmetje.Skuddbot.Enums.EmojiEnum;
import me.Cooltimmetje.Skuddbot.Main;
import me.Cooltimmetje.Skuddbot.Utilities.Constants;
import me.Cooltimmetje.Skuddbot.Utilities.MessagesUtils;

import java.util.HashMap;

/**
 * http://thecodinglove.com/post/153951828532/git-push-origin-master-force
 *
 * @author Tim (Cooltimmetje)
 * @version v0.5.1-ALPHA
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
    public static void setChannel(Message message){
        if(message.getAuthor().get().getId().asLong() == Constants.TIMMY_ID || message.getAuthor().get().getId().asLong() == Constants.JASCH_ID){
            String[] args = message.getContent().get().split(" ");
            if (args.length > 1) {
                TextChannel channel = (TextChannel) Main.getInstance().getSkuddbot().getChannelById(Snowflake.of(args[1])).block();
                if(channel != null){
                    channels.put(message.getAuthor().get().getId().asString(), channel.getId().asString());
                    MessagesUtils.addReaction(message,"Channel set to " + channel.getMention() + " in **" + channel.getGuild().block().getName() + "** `(ID: " + channel.getGuild().block().getId().asLong() + ")`!\n" +
                            "You may now send messages to that channel using the `!say` command in PM.", EmojiEnum.WHITE_CHECK_MARK, false);
                } else {
                    MessagesUtils.addReaction(message,"Well... that channel doesn't exist, or I'm not in that server!", EmojiEnum.X, false);
                }
            } else {
                MessagesUtils.addReaction(message,"You cunt... I need a fucking channel to do this... <.<", EmojiEnum.X, false);
            }
        }
    }

    /**
     * http://thecodinglove.com/post/132136853097/intern-trying-to-deploy
     *
     * @param message SPICY MEMES!
     */
    public static void sayMessage(Message message){
        if(message.getAuthor().get().getId().asLong() == Constants.TIMMY_ID || message.getAuthor().get().getId().asLong() == Constants.JASCH_ID){
            String[] args = message.getContent().get().split(" ");
            if (args.length > 1) {
                MessageChannel channel = (MessageChannel) Main.getInstance().getSkuddbot().getChannelById(Snowflake.of(channels.get(message.getAuthor().get().getId().asString()))).block();
                if(channel != null){
                    StringBuilder sb = new StringBuilder();
                    for(int i=1; i<args.length; i++){
                        sb.append(args[i]).append(" ");
                    }
                    MessagesUtils.sendPlain(sb.toString().trim(), channel, true);
                    MessagesUtils.addReaction(message,":mailbox_with_mail: " + channel.getMention(), EmojiEnum.MAILBOX_WITH_MAIL, false);
                } else {
                    MessagesUtils.addReaction(message,"The channel that you set doesn't a exist anymore O.o (ID: " + channels.get(message.getAuthor().get().getId().asString()) + ")", EmojiEnum.X, false);
                }
            } else {
                MessagesUtils.addReaction(message,"Err... what would you like to say?", EmojiEnum.X, false);
            }
        }
    }

}
