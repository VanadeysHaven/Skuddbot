package me.Cooltimmetje.Skuddbot.Minigames.Blackjack;

import me.Cooltimmetje.Skuddbot.Profiles.Server;
import me.Cooltimmetje.Skuddbot.Profiles.ServerManager;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.reaction.ReactionAddEvent;
import sx.blah.discord.handle.obj.IMessage;

/**
 * This handles all games of blackjack on a global basis.
 *
 * @author Tim (Cooltimmetje)
 * @version v0.4.61-ALPHA
 * @since v0.4.5-ALPHA
 */
public class BlackjackManager {

    public static void run(IMessage message){
        ServerManager.getServer(message.getGuild().getStringID()).getBlackjackHandler().startNewGame(message.getAuthor(), message);
    }

    public static void clearCooldowns(){
        for(Server server : ServerManager.servers.values()){
            server.getBlackjackHandler().cooldowns.clear();
        }
    }

    @EventSubscriber
    public void onReaction(ReactionAddEvent event){
        ServerManager.getServer(event.getGuild().getStringID()).getBlackjackHandler().onReaction(event);
    }

}
