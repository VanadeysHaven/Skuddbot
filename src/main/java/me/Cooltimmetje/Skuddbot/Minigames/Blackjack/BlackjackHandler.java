package me.Cooltimmetje.Skuddbot.Minigames.Blackjack;

import discord4j.core.event.domain.message.ReactionAddEvent;
import discord4j.core.object.entity.Channel;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import discord4j.core.object.util.Snowflake;
import me.Cooltimmetje.Skuddbot.Enums.EmojiEnum;
import me.Cooltimmetje.Skuddbot.Utilities.CooldownManager;
import me.Cooltimmetje.Skuddbot.Utilities.Logger;
import me.Cooltimmetje.Skuddbot.Utilities.MessagesUtils;

import java.util.HashMap;

/**
 * This handles all games of blackjack on a per-server basis.
 *
 * @author Tim (Cooltimmetje)
 * @version v0.5.1-ALPHA
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

    void startNewGame(User user, Message message){
        if(cooldownManager.isOnCooldown(user.getId().asString())){
            MessagesUtils.addReaction(message, "Hold on there, we don't want you to get a gambling addiction, you'll have to wait 5 minutes between games.", EmojiEnum.HOURGLASS_FLOWING_SAND, false);
            return;
        }

        if(!games.containsKey(user.getId().asString())) {
            BlackjackGame blackjackGame = new BlackjackGame(user.asMember(Snowflake.of(serverId)).block(), message.getChannel().block());
            if(blackjackGame.gameState != GameStates.ENDED) {
                games.put(user.getId().asString(), blackjackGame);
            }
        } else {
            MessagesUtils.addReaction(message, "You already have a game of blackjack in progress, please finish that first!", EmojiEnum.X, false);
        }
    }

    void onReaction(ReactionAddEvent event){
        if(event.getUser().block().isBot()) return;
        if(!games.containsKey(event.getUser().block().getId().asString())) return;
        if(event.getChannel().block().getType() == Channel.Type.DM) return;
        if(games.get(event.getUser().block().getId().asString()).getMessage().getId().asLong() != event.getMessage().block().getId().asLong()) return;

        if(event.getEmoji().asUnicodeEmoji().get().getRaw().equals(EmojiEnum.H.getUnicode())) games.get(event.getUser().block().getId().asString()).hit();
        if(event.getEmoji().asUnicodeEmoji().get().getRaw().equals(EmojiEnum.S.getUnicode())) games.get(event.getUser().block().getId().asString()).stand();
    }

    void applyCooldown(String identifier){
        cooldownManager.applyCooldown(identifier);
    }

    void clearCooldowns(){
        cooldownManager.clearAll();
    }
}
