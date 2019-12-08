package me.Cooltimmetje.Skuddbot.Commands.Admin.SuperAdmin;

import discord4j.core.object.entity.Message;
import me.Cooltimmetje.Skuddbot.Profiles.MySqlManager;
import me.Cooltimmetje.Skuddbot.Utilities.Constants;
import me.Cooltimmetje.Skuddbot.Utilities.Logger;
import me.Cooltimmetje.Skuddbot.Utilities.MessagesUtils;

/**
 * This class is used for reloading the list of global information.
 *
 * @author Tim (Cooltimmetje)
 * @version v0.5.1-ALPHA
 * @since v0.2-ALPHA
 */
public class ReloadGlobal {

    /**
     * CMD: Reloading the list of global information.
     *
     * @param message The message that the command got triggered off.
     */
    public static void run(Message message){
        if (message.getAuthor().get().getId().asLong() == Constants.TIMMY_ID){
            Constants.config.clear();
            MySqlManager.loadGlobal();
            MessagesUtils.sendSuccess("Global config reloaded!", message.getChannel().block());
        } else {
            Logger.info(message.getAuthor().get().getUsername() + " attempted to do something they don't have permission for.");
        }
    }

}
