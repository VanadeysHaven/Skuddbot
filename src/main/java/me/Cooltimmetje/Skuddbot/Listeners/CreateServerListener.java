package me.Cooltimmetje.Skuddbot.Listeners;

import discord4j.core.event.domain.guild.GuildCreateEvent;
import discord4j.core.object.entity.MessageChannel;
import me.Cooltimmetje.Skuddbot.Profiles.ServerManager;
import me.Cooltimmetje.Skuddbot.Utilities.Logger;
import me.Cooltimmetje.Skuddbot.Utilities.MessagesUtils;

import java.util.ArrayList;

/**
 * Stuff to handle when we join a server.
 *
 * @author Tim (Cooltimmetje)
 * @version v0.5.1-ALPHA
 * @since v0.2-ALPHA
 */
public class CreateServerListener {

    public static ArrayList<String> authorized = new ArrayList<>();

    public static void onCreate(GuildCreateEvent event){
        if(authorized.contains(event.getGuild().getId().asString())) {
            Logger.info("[ServerAuthorization] " + event.getGuild().getName() + " (ID: " + event.getGuild().getId().asString() + ") is authorized to use Skuddbot.");
            ServerManager.getServer(event.getGuild().getId().asString());
        } else {
            Logger.info("[ServerAuthorization] " + event.getGuild().getName() + " (ID: " + event.getGuild().getId().asString() + ") is not authorized to use Skuddbot. Leaving...");
            MessagesUtils.sendPlain("I'm sorry but this server is not authorized... Please refer to the manual on how to get your server authorized! You can find the manual here: https://goo.gl/oWoyG2", (MessageChannel) event.getGuild().getChannelById(event.getGuild().getId()).block(), false);
            event.getGuild().leave().block();
        }
    }

}
