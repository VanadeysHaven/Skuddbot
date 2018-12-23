package me.Cooltimmetje.Skuddbot.Minigames.Blackjack;

import me.Cooltimmetje.Skuddbot.Profiles.ServerManager;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.reaction.ReactionAddEvent;
import sx.blah.discord.handle.obj.IMessage;

public class BlackjackManager {

    public static void run(IMessage message){
        ServerManager.getServer(message.getGuild().getStringID()).getBlackjackHandler().startNewGame(message.getAuthor(), message);
    }

    @EventSubscriber
    public void onReaction(ReactionAddEvent event){
        ServerManager.getServer(event.getGuild().getStringID()).getBlackjackHandler().onReaction(event);
    }

}
