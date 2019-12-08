package me.Cooltimmetje.Skuddbot.Commands.Admin.SuperAdmin;

import discord4j.core.object.entity.Message;
import me.Cooltimmetje.Skuddbot.Enums.EmojiEnum;
import me.Cooltimmetje.Skuddbot.Profiles.MySqlManager;
import me.Cooltimmetje.Skuddbot.Profiles.ProfileManager;
import me.Cooltimmetje.Skuddbot.Profiles.ServerManager;
import me.Cooltimmetje.Skuddbot.Utilities.Constants;
import me.Cooltimmetje.Skuddbot.Utilities.Logger;
import me.Cooltimmetje.Skuddbot.Utilities.MessagesUtils;

import java.text.MessageFormat;

/**
 * This class is used to save profiles and other stuff to the database.
 *
 * @author Tim (Cooltimmetje)
 * @version v0.5.1-ALPHA
 * @since v0.1-ALPHA
 */

//TODO: REWRITE
public class SaveProfile {

    /**
     * CMD: Force saving data to the database.
     *
     * @param message The message that this command got triggered off.
     */
    public static void run(Message message){
        long startTime = System.currentTimeMillis();

        if(message.getAuthor().get().getId().asLong() == Constants.TIMMY_ID){
            if(message.getUserMentions().collectList().block().isEmpty()){
                if(message.getContent().get().split(" ").length > 1){
                    if(message.getContent().get().split(" ")[1].equalsIgnoreCase("-all")){
                        ServerManager.saveAll();
                        MessagesUtils.addReaction(message,"All profiles saved! (" + (System.currentTimeMillis() - startTime) + " ms)", EmojiEnum.WHITE_CHECK_MARK);
                    } else if (message.getContent().get().split(" ")[1].equalsIgnoreCase("-loadtop")) {
                        MySqlManager.getTopDiscord(message.getGuild().block().getId().asString());
                        MySqlManager.getTopTwitch(message.getGuild().block().getId().asString());
                        MessagesUtils.addReaction(message, "Successfully loaded the top 10 for both Discord and Twitch into memory! (" + (System.currentTimeMillis() - startTime) + " ms)", EmojiEnum.WHITE_CHECK_MARK);
                    } else if (message.getContent().get().split(" ")[1].equalsIgnoreCase("-saveservers")) {
                        ServerManager.saveAll();
                        MessagesUtils.addReaction(message, "Saved all servers! (" + (System.currentTimeMillis() - startTime) + " ms)", EmojiEnum.WHITE_CHECK_MARK);
                    } else {
                        MySqlManager.saveProfile(ProfileManager.getDiscord(message.getAuthor().get().asMember(message.getGuild().block().getId()).block(), true));
                        MessagesUtils.addReaction(message, MessageFormat.format("Saved profile for user **{0}** with id `{1}`! (" + (System.currentTimeMillis() - startTime) + " ms)", message.getAuthor().get().getUsername(),message.getAuthor().get().getId().asString()), EmojiEnum.WHITE_CHECK_MARK);
                    }
                }
            } else {
                MySqlManager.saveProfile(ProfileManager.getDiscord(message.getAuthor().get().asMember(message.getGuild().block().getId()).block(), true));
                MessagesUtils.addReaction(message, MessageFormat.format("Saved profile for user **{0}** with id `{1}`! (" + (System.currentTimeMillis() - startTime) + " ms)", message.getUserMentions().collectList().block().get(0).getUsername(), message.getUserMentions().collectList().block().get(0).getId().asString()), EmojiEnum.WHITE_CHECK_MARK);
            }
        } else {
            Logger.info(message.getAuthor().get().getUsername() + " attempted to do something they don't have permission for.");
        }

    }

}
