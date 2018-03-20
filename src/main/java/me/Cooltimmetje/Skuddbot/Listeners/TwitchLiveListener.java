package me.Cooltimmetje.Skuddbot.Listeners;

import me.Cooltimmetje.Skuddbot.Enums.DataTypes;
import me.Cooltimmetje.Skuddbot.Main;
import me.Cooltimmetje.Skuddbot.Profiles.ServerManager;
import me.Cooltimmetje.Skuddbot.Utilities.MessagesUtils;
import me.Cooltimmetje.Skuddbot.Utilities.MiscUtils;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

import java.util.HashMap;

/**
 * Things to do with people going live on Twitch.
 *
 * @author Tim (Cooltimmetje)
 * @version v0.4.01-ALPHA-DEV
 * @since v0.1-ALPHA
 */
public class TwitchLiveListener {

    private HashMap<String,Long> cooldown = new HashMap<>();

    @EventSubscriber
    public void onMessage(MessageReceivedEvent event){
        if(event.getMessage().getAuthor().getStringID().equals("165140151121608704")){
            if(event.getMessage().getContent().contains("just went offline, here are their most recent stats.")){
//                ServerManager.getServer(event.getMessage().getGuild().getStringID()).runAnalytics(event.getMessage().getChannel());
            } else if(event.getMessage().getContent().contains("just went live!")) {
                if (ServerManager.getServer(event.getMessage().getGuild().getStringID()).getTwitchChannel() != null) {
                    Main.getSkuddbotTwitch().sendMessage("#" + ServerManager.getServer(event.getMessage().getGuild().getStringID()).getTwitchChannel(), MiscUtils.getRandomMessage(DataTypes.ALIVE));

//                    ServerManager.getServer(event.getMessage().getGuild().getStringID()).setStreamLive(true);
                }

                if (event.getMessage().getGuild().getStringID().equals("198483566026424321")) { //Melsh
                    MessagesUtils.sendPlain("@here melsh87 just went live! Go check them out and show them some love! https://www.twitch.tv/melsh87", Main.getInstance().getSkuddbot().getChannelByID(157855484395782144L), true);
                } else if (event.getMessage().getGuild().getStringID().equals("157774629975490561")) { //Ray
                    MessagesUtils.sendPlain("@here rayskudda just went live! Go check them out and show them some love! https://www.twitch.tv/rayskudda", Main.getInstance().getSkuddbot().getChannelByID(231813505961951232L), true);
                }
            }
        }
    }

}
