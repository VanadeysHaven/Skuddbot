package me.Cooltimmetje.Skuddbot.Commands.Admin;

import me.Cooltimmetje.Skuddbot.Listeners.CreateServerListener;
import me.Cooltimmetje.Skuddbot.Profiles.MySqlManager;
import me.Cooltimmetje.Skuddbot.Utilities.Constants;
import me.Cooltimmetje.Skuddbot.Utilities.Logger;
import me.Cooltimmetje.Skuddbot.Utilities.MessagesUtils;
import sx.blah.discord.handle.obj.IMessage;

/**
 * Created by Tim on 10/3/2016.
 */
public class LoadAuth {

    public static void run(IMessage message){
        if (message.getAuthor().getID().equals(Constants.TIMMY_OVERRIDE)){
            CreateServerListener.authorized.clear();
            MySqlManager.loadAuth();
            MessagesUtils.sendSuccess("Authorized Servers reloaded.", message.getChannel());
        } else {
            Logger.info(message.getAuthor().getName() + " attempted to do something they don't have permission for.");
        }
    }

}
