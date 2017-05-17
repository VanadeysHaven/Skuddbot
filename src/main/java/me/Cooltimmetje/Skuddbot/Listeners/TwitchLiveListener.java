package me.Cooltimmetje.Skuddbot.Listeners;

import me.Cooltimmetje.Skuddbot.Enums.DataTypes;
import me.Cooltimmetje.Skuddbot.Main;
import me.Cooltimmetje.Skuddbot.Profiles.ServerManager;
import me.Cooltimmetje.Skuddbot.Utilities.MessagesUtils;
import me.Cooltimmetje.Skuddbot.Utilities.MiscUtils;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.handle.impl.events.StatusChangeEvent;
import sx.blah.discord.handle.obj.Status;

/**
 * Things to do with people going live on Twitch.
 *
 * @author Tim (Cooltimmetje)
 * @version v0.4-ALPHA
 * @since v0.1-ALPHA
 */
public class TwitchLiveListener {

    @EventSubscriber
    public void onMessage(MessageReceivedEvent event){
        if(event.getMessage().getAuthor().getID().equals("165140151121608704")){
            if(event.getMessage().getContent().contains("just went offline, here are their most recent stats.")){
                ServerManager.getServer(event.getMessage().getGuild().getID()).runAnalytics(event.getMessage().getChannel());
            } else if(event.getMessage().getContent().contains("just went live!")){
                if(ServerManager.getServer(event.getMessage().getGuild().getID()).getTwitchChannel() != null) {
                    Main.getSkuddbotTwitch().sendMessage("#" + ServerManager.getServer(event.getMessage().getGuild().getID()).getTwitchChannel(), MiscUtils.getRandomMessage(DataTypes.ALIVE));

                    ServerManager.getServer(event.getMessage().getGuild().getID()).setStreamLive(true);
                }
            }
        }
    }

    @EventSubscriber
    public void onStatusChange(StatusChangeEvent event){
        if(event.getOldStatus().getType() != Status.StatusType.STREAM) {
            if (event.getUser().getID().equals("131382094457733120")) { //Melsh
                if (event.getNewStatus().getType() == Status.StatusType.STREAM) {
                    MessagesUtils.sendPlain("@here melsh87 just went live! Go check them out and show them some love! https://www.twitch.tv/melsh87", Main.getInstance().getSkuddbot().getChannelByID("157855484395782144"), true);
                }
            } else if (event.getUser().getID().equals("147295556979523584")) { //Ray
                if (event.getNewStatus().getType() == Status.StatusType.STREAM) {
                    MessagesUtils.sendPlain("@here rayskudda just went live! Go check them out and show them some love! https://www.twitch.tv/rayskudda", Main.getInstance().getSkuddbot().getChannelByID("231813505961951232"), true);
                }
            }
        }
    }

}
