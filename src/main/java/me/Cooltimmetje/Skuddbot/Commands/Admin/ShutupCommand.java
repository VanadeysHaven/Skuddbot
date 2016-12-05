package me.Cooltimmetje.Skuddbot.Commands.Admin;

import me.Cooltimmetje.Skuddbot.Utilities.Constants;
import me.Cooltimmetje.Skuddbot.Utilities.MessagesUtils;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.MentionEvent;

/**
 * Created by Tim on 9/11/2016.
 */
public class ShutupCommand {

    @EventSubscriber
    public void onMention(MentionEvent event){

        if(event.getMessage().getAuthor().getID().equals(Constants.TIMMY_OVERRIDE)){
            if(event.getMessage().getContent().split(" ").length > 1){
                if(event.getMessage().getContent().split(" ")[1].equalsIgnoreCase("shutup")){
                    Constants.MUTED = !Constants.MUTED;
                    MessagesUtils.sendForce(Constants.MUTED ? ":zipper_mouth:" : ":smiley:", event.getMessage().getChannel());
                }
            }
        }

    }

}
