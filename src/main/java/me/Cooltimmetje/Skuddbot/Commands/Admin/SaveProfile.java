package me.Cooltimmetje.Skuddbot.Commands.Admin;

import me.Cooltimmetje.Skuddbot.Profiles.MySqlManager;
import me.Cooltimmetje.Skuddbot.Profiles.ProfileManager;
import me.Cooltimmetje.Skuddbot.Profiles.ServerManager;
import me.Cooltimmetje.Skuddbot.Utilities.Constants;
import me.Cooltimmetje.Skuddbot.Utilities.Logger;
import me.Cooltimmetje.Skuddbot.Utilities.MessagesUtils;
import sx.blah.discord.handle.obj.IMessage;

import java.text.MessageFormat;

/**
 * This class is used to save profiles and other stuff to the database.
 *
 * @author Tim (Cooltimmetje)
 * @version v0.2-ALPHA
 * @since v0.1-ALPHA
 */

//TODO: Revamp this command.
public class SaveProfile {

    /**
     * CMD: Force saving data to the database.
     *
     * @param message The message that this command got triggered off.
     */
    public static void run(IMessage message){
        long startTime = System.currentTimeMillis();

        if(message.getAuthor().getID().equals(Constants.TIMMY_OVERRIDE)){
            if(message.getMentions().isEmpty()){
                if(message.getContent().split(" ").length > 1){
                    if(message.getContent().split(" ")[1].equalsIgnoreCase("-all")){
                        ServerManager.saveAll();
                        MessagesUtils.sendSuccess("All profiles saved! (" + (System.currentTimeMillis() - startTime) + " ms)", message.getChannel());
                    } else if (message.getContent().split(" ")[1].equalsIgnoreCase("-loadtop")) {
                        MySqlManager.getTopDiscord(message.getGuild().getID());
                        MySqlManager.getTopTwitch(message.getGuild().getID());
                        MessagesUtils.sendSuccess(MessageFormat.format("Successfully loaded the top 10 for both Discord and Twitch into memory! (" + (System.currentTimeMillis() - startTime) + " ms)", message.getAuthor().getName(),message.getAuthor().getID()), message.getChannel());
                    } else if (message.getContent().split(" ")[1].equalsIgnoreCase("-saveservers")) {
                        ServerManager.saveAll();
                        MessagesUtils.sendSuccess(MessageFormat.format("Saved all servers! (" + (System.currentTimeMillis() - startTime) + " ms)", message.getAuthor().getName(),message.getAuthor().getID()), message.getChannel());
                    } else {
                        MySqlManager.saveProfile(ProfileManager.getDiscord(message.getAuthor().getID(), message.getGuild().getID(), true));
                        MessagesUtils.sendSuccess(MessageFormat.format("Saved profile for user **{0}** with id `{1}`! (" + (System.currentTimeMillis() - startTime) + " ms)", message.getAuthor().getName(),message.getAuthor().getID()), message.getChannel());
                    }
                }
            } else {
                MySqlManager.saveProfile(ProfileManager.getDiscord(message.getMentions().get(0).getID(), message.getGuild().getID(), true));
                MessagesUtils.sendSuccess(MessageFormat.format("Saved profile for user **{0}** with id `{1}`! (" + (System.currentTimeMillis() - startTime) + " ms)", message.getMentions().get(0).getName(),message.getMentions().get(0).getID()), message.getChannel());
            }
        } else {
            Logger.info(message.getAuthor().getName() + " attempted to do something they don't have permission for.");
        }

    }

}
