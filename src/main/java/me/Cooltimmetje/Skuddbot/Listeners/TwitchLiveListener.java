package me.Cooltimmetje.Skuddbot.Listeners;

import me.Cooltimmetje.Skuddbot.Enums.DataTypes;
import me.Cooltimmetje.Skuddbot.Main;
import me.Cooltimmetje.Skuddbot.Profiles.ServerManager;
import me.Cooltimmetje.Skuddbot.Utilities.MessagesUtils;
import me.Cooltimmetje.Skuddbot.Utilities.MiscUtils;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.MentionEvent;
import sx.blah.discord.handle.impl.events.StatusChangeEvent;
import sx.blah.discord.handle.obj.Status;

/**
 * Created by Tim on 10/7/2016.
 */
public class TwitchLiveListener {

    @EventSubscriber
    public void onAtEveryone(MentionEvent event){
        if(event.getMessage().mentionsEveryone()){
            if(event.getMessage().getAuthor().getID().equals("165140151121608704")){
                if(ServerManager.getServer(event.getMessage().getGuild().getID()).getTwitchChannel() != null){
                    Main.getSkuddbotTwitch().sendMessage("#" + ServerManager.getServer(event.getMessage().getGuild().getID()).getTwitchChannel(), MiscUtils.getRandomMessage(DataTypes.ALIVE));
                }
            }
        }
    }
    @EventSubscriber
    public void onStatusChange(StatusChangeEvent event){
        if(event.getUser().getID().equals("131382094457733120")){ //Melsh
            if(event.getNewStatus().getType() == Status.StatusType.STREAM){
                MessagesUtils.sendPlain("@here melsh87 just went live! Go check them out and show them some love! https://www.twitch.tv/melsh87", Main.getInstance().getSkuddbot().getChannelByID("157855484395782144"));
            }
        } else if(event.getUser().getID().equals("147295556979523584")){ //Ray
            if(event.getNewStatus().getType() == Status.StatusType.STREAM){
                MessagesUtils.sendPlain("@here rayskudda just went live! Go check them out and show them some love! https://www.twitch.tv/rayskudda", Main.getInstance().getSkuddbot().getChannelByID("231813505961951232"));
            }
        }
    }

}
