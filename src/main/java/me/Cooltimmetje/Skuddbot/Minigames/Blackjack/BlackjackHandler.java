package me.Cooltimmetje.Skuddbot.Minigames.Blackjack;

import me.Cooltimmetje.Skuddbot.Enums.EmojiEnum;
import me.Cooltimmetje.Skuddbot.Utilities.CooldownManager;
import me.Cooltimmetje.Skuddbot.Utilities.Logger;
import me.Cooltimmetje.Skuddbot.Utilities.MessagesUtils;
import sx.blah.discord.handle.impl.events.guild.channel.message.reaction.ReactionAddEvent;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

import java.util.HashMap;

/**
 * This handles all games of blackjack on a per-server basis.
 *
 * @author Tim (Cooltimmetje)
 * @version v0.4.61-ALPHA
 * @since v0.4.5-ALPHA
 */
public class BlackjackHandler {

    private static final int COOLDOWN = 300;

    private String serverId;
    private CooldownManager cooldownManager;

    public BlackjackHandler(String serverId){
        this.serverId = serverId;
        this.cooldownManager = new CooldownManager(COOLDOWN);

        Logger.info("Creating Blackjack handler for Server with ID: " + serverId);
    }

    HashMap<String, BlackjackGame> games = new HashMap<>();

    void startNewGame(IUser user, IMessage message){
        if(cooldownManager.isOnCooldown(user.getStringID())){
            MessagesUtils.addReaction(message, "Hold on there, we don't want you to get a gambling addiction, you'll have to wait 5 minutes between games.", EmojiEnum.HOURGLASS_FLOWING_SAND, false);
            return;
        }

        if(!games.containsKey(user.getStringID())) {
            BlackjackGame blackjackGame = new BlackjackGame(user, message.getChannel());
            if(blackjackGame.gameState != GameStates.ENDED) {
                games.put(user.getStringID(), blackjackGame);
            }
        } else {
            MessagesUtils.addReaction(message, "You already have a game of blackjack in progress, please finish that first!", EmojiEnum.X, false);
        }
    }

    void onReaction(ReactionAddEvent event){
        if(event.getUser().isBot()) return;
        if(!games.containsKey(event.getUser().getStringID())) return;
        if(event.getChannel().isPrivate()) return;
        if(games.get(event.getUser().getStringID()).getMessage().getLongID()!= event.getMessage().getLongID()) return;

        if(EmojiEnum.getByUnicode(event.getReaction().getEmoji().getName()) == EmojiEnum.H) games.get(event.getUser().getStringID()).hit();
        if(EmojiEnum.getByUnicode(event.getReaction().getEmoji().getName()) == EmojiEnum.S) games.get(event.getUser().getStringID()).stand();
    }

    void applyCooldown(String identifier){
        cooldownManager.applyCooldown(identifier);
    }

    void clearCooldowns(){
        cooldownManager.clearAll();
    }
}
