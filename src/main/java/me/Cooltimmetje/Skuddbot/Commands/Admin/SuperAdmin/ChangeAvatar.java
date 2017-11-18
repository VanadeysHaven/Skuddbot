package me.Cooltimmetje.Skuddbot.Commands.Admin.SuperAdmin;

import me.Cooltimmetje.Skuddbot.Main;
import me.Cooltimmetje.Skuddbot.Utilities.Constants;
import me.Cooltimmetje.Skuddbot.Utilities.MessagesUtils;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.Image;
import sx.blah.discord.util.RateLimitException;

/**
 * This allows the avatar of the bot to be changed!
 *
 * @author Tim (Cooltimmetje)
 * @version v0.4.0.1-ALPHA-DEV
 * @since v0.3-ALPHA
 */
public class ChangeAvatar {

    public static void run(IMessage message){
        if(Constants.adminUser.contains(message.getAuthor().getStringID())){
            String[] args = message.getContent().split(" ");

            if(args.length > 1){
                if(!args[1].endsWith(".png")){
                    MessagesUtils.sendError("The URL must end with .png", message.getChannel());
                    return;
                }
                try {
                    Main.getInstance().getSkuddbot().changeAvatar(Image.forUrl("png", args[1]));

                    MessagesUtils.sendSuccess("Changed image!", message.getChannel());
                } catch (DiscordException e) {
                    e.printStackTrace();
                    MessagesUtils.sendError("That didn't work. Try again.", message.getChannel());
                } catch (RateLimitException e) {
                    e.printStackTrace();
                    MessagesUtils.sendError("You got rate limited, try again later.", message.getChannel());
                }
            } else {
                MessagesUtils.sendError("Please enter a URL to change the avatar to. (URL must end in '.png')", message.getChannel());
            }
        }
    }

}
