package me.Cooltimmetje.Skuddbot.Minigames.Blackjack;

import discord4j.core.event.domain.message.ReactionAddEvent;
import discord4j.core.object.entity.Channel;
import discord4j.core.object.entity.Message;
import me.Cooltimmetje.Skuddbot.Profiles.Server;
import me.Cooltimmetje.Skuddbot.Profiles.ServerManager;

/**
 * This handles all games of blackjack on a global basis.
 *
 * @author Tim (Cooltimmetje)
 * @version v0.5.1-ALPHA
 * @since v0.4.5-ALPHA
 */
public class BlackjackManager {

    public static void run(Message message){
        ServerManager.getServer(message.getGuild().block()).getBlackjackHandler().startNewGame(message.getAuthor().get(), message);
    }

    public static void clearCooldowns(){
        for(Server server : ServerManager.servers.values()){
            server.getBlackjackHandler().clearCooldowns();
        }
    }

    public void onReaction(ReactionAddEvent event){
        if(event.getMessage().block().getChannel().block().getType() == Channel.Type.DM) return;
        ServerManager.getServer(event.getGuild().block().getId().asString()).getBlackjackHandler().onReaction(event);
    }

}
