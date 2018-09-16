package me.Cooltimmetje.Skuddbot.Commands.Useless;

import me.Cooltimmetje.Skuddbot.Enums.EmojiEnum;
import me.Cooltimmetje.Skuddbot.Profiles.Server;
import me.Cooltimmetje.Skuddbot.Profiles.ServerManager;
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
 * @version v0.4.32-ALPHA
 * @since v0.4.32-ALPHA
 */
public class HugCommand {

    public static void run(IMessage message){
        IGuild guild = message.getGuild();
        Server server = ServerManager.getServer(guild.getStringID());
        IChannel channel = message.getChannel();
        IUser user = message.getAuthor();

        int activeDelay = 24 * 60 * 60 * 1000;
        ArrayList<Long> activeUsers = new ArrayList<>(server.lastSeen.keySet());
        for(Long userid : server.lastSeen.keySet()){
            if((System.currentTimeMillis() - server.lastSeen.get(user.getLongID())) > activeDelay){
                activeUsers.remove(userid);
            }
        }

        if(activeUsers.size() < 1){
            MessagesUtils.addReaction(message, "There are no users to be hugged.", EmojiEnum.X);
            return;
        }
        IUser randomUser = user;

        while(user == randomUser) {
            randomUser = message.getGuild().getUserByID(activeUsers.get(MiscUtils.randomInt(0, activeUsers.size() - 1)));
        }

        if(user.getLongID() == Constants.JASCH_ID){
            MessagesUtils.sendPlain("FUCK YOU " + randomUser.getDisplayName(guild), channel, false);
            return;
        }

        MessagesUtils.sendPlain("*hugs " + randomUser.getDisplayName(guild) + "*", channel, false);
    }

}
