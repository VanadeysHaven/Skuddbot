package me.Cooltimmetje.Skuddbot.Commands;

import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.MessageChannel;
import discord4j.core.object.entity.User;
import discord4j.core.object.presence.Activity;
import discord4j.core.object.presence.Presence;
import me.Cooltimmetje.Skuddbot.Enums.EmojiEnum;
import me.Cooltimmetje.Skuddbot.Main;
import me.Cooltimmetje.Skuddbot.Utilities.Constants;
import me.Cooltimmetje.Skuddbot.Utilities.Logger;
import me.Cooltimmetje.Skuddbot.Utilities.MessagesUtils;

/**
 * This command changes the playing status of Skuddbot.
 *
 * @author Tim (Cooltimmetje)
 * @version v0.5.1-ALPHA
 * @since v0.1-ALPHA
 */
public class GameCommand {

    /**
     * CMD: This command will change the "playing" status of Skuddbot to whatever gets specified, only available to Awesome users.
     *
     * @param message This is the message that ran the command, and contains the arguments.
     */
    public static void run(Message message){
        String msgContent = message.getContent().get(); //Message content.
        MessageChannel channel = message.getChannel().block(); //Channel to send the confirmation message to
        User user = message.getAuthor().get(); //User that sent the message - Used to check the permissions.

        if(!Constants.awesomeUser.contains(user.getId().asString())){ //Actual permission check
            Logger.info(user.getUsername() + " attempted to do something they don't have permission for.");
        } else {
            if(Constants.EVENT_ACTIVE){
                MessagesUtils.addReaction(message,"There is a timed event active: " + Constants.CURRENT_EVENT, EmojiEnum.X, false);
            } else {
                if(msgContent.split(" ").length >= 2){ //Check arguments - Is there something specified?
                    String[] args = msgContent.split(" ");
                    StringBuilder sb = new StringBuilder();
                    for(int i=1; i<args.length; i++){ //Trim off the !game part
                        sb.append(args[i]).append(" ");
                    }
                    String input = sb.toString().trim();
                    String substring = input.substring(0, Math.min(input.length(), 128));
                    Main.getInstance().getSkuddbot().updatePresence(Presence.online(Activity.playing(substring))).block();
                    if(input.length() > 128) { //Check limit - See if we need to display the warning.
                        MessagesUtils.addReaction(message,"Game set to: `" + substring + "`\n " +
                                ":warning: Your message exceeded the __128 character limit__, therefore we have trimmed it down to that limit.", EmojiEnum.WHITE_CHECK_MARK, false);
                    } else {
                        MessagesUtils.addReaction(message,"Game set to: `" + substring + "`", EmojiEnum.WHITE_CHECK_MARK, false);
                    }
                } else {
                    MessagesUtils.addReaction(message,"No game specified.", EmojiEnum.X, false);
                }
            }
        }
    }

}
