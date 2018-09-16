package me.Cooltimmetje.Skuddbot.Commands.Admin.SuperAdmin;

import me.Cooltimmetje.Skuddbot.Profiles.MySqlManager;
import me.Cooltimmetje.Skuddbot.Utilities.Constants;
import me.Cooltimmetje.Skuddbot.Utilities.Logger;
import me.Cooltimmetje.Skuddbot.Utilities.MessagesUtils;
import sx.blah.discord.handle.obj.IMessage;

/**
 * This class is used for reloading the list of global information.
 *
 * @author Tim (Cooltimmetje)
 * @version v0.4.32-ALPHA
 * @since v0.2-ALPHA
 */
public class ReloadGlobal {

    /**
     * CMD: Reloading the list of global information.
     *
     * @param message The message that the command got triggered off.
     */
    public static void run(IMessage message){
        if (message.getAuthor().getLongID() == Constants.TIMMY_ID){
            Constants.config.clear();
            MySqlManager.loadGlobal();
            MessagesUtils.sendSuccess("Global config reloaded!", message.getChannel());
        } else {
            Logger.info(message.getAuthor().getName() + " attempted to do something they don't have permission for.");
        }
    }

}
