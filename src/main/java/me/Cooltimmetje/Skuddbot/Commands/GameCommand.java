package me.Cooltimmetje.Skuddbot.Commands;

import me.Cooltimmetje.Skuddbot.Main;
import me.Cooltimmetje.Skuddbot.Utilities.Constants;
import me.Cooltimmetje.Skuddbot.Utilities.Logger;
import me.Cooltimmetje.Skuddbot.Utilities.MessagesUtils;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

/**
 * This command changes the playing status of Skuddbot.
 *
 * @author Tim (Cooltimmetje)
 * @version v0.4.1-ALPHA
 * @since v0.1-ALPHA
 */
public class GameCommand {

    /**
     * CMD: This command will change the "playing" status of Skuddbot to whatever gets specified, only available to Awesome users.
     *
     * @param message This is the message that ran the command, and contains the arguments.
     */
    public static void run(IMessage message){
        String msgContent = message.getContent(); //Message content.
        IChannel channel = message.getChannel(); //Channel to send the confirmation message to
        IUser user = message.getAuthor(); //User that sent the message - Used to check the permissions.

        if(!Constants.awesomeUser.contains(user.getStringID())){ //Actual permission check
            Logger.info(user.getName() + " attempted to do something they don't have permission for.");
        } else {
            if(Constants.EVENT_ACTIVE){
                MessagesUtils.sendError("There is a timed event active: " + Constants.CURRENT_EVENT, message.getChannel());
            } else {
                if(msgContent.split(" ").length >= 2){ //Check arguments - Is there something specified?
                    String[] args = msgContent.split(" ");
                    StringBuilder sb = new StringBuilder();
                    for(int i=1; i<args.length; i++){ //Trim off the !game part
                        sb.append(args[i]).append(" ");
                    }
                    String input = sb.toString().trim();
                    Main.getInstance().getSkuddbot().changePlayingText(input.substring(0, Math.min(input.length(), 128))); //Set the playing status.
                    if(input.length() > 128) { //Check limit - See if we need to display the warning.
                        MessagesUtils.sendSuccess("Game set to: `" + input.substring(0, Math.min(input.length(), 128)) + "`\n " +
                                ":warning: Your message exceeded the __128 character limit__, therefore we have trimmed it down to that limit.", channel);
                    } else {
                        MessagesUtils.sendSuccess("Game set to: `" + input.substring(0, Math.min(input.length(), 128)) + "`", channel);
                    }
                } else {
                    MessagesUtils.sendError("No game specified.", channel);
                }
            }
        }
    }

}
