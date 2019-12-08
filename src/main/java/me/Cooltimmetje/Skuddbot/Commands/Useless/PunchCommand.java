package me.Cooltimmetje.Skuddbot.Commands.Useless;

import discord4j.core.object.entity.*;
import discord4j.core.object.util.Snowflake;
import me.Cooltimmetje.Skuddbot.Minigames.Challenge.ChallengeHandler;
import me.Cooltimmetje.Skuddbot.Profiles.*;
import me.Cooltimmetje.Skuddbot.Utilities.Constants;
import me.Cooltimmetje.Skuddbot.Utilities.MessagesUtils;
import me.Cooltimmetje.Skuddbot.Utilities.MiscUtils;

import java.util.ArrayList;

/**
 * Picks an random active user, and punches it.
 *
 * @author Tim (Cooltimmetje)
 * @version v0.5.1-ALPHA
 * @since v0.4.32-ALPHA
 */
public class PunchCommand {

    @SuppressWarnings("Duplicates")
    public static void run(Message message){
        ProfileManager.getDiscord(message.getAuthor().get(), message.getGuild().block(), true);
        Guild guild = message.getGuild().block();
        Server server = ServerManager.getServer(guild.getId().asString());
        MessageChannel channel = message.getChannel().block();
        User user = message.getAuthor().get();
        ChallengeHandler challengeHandler = server.getChallengeHandler();
        Member randomUser;

        if(!challengeHandler.targetPunch.containsKey(user)) {
            ArrayList<Long> activeUsers = MiscUtils.gatherActiveUsers(server);

            if (activeUsers.size() <= 1) {
                MySqlManager.getTopDiscord(guild.getId().asString());
                activeUsers = MiscUtils.gatherActiveUsers(server);
            }

            do {
                randomUser = message.getGuild().block().getMemberById(Snowflake.of(activeUsers.get(MiscUtils.randomInt(0, activeUsers.size() - 1)))).block();
            } while (user == randomUser || randomUser == null);
        } else {
            randomUser = challengeHandler.targetPunch.get(user);
        }

        SkuddUser pickedUser = ProfileManager.getDiscord(randomUser.getId().asString(), guild.getId().asString(), true);
        String userMention = pickedUser.isMentionMe() ? randomUser.getMention() : randomUser.getDisplayName();

        if(user.getId().asLong() == Constants.JASCH_ID){
            MessagesUtils.sendPlain("*lights " + userMention + " on fire*", channel, false);
            return;
        }
        if(user.getId().asLong() == 131382094457733120L){
            MessagesUtils.sendPlain("*" + user.asMember(guild.getId()).block().getDisplayName() + " unleashes his lightning punch on " + userMention + "* :zap:" , channel, false);
            return;
        }

        MessagesUtils.sendPlain("*punches " + userMention + "*", channel, false);
    }

}
