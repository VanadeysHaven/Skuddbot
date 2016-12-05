package me.Cooltimmetje.Skuddbot.Listeners;

import me.Cooltimmetje.Skuddbot.Profiles.ServerManager;
import me.Cooltimmetje.Skuddbot.Utilities.Logger;
import me.Cooltimmetje.Skuddbot.Utilities.MessagesUtils;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.GuildCreateEvent;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.RateLimitException;

import java.util.ArrayList;

/**
 * Created by Tim on 8/22/2016.
 */
public class CreateServerListener {

    public static ArrayList<String> authorized = new ArrayList<>();

    @EventSubscriber
    public void onCreate(GuildCreateEvent event){
        if(authorized.contains(event.getGuild().getID())) {
            Logger.info("[ServerAuthorization] " + event.getGuild().getName() + " (ID: " + event.getGuild().getID() + ") is authorized to use Skuddbot.");
            ServerManager.getServer(event.getGuild().getID());
        } else {
            Logger.info("[ServerAuthorization] " + event.getGuild().getName() + " (ID: " + event.getGuild().getID() + ") is not authorized to use Skuddbot. Leaving...");
            MessagesUtils.sendPlain("I'm sorry but this server is not authorized... Please refer to the manual on how to get your server authorized! You can find the manual here: https://goo.gl/oWoyG2", event.getGuild().getChannelByID(event.getGuild().getID()));
            try {
                event.getGuild().leaveGuild();
            } catch (DiscordException | RateLimitException e) {
                e.printStackTrace();
            }
        }
    }

}
