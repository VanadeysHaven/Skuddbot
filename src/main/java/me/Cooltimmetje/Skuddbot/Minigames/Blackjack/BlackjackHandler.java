package me.Cooltimmetje.Skuddbot.Minigames.Blackjack;

import me.Cooltimmetje.Skuddbot.Enums.EmojiEnum;
import me.Cooltimmetje.Skuddbot.Utilities.Logger;
import sx.blah.discord.handle.impl.events.guild.channel.message.reaction.ReactionAddEvent;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.RequestBuffer;

import java.util.HashMap;

public class BlackjackHandler {

    private String serverId;

    public BlackjackHandler(String serverId){
        this.serverId = serverId;

        Logger.info("Creating Blackjack handler for Server with ID: " + serverId);
    }

    public HashMap<String, BlackjackGame> games = new HashMap<>();

    public void startNewGame(IUser user, IMessage message){
        if(!games.containsKey(user.getStringID())) {
            games.put(user.getStringID(), new BlackjackGame(user, message.getChannel()));
        } else {
            RequestBuffer.request(message::delete);
        }
    }

    public void onReaction(ReactionAddEvent event){
        if(event.getUser().isBot()){
            return;
        }
        if(!games.containsKey(event.getUser().getStringID())){
            return;
        }
        if(event.getChannel().isPrivate()){
            return;
        }
        if(games.get(event.getUser().getStringID()).getMessage() != event.getMessage()){
            return;
        }

        if(EmojiEnum.getByUnicode(event.getReaction().getEmoji().getName()) == EmojiEnum.REGIONAL_INDICATOR_H){
            games.get(event.getUser().getStringID()).hit();
        }
        if(EmojiEnum.getByUnicode(event.getReaction().getEmoji().getName()) == EmojiEnum.REGIONAL_INDICATOR_S){
            games.get(event.getUser().getStringID()).stand();
        }
    }
}
