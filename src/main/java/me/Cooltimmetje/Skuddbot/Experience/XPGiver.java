package me.Cooltimmetje.Skuddbot.Experience;

import me.Cooltimmetje.Skuddbot.Profiles.ProfileManager;
import me.Cooltimmetje.Skuddbot.Profiles.Server;
import me.Cooltimmetje.Skuddbot.Profiles.ServerManager;
import me.Cooltimmetje.Skuddbot.Profiles.SkuddUser;
import me.Cooltimmetje.Skuddbot.Utilities.MiscUtils;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;

/**
 * Created by Tim on 8/2/2016.
 */
public class XPGiver {

    @EventSubscriber
    public void onMessage(MessageReceivedEvent event){
        if(!event.getMessage().getContent().startsWith("!") && !event.getMessage().getAuthor().isBot() && !event.getMessage().getChannel().isPrivate()){
            SkuddUser user = ProfileManager.getDiscord(event.getMessage().getAuthor().getID(), event.getMessage().getGuild().getID(), true);
            Server server = ServerManager.getServer(event.getMessage().getGuild().getID());
            int xpAwarded = MiscUtils.randomInt(server.getMinXP(), server.getMaxXP());
            user.setXp(user.getXp() + xpAwarded);
            user.calcXP(false,event.getMessage());
        }
    }
}
