package me.Cooltimmetje.Skuddbot.Listeners;

import me.Cooltimmetje.Skuddbot.Profiles.MySqlManager;
import me.Cooltimmetje.Skuddbot.Profiles.ServerManager;
import me.Cooltimmetje.Skuddbot.Utilities.Logger;
import me.Cooltimmetje.Skuddbot.Utilities.MessagesUtils;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.GuildCreateEvent;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.RateLimitException;

import java.util.ArrayList;

/**
 * Stuff to handle when we join a server.
 *
 * @author Tim (Cooltimmetje)
 * @version v0.4.41-ALPHA
 * @since v0.2-ALPHA
 */
public class CreateServerListener {

    public static ArrayList<String> authorized = new ArrayList<>();

    @EventSubscriber
    public void onCreate(GuildCreateEvent event){
        if(authorized.contains(event.getGuild().getStringID())) {
            Logger.info("[ServerAuthorization] " + event.getGuild().getName() + " (ID: " + event.getGuild().getStringID() + ") is authorized to use Skuddbot.");
            ServerManager.getServer(event.getGuild().getStringID());
            MySqlManager.getTopDiscord(event.getGuild().getStringID());
        } else {
            Logger.info("[ServerAuthorization] " + event.getGuild().getName() + " (ID: " + event.getGuild().getStringID() + ") is not authorized to use Skuddbot. Leaving...");
            MessagesUtils.sendPlain("I'm sorry but this server is not authorized... Please refer to the manual on how to get your server authorized! You can find the manual here: https://goo.gl/oWoyG2", event.getGuild().getChannelByID(event.getGuild().getLongID()), false);
            try {
                event.getGuild().leave();
            } catch (DiscordException | RateLimitException e) {
                e.printStackTrace();
            }
        }
    }

}
