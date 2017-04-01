package me.Cooltimmetje.Skuddbot.Experience;

import me.Cooltimmetje.Skuddbot.Profiles.ProfileManager;
import me.Cooltimmetje.Skuddbot.Profiles.Server;
import me.Cooltimmetje.Skuddbot.Profiles.ServerManager;
import me.Cooltimmetje.Skuddbot.Profiles.SkuddUser;
import me.Cooltimmetje.Skuddbot.Utilities.MiscUtils;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;

/**
 * This class handle's XP giving on Discord.
 *
 * @author Tim (Cooltimmetje)
 * @version v0.4-ALPHA-DEV
 * @since v0.1-ALPHA
 */
public class XPGiver {

    @EventSubscriber
    @SuppressWarnings("all") //FUCK OFF
    public void onMessage(MessageReceivedEvent event){
        if(!event.getMessage().getContent().startsWith("!") && !event.getMessage().getAuthor().isBot() && !event.getMessage().getChannel().isPrivate()){
            SkuddUser user = ProfileManager.getDiscord(event.getMessage().getAuthor().getID(), event.getMessage().getGuild().getID(), true);
            if(user.isTrackMe()) {
                Server server = ServerManager.getServer(event.getMessage().getGuild().getID());
                int xpAwarded = MiscUtils.randomInt(server.getMinXP(), server.getMaxXP());
                user.setXp(user.getXp() + xpAwarded);
                user.calcXP(false, event.getMessage());
            }
        }
    }
}