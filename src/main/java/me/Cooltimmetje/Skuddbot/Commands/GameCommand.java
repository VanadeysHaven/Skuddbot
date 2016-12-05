package me.Cooltimmetje.Skuddbot.Commands;

import me.Cooltimmetje.Skuddbot.Main;
import me.Cooltimmetje.Skuddbot.Utilities.Logger;
import me.Cooltimmetje.Skuddbot.Utilities.MessagesUtils;
import sx.blah.discord.handle.obj.*;

/**
 * Created by Tim on 8/2/2016.
 */
public class GameCommand {

    /**
     * This command will change the "playing" status of Skuddbot to whatever gets specified, not avaialble to everyone.
     *
     * @param message This is the message that ran the command, and contains the arguments.
     */

    public static void run(IMessage message){
        String msgContent = message.getContent(); //Message content.
        IChannel channel = message.getChannel(); //Channel to send the confirmation message to
        IUser user = message.getAuthor(); //User that sent the message - Used to check the permissions.
        IGuild rayGuild = Main.getInstance().getSkuddbot().getGuildByID("157774629975490561"); //Instance of Ray's server - Used to check perms

        if(!rayGuild.getUsers().contains(user)){ //If they are not in Ray's server we can assume they do not have permission.
            Logger.info(user.getName() + " attempted to do something they don't have permission for.");
            return;
        }
        if(!user.getRolesForGuild(rayGuild).contains(rayGuild.getRolesByName("Bot Testers").get(0))){ //Actual permission check
            Logger.info(user.getName() + " attempted to do something they don't have permission for.");
        } else {
            if(msgContent.split(" ").length >= 2){ //Check arguments - Is there something specified?
                String[] args = msgContent.split(" ");
                StringBuilder sb = new StringBuilder();
                for(int i=1; i<args.length; i++){ //Trim off the !game part
                    sb.append(args[i]).append(" ");
                }
                String input = sb.toString().trim();
                Main.getInstance().getSkuddbot().changeStatus(Status.game(input.substring(0, Math.min(input.length(), 128)))); //Set the playing status.
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
