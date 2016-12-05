package me.Cooltimmetje.Skuddbot.Commands.Admin;

import me.Cooltimmetje.Skuddbot.Profiles.MySqlManager;
import me.Cooltimmetje.Skuddbot.Utilities.Constants;
import me.Cooltimmetje.Skuddbot.Utilities.Logger;
import me.Cooltimmetje.Skuddbot.Utilities.MessagesUtils;
import sx.blah.discord.handle.obj.IMessage;

/**
 * Created by Tim on 10/2/2016.
 */
public class ReloadGlobal {

    public static void run(IMessage message){
        if (message.getAuthor().getID().equals(Constants.TIMMY_OVERRIDE)){
            Constants.config.clear();
            MySqlManager.loadGlobal();
            MessagesUtils.sendSuccess("Global config reloaded!", message.getChannel());
        } else {
            Logger.info(message.getAuthor().getName() + " attempted to do something they don't have permission for.");
        }
    }

}
