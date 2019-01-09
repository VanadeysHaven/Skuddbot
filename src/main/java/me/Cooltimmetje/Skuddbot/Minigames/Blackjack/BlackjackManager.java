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
 * @version v0.4.51-ALPHA
 * @since v0.4.5-ALPHA
 */
public class BlackjackManager {

    public static void run(IMessage message){
        if(message.getGuild().getLongID() == 198483566026424321L && message.getContent().split(" ")[0].equalsIgnoreCase("!bj")){
            return;
        }

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
