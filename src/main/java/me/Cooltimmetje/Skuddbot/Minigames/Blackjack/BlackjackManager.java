package me.Cooltimmetje.Skuddbot.Minigames.Blackjack;

import me.Cooltimmetje.Skuddbot.Profiles.ServerManager;
import sx.blah.discord.handle.obj.IMessage;

public class BlackjackManager {

    public static void run(IMessage message){
        ServerManager.getServer(message.getGuild().getStringID()).getBlackjackHandler().startNewGame(message.getAuthor(), message);
    }

}
