package me.Cooltimmetje.Skuddbot.Minigames.Blackjack;

import com.vdurmont.emoji.EmojiManager;
import me.Cooltimmetje.Skuddbot.Enums.EmojiEnum;
import me.Cooltimmetje.Skuddbot.Profiles.ProfileManager;
import me.Cooltimmetje.Skuddbot.Profiles.ServerManager;
import me.Cooltimmetje.Skuddbot.Profiles.SkuddUser;
import me.Cooltimmetje.Skuddbot.Utilities.MessagesUtils;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.RequestBuffer;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * This is a game of blackjack, with all the needed functions to carry out an game.
 *
 * @author Tim (Cooltimmetje)
 * @version v0.4.51-ALPHA
 * @since v0.4.5-ALPHA
 */
public class BlackjackGame {

    private IMessage message;
    private IUser user;
    private IGuild guild;
    private ArrayList<Card> playerHand;
    private ArrayList<Card> dealerHand;
    private Card holeCard;

    private String userDisplayName;
    private int dealerHandValue;
    private String dealerHandString;
    private int playerHandValue;
    private String playerHandString;
    private String playingInstructions;
    private GameStates gameState;


    private String messageFormat = "[beta] **BLACKJACK** | *{0}*\n\n" +
            "**DEALER HAND:** (hand value: {1}) *Dealer draws to 16, stands on 17.*\n" +
            "{2}\n\n" +
            "**YOUR HAND:** (hand value: {3})\n" +
            "{4}\n\n" +
            "{5}";

    public BlackjackGame(IUser user, IChannel channel){
        this.user = user;
        this.guild = channel.getGuild();
        this.gameState = GameStates.PLAYER_PLAYING;

        playerHand = new ArrayList<>();
        playerHand.add(new Card());
        playerHand.add(new Card());

        dealerHand = new ArrayList<>();
        dealerHand.add(new Card());
        holeCard = new Card();

        updateGameStats();
        this.message = MessagesUtils.sendPlain(buildMessage(), channel, false);

        if(gameState == GameStates.PLAYER_PLAYING) {
            RequestBuffer.request(() -> {
                message.addReaction(EmojiManager.getForAlias(EmojiEnum.REGIONAL_INDICATOR_H.getAlias()));
                message.addReaction(EmojiManager.getForAlias(EmojiEnum.REGIONAL_INDICATOR_S.getAlias()));
            });
        }
    }

    private void updateGameStats() {
        userDisplayName = user.getDisplayName(guild);
        dealerHandValue = calculateHandValue(dealerHand);
        dealerHandString = formatHand(dealerHand);
        playerHandValue = calculateHandValue(playerHand);
        playerHandString = formatHand(playerHand);
        SkuddUser su = ProfileManager.getDiscord(user, guild, true);

        if (gameState == GameStates.PLAYER_PLAYING) {
            if (playerHandValue == 21) {
                playingInstructions = "**21! You win!**";
                su.setBlackjackWins(su.getBlackjackWins() + 1);
                su.setBlackjackTwentyOnes(su.getBlackjackTwentyOnes() + 1);
                gameState = GameStates.ENDED;
            } else if (playerHandValue > 21) {
                playingInstructions = "**You busted! Better luck next time.**";
                su.setBlackjackLosses(su.getBlackjackLosses() + 1);
                gameState = GameStates.ENDED;
            } else {
                playingInstructions = "*Press " + EmojiEnum.REGIONAL_INDICATOR_H.getEmoji() + " to hit, press " + EmojiEnum.REGIONAL_INDICATOR_S.getEmoji() + " to stand.*";
                gameState = GameStates.PLAYER_PLAYING;
            }
        } else if (gameState == GameStates.DEALER_PLAYING) {
            if(dealerHandValue == 21) {
                playingInstructions = "**You lose! The dealer got 21.**";
                su.setBlackjackLosses(su.getBlackjackLosses() + 1);
                gameState = GameStates.ENDED;
            } else if(dealerHandValue > 21) {
                playingInstructions = "**You win! The dealer busted!**";
                su.setBlackjackWins(su.getBlackjackWins() + 1);
                gameState = GameStates.ENDED;
            } else if(dealerHandValue == playerHandValue){
                playingInstructions = "**PUSH! You tied with the dealer.**";
                su.setBlackjackPushes(su.getBlackjackPushes() + 1);
                gameState = GameStates.ENDED;
            } else if (dealerHandValue > playerHandValue){
                playingInstructions = "**You lose! The dealer has a higher hand value than you!**";
                su.setBlackjackLosses(su.getBlackjackLosses() + 1);
                gameState = GameStates.ENDED;
            } else {
                playingInstructions = "**You win! The dealer has a lower hand value than you!**";
                su.setBlackjackWins(su.getBlackjackWins() + 1);
                gameState = GameStates.ENDED;
            }
        }
    }

    private String buildMessage(){
        return MessageFormat.format(messageFormat, userDisplayName, dealerHandValue, dealerHandString, playerHandValue, playerHandString, playingInstructions);
    }

    private int calculateHandValue(ArrayList<Card> cards){
        int aces = 0;
        int handValue = 0;
        for(Card card : cards){
            if(card.getRank() == CardRanks.ACE){
                aces++;
                continue;
            }

            handValue += card.getRank().getValue();
        }

        for(int i=0; i < aces; i++){
            if((handValue + 11) <= 21){
                handValue += 11;
            } else {
                handValue += 1;
            }
        }

        return handValue;
    }

    private String formatHand(ArrayList<Card> cards){
        StringBuilder sb = new StringBuilder();

        for(Card card : cards){
            sb.append(card.toString()).append("\n");
        }

        if(cards.size() == 1){
            sb.append(EmojiEnum.QUESTION.getEmoji()).append(" face down card\n");
        }

        return sb.toString().trim();
    }

    public void hit(){
        if(gameState != GameStates.PLAYER_PLAYING){
            return;
        }
        playerHand.add(new Card());
        updateGameStats();

        RequestBuffer.request(() -> message.edit(buildMessage()));

        if(gameState == GameStates.ENDED){
            ServerManager.getServer(message.getGuild().getStringID()).getBlackjackHandler().games.remove(user.getStringID());
        } else if (gameState == GameStates.PLAYER_PLAYING){
            RequestBuffer.request(() -> message.removeReaction(user, EmojiManager.getForAlias(EmojiEnum.REGIONAL_INDICATOR_H.getAlias())));
        }
    }

    public void stand(){
        if(gameState != GameStates.PLAYER_PLAYING){
            return;
        }

        message.getChannel().setTypingStatus(true);
        gameState = GameStates.DEALER_PLAYING;

        dealerHand.add(holeCard);

        while(calculateHandValue(dealerHand) < 17){
            dealerHand.add(new Card());
        }

        ScheduledThreadPoolExecutor exec = new ScheduledThreadPoolExecutor(1);
        exec.schedule(() -> {
            updateGameStats();
            RequestBuffer.request(() -> message.edit(buildMessage()));

            ServerManager.getServer(message.getGuild().getStringID()).getBlackjackHandler().games.remove(user.getStringID());

            message.getChannel().setTypingStatus(false);
        }, 5, TimeUnit.SECONDS);
    }

    public IMessage getMessage(){
        return message;
    }

}
