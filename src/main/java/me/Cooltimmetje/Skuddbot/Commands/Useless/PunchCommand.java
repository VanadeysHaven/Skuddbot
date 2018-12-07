package me.Cooltimmetje.Skuddbot.Commands.Useless;

import me.Cooltimmetje.Skuddbot.Enums.EmojiEnum;
import me.Cooltimmetje.Skuddbot.Minigames.Challenge.ChallengeHandler;
import me.Cooltimmetje.Skuddbot.Profiles.ProfileManager;
import me.Cooltimmetje.Skuddbot.Profiles.Server;
import me.Cooltimmetje.Skuddbot.Profiles.ServerManager;
import me.Cooltimmetje.Skuddbot.Profiles.SkuddUser;
import me.Cooltimmetje.Skuddbot.Utilities.Constants;
import me.Cooltimmetje.Skuddbot.Utilities.MessagesUtils;
import me.Cooltimmetje.Skuddbot.Utilities.MiscUtils;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

import java.util.ArrayList;

/**
 * Picks an random active user, and punches it.
 *
 * @author Tim (Cooltimmetje)
 * @version v0.4.41-ALPHA
 * @since v0.4.32-ALPHA
 */
public class PunchCommand {

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
            int activeDelay = 24 * 60 * 60 * 1000;
            ArrayList<Long> activeUsers = new ArrayList<>();
            for (Long userid : server.lastSeen.keySet()) {
                if ((System.currentTimeMillis() - server.lastSeen.get(userid) < activeDelay)) {
                    activeUsers.add(userid);
                }
            }

            if (activeUsers.size() <= 1) {
                MessagesUtils.addReaction(message, "There are no users to be punched.", EmojiEnum.X);
                return;
            }


            do {
                randomUser = message.getGuild().getUserByID(activeUsers.get(MiscUtils.randomInt(0, activeUsers.size() - 1)));
            } while (user == randomUser || randomUser == null);
        } else {
            randomUser = challengeHandler.targetPunch.get(user);
        }

        SkuddUser pickedUser = ProfileManager.getDiscord(randomUser.getStringID(), guild.getStringID(), true);
        String userMention = pickedUser.isMentionMe() ? randomUser.mention() : randomUser.getDisplayName(guild);

        if(user.getLongID() == Constants.JASCH_ID){
            MessagesUtils.sendPlain("*lights " + userMention + " on fire*", channel, false);
            return;
        }

        MessagesUtils.sendPlain("*punches " + userMention + "*", channel, false);
    }

}
