package me.Cooltimmetje.Skuddbot.Minigames.Blackjack;

import me.Cooltimmetje.Skuddbot.Utilities.Logger;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

import java.util.HashMap;

public class BlackjackHandler {

    private String serverId;

    public BlackjackHandler(String serverId){
        this.serverId = serverId;

        Logger.info("Creating Blackjack handler for Server with ID: " + serverId);
    }

    private HashMap<String, BlackjackGame> games = new HashMap<>();

    public void startNewGame(IUser user, IMessage message){
        games.put(user.getStringID(), new BlackjackGame(user, message.getChannel()));
    }
}
