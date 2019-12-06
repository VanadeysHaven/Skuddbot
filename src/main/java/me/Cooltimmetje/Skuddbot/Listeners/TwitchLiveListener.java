package me.Cooltimmetje.Skuddbot.Listeners;

import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.MessageChannel;
import discord4j.core.object.util.Snowflake;
import me.Cooltimmetje.Skuddbot.Main;
import me.Cooltimmetje.Skuddbot.Utilities.MessagesUtils;

import java.util.HashMap;

/**
 * Things to do with people going live on Twitch.
 *
 * @author Tim (Cooltimmetje)
 * @version v0.5.1-ALPHA
 * @since v0.1-ALPHA
 */
public class TwitchLiveListener {

    private HashMap<String,Long> cooldown = new HashMap<>();

    public static void onMessage(MessageCreateEvent event){
        if(event.getMessage().getAuthorAsMember().block().getId().asString().equals("165140151121608704")){
            if(event.getMessage().getContent().get().contains("just went live!")) {

                if (event.getMessage().getGuild().block().getId().asString().equals("198483566026424321")) { //Melsh
                    MessagesUtils.sendPlain("@here melsh87 just went live! Go check them out and show them some love! https://www.twitch.tv/melsh87", (MessageChannel) Main.getInstance().getSkuddbot().getChannelById(Snowflake.of(157855484395782144L)), true);
                } else if (event.getMessage().getGuild().block().getId().asString().equals("157774629975490561")) { //Ray
                    MessagesUtils.sendPlain("@here rayskudda just went live! Go check them out and show them some love! https://www.twitch.tv/rayskudda", (MessageChannel) Main.getInstance().getSkuddbot().getChannelById(Snowflake.of(231813505961951232L)), true);
                }
            }
        }
    }

}
