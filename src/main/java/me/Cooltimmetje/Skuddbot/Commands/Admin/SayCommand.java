package me.Cooltimmetje.Skuddbot.Commands.Admin;

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
 * @version v0.2-ALPHA
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
        if(message.getAuthor().getID().equals(Constants.TIMMY_OVERRIDE) || message.getAuthor().getID().equals(Constants.JASCH_OVERRIDE)){
            String[] args = message.getContent().split(" ");
            if (args.length > 1) {
                IChannel channel = Main.getInstance().getSkuddbot().getChannelByID(args[1]);
                if(channel != null){
                    channels.put(message.getAuthor().getID(), channel.getID());
                    MessagesUtils.sendSuccess("Channel set to " + channel.mention() + " in **" + channel.getGuild().getName() + "** `(ID: " + channel.getGuild().getID() + ")`!\n" +
                            "You may now send messages to that channel using the `!say` command in PM.", message.getChannel());
                } else {
                    MessagesUtils.sendError("Well... that channel doesn't exist, or I'm not in that server!", message.getChannel());
                }
            } else {
                MessagesUtils.sendError("You cunt... I need a fucking channel to do this... <.<", message.getChannel());
            }
        }
    }

    /**
     * http://thecodinglove.com/post/132136853097/intern-trying-to-deploy
     *
     * @param message SPICY MEMES!
     */
    public static void sayMessage(IMessage message){
        if(message.getAuthor().getID().equals(Constants.TIMMY_OVERRIDE) || message.getAuthor().getID().equals(Constants.JASCH_OVERRIDE)){
            String[] args = message.getContent().split(" ");
            if (args.length > 1) {
                IChannel channel = Main.getInstance().getSkuddbot().getChannelByID(channels.get(message.getAuthor().getID()));
                if(channel != null){
                    StringBuilder sb = new StringBuilder();
                    for(int i=1; i<args.length; i++){
                        sb.append(args[i]).append(" ");
                    }
                    MessagesUtils.sendPlain(sb.toString().trim(), channel);
                    MessagesUtils.sendSuccess(":mailbox_with_mail: " + channel.mention(), message.getChannel());
                } else {
                    MessagesUtils.sendError("The channel that you set isn't a thingy anymore O.o (ID: " + channels.get(message.getAuthor().getID()) + ")", message.getChannel());
                }
            } else {
                MessagesUtils.sendError("Err... what would you like to say?", message.getChannel());
            }
        }
    }

}
