package me.Cooltimmetje.Skuddbot.Commands.Useless;

import me.Cooltimmetje.Skuddbot.Minigames.Challenge.ChallengeHandler;
import me.Cooltimmetje.Skuddbot.Profiles.*;
import me.Cooltimmetje.Skuddbot.Utilities.Constants;
import me.Cooltimmetje.Skuddbot.Utilities.MessagesUtils;
import me.Cooltimmetje.Skuddbot.Utilities.MiscUtils;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

import java.util.ArrayList;

/**
 * Picks an random active user, and hugs it.
 *
 * @author Tim (Cooltimmetje)
 * @version v0.4.51-ALPHA
 * @since v0.4.32-ALPHA
 */
public class HugCommand {

    @SuppressWarnings("Duplicates")
    public static void run(IMessage message){
        ProfileManager.getDiscord(message.getAuthor().getStringID(), message.getGuild().getStringID(), true);
        IGuild guild = message.getGuild();
        Server server = ServerManager.getServer(guild.getStringID());
        IChannel channel = message.getChannel();
        IUser user = message.getAuthor();
        ChallengeHandler challengeHandler = server.getChallengeHandler();
        IUser randomUser;

        if(!challengeHandler.targetPunch.containsKey(user)) {
            ArrayList<Long> activeUsers = MiscUtils.gatherActiveUsers(server);

            if (activeUsers.size() <= 1) {
                MySqlManager.getTopDiscord(guild.getStringID());
                activeUsers = MiscUtils.gatherActiveUsers(server);
            }

            do {
                randomUser = message.getGuild().getUserByID(activeUsers.get(MiscUtils.randomInt(0, activeUsers.size() - 1)));
            } while (user == randomUser);
        } else {
            randomUser = challengeHandler.targetPunch.get(user);
        }

        SkuddUser pickedUser = ProfileManager.getDiscord(randomUser.getStringID(), guild.getStringID(), true);
        String userMention = pickedUser.isMentionMe() ? randomUser.mention() : randomUser.getDisplayName(guild);

        if(user.getLongID() == Constants.JASCH_ID){
            MessagesUtils.sendPlain("FUCK YOU " + userMention.toUpperCase(), channel, false);
            return;
        }

        MessagesUtils.sendPlain("*hugs " + userMention + "*", channel, false);
    }

}
