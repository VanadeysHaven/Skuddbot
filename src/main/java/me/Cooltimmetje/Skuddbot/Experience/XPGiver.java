package me.Cooltimmetje.Skuddbot.Experience;

import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Channel;
import me.Cooltimmetje.Skuddbot.Profiles.ProfileManager;
import me.Cooltimmetje.Skuddbot.Profiles.Server;
import me.Cooltimmetje.Skuddbot.Profiles.ServerManager;
import me.Cooltimmetje.Skuddbot.Profiles.SkuddUser;
import me.Cooltimmetje.Skuddbot.Utilities.MiscUtils;

/**
 * This class handle's XP giving on Discord.
 *
 * @author Tim (Cooltimmetje)
 * @version v0.5.1-ALPHA
 * @since v0.1-ALPHA
 */
public class XPGiver {

    @SuppressWarnings("all") //FUCK OFF
    public static void onMessage(MessageCreateEvent event){
        if(!event.getMessage().getAuthor().get().isBot() && event.getMessage().getChannel().block().getType() == Channel.Type.GUILD_TEXT){
            SkuddUser user = ProfileManager.getDiscord(event.getMessage().getAuthor().get().getId().asString(), event.getMessage().getGuild().block().getId().asString(), true);
            if(user.isTrackMe()) {
                Server server = ServerManager.getServer(event.getMessage().getGuild().block().getId().asString());
                int xpAwarded = MiscUtils.randomInt(server.getMinXP(), server.getMaxXP());
                user.setXp(user.getXp() + xpAwarded);
                user.calcXP(true, event.getMessage());
            }
        }
    }
}