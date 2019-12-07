package me.Cooltimmetje.Skuddbot.Minigames.Blackjack;

import discord4j.core.object.entity.*;
import discord4j.core.object.reaction.ReactionEmoji;
import discord4j.core.spec.MessageEditSpec;
import me.Cooltimmetje.Skuddbot.Enums.EmojiEnum;
import me.Cooltimmetje.Skuddbot.Profiles.ProfileManager;
import me.Cooltimmetje.Skuddbot.Profiles.ServerManager;
import me.Cooltimmetje.Skuddbot.Profiles.SkuddUser;
import me.Cooltimmetje.Skuddbot.Utilities.EmojiHelper;
import me.Cooltimmetje.Skuddbot.Utilities.MessagesUtils;
import reactor.core.publisher.Mono;

import java.text.MessageFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * This is a game of blackjack, with all the needed functions to carry out an game.
 *
 * @author Tim (Cooltimmetje)
 * @version v0.5.1-ALPHA
 * @since v0.4.5-ALPHA
 */
public class BlackjackGame {

    private Message message;
    private Member member;
    private Guild guild;
    private ArrayList<Card> playerHand;
    private ArrayList<Card> dealerHand;
    private Card holeCard;
    private ArrayList<Card> cardsInGame;

    private String userDisplayName;
    private int dealerHandValue;
    private String dealerHandString;
    private int playerHandValue;
    private String playerHandString;
    private String playingInstructions;
    public GameStates gameState;

    private int baseReward = 50;
    private int winBonus = 75;
    private int twentyOneBonus = 100;


    private String messageFormat = "**BLACKJACK** | *{0}*\n\n" +
            "**DEALER HAND:** (hand value: {1}) *Dealer draws to 16, stands on 17.*\n" +
            "{2}\n\n" +
            "**YOUR HAND:** (hand value: {3})\n" +
            "{4}\n\n" +
            "{5}";

    public BlackjackGame(Member member, MessageChannel channel){
        this.member = member;
        this.guild = ((TextChannel) channel).getGuild().block();
        this.gameState = GameStates.PLAYER_PLAYING;
        this.cardsInGame = new ArrayList<>();

        playerHand = new ArrayList<>();
        playerHand.add(drawNewCard());
        playerHand.add(drawNewCard());

        dealerHand = new ArrayList<>();
        dealerHand.add(drawNewCard());
        holeCard = drawNewCard();

        updateGameStats();
        this.message = MessagesUtils.sendPlain(buildMessage(), channel, false);

        if(gameState == GameStates.PLAYER_PLAYING) {
            message.addReaction(ReactionEmoji.unicode(EmojiEnum.H.getUnicode()));
            message.addReaction(ReactionEmoji.unicode(EmojiEnum.S.getUnicode()));
        }
    }

    private void updateGameStats() {
        userDisplayName = member.getDisplayName();
        dealerHandValue = calculateHandValue(dealerHand);
        dealerHandString = formatHand(dealerHand);
        playerHandValue = calculateHandValue(playerHand);
        playerHandString = formatHand(playerHand);
        SkuddUser su = ProfileManager.getDiscord(member, true);

        if (gameState == GameStates.PLAYER_PLAYING) {
            if (playerHandValue == 21) {
                playingInstructions = "**21! You win!** | Reward: *+" + (baseReward + winBonus + twentyOneBonus) + " " + EmojiHelper.getEmoji("xp_icon") + "*";
                su.setXp(su.getXp() + baseReward + winBonus + twentyOneBonus);
                su.setBlackjackWins(su.getBlackjackWins() + 1);
                su.setBlackjackTwentyOnes(su.getBlackjackTwentyOnes() + 1);
                gameState = GameStates.ENDED;
            } else if (playerHandValue > 21) {
                playingInstructions = "**You busted! Better luck next time.**";
                su.setBlackjackLosses(su.getBlackjackLosses() + 1);
                gameState = GameStates.ENDED;
            } else {
                playingInstructions = "*Press " + EmojiEnum.H.getUnicode() + " to hit, press " + EmojiEnum.S.getUnicode() + " to stand.*";
                gameState = GameStates.PLAYER_PLAYING;
            }
        } else if (gameState == GameStates.DEALER_PLAYING) {
            if(dealerHandValue == 21) {
                playingInstructions = "**You lose! The dealer got 21.**";
                su.setBlackjackLosses(su.getBlackjackLosses() + 1);
                gameState = GameStates.ENDED;
            } else if(dealerHandValue > 21) {
                playingInstructions = "**You win! The dealer busted!** | Reward: *+" + (baseReward + winBonus) + " " + EmojiHelper.getEmoji("xp_icon") + "*";
                su.setXp(su.getXp() + baseReward + winBonus);
                su.setBlackjackWins(su.getBlackjackWins() + 1);
                gameState = GameStates.ENDED;
            } else if(dealerHandValue == playerHandValue){
                playingInstructions = "**PUSH! You tied with the dealer.** | Reward: *+" + (baseReward) + " " + EmojiHelper.getEmoji("xp_icon") + "*";
                su.setXp(su.getXp() + baseReward);
                su.setBlackjackPushes(su.getBlackjackPushes() + 1);
                gameState = GameStates.ENDED;
            } else if (dealerHandValue > playerHandValue){
                playingInstructions = "**You lose! The dealer has a higher hand value than you!**";
                su.setBlackjackLosses(su.getBlackjackLosses() + 1);
                gameState = GameStates.ENDED;
            } else {
                playingInstructions = "**You win! The dealer has a lower hand value than you!** | Reward: *+" + (baseReward + winBonus) + " " + EmojiHelper.getEmoji("xp_icon") + "*";
                su.setXp(su.getXp() + baseReward + winBonus);
                su.setBlackjackWins(su.getBlackjackWins() + 1);
                gameState = GameStates.ENDED;
            }
        }

        if(gameState == GameStates.ENDED){
            ServerManager.getServer(guild).getBlackjackHandler().applyCooldown(member.getId().asString());
            if(dealerHand.size() == 1) {
                dealerHand.add(holeCard);
                dealerHandValue = calculateHandValue(dealerHand);
                dealerHandString = formatHand(dealerHand);
            }
        }
    }

    private String buildMessage(){
        return MessageFormat.format(messageFormat, userDisplayName, dealerHandValue, dealerHandString, playerHandValue, playerHandString, playingInstructions);
    }

    private void editMessage(){
        Consumer<MessageEditSpec> edit = spec -> {
            spec.setContent(buildMessage());
        };

        message.edit(edit).block();
    }

    private int calculateHandValue(ArrayList<Card> cards){
        int aces = 0;
        int elevenAces = 0;
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
                elevenAces++;
            } else {
                handValue += 1;
            }
        }

        if(handValue > 21 && elevenAces > 0){
            for(int i=0; i < elevenAces; i++){
                if(handValue > 21){
                    handValue -= 10;
                }
            }
        }

        return handValue;
    }

    private String formatHand(ArrayList<Card> cards){
        StringBuilder sb = new StringBuilder();
        SkuddUser su = ProfileManager.getDiscord(member, true);

        for(Card card : cards){
            sb.append(card.toString()).append(" | ");
        }

        if(cards.size() == 1){
            sb.append(EmojiEnum.QUESTION.getUnicode()).append(" ").append(EmojiEnum.QUESTION.getUnicode()).append(" | ");
        }

        String hand = sb.toString();
        return hand.substring(0, hand.length() - 3);
    }

    public void hit(){
        if(gameState != GameStates.PLAYER_PLAYING){
            return;
        }
        playerHand.add(drawNewCard());
        updateGameStats();

        editMessage();

        if(gameState == GameStates.ENDED){
            ServerManager.getServer(message.getGuild().block().getId().asString()).getBlackjackHandler().games.remove(member.getId().asString());
        } else if (gameState == GameStates.PLAYER_PLAYING){
             message.removeReaction(ReactionEmoji.unicode(EmojiEnum.H.getUnicode()), member.getId());
        }
    }

    public void stand(){
        if(gameState != GameStates.PLAYER_PLAYING){
            return;
        }

        message.getChannel().block().typeUntil(Mono.delay(Duration.ofSeconds(5)));
        gameState = GameStates.DEALER_PLAYING;

        dealerHand.add(holeCard);

        while(calculateHandValue(dealerHand) < 17){
            dealerHand.add(drawNewCard());
        }

        ScheduledThreadPoolExecutor exec = new ScheduledThreadPoolExecutor(1);
        exec.schedule(() -> {
            updateGameStats();
            editMessage();

            ServerManager.getServer(message.getGuild().block().getId().asString()).getBlackjackHandler().games.remove(member.getId().asString());
        }, 5, TimeUnit.SECONDS);
    }

    public Message getMessage(){
        return message;
    }

    private boolean cardExistsInGame(Card card){
        for(Card cardInGame : cardsInGame)
            if(card.equals(cardInGame)) return true;

        return false;
    }

    private Card drawNewCard(){
        Card card;
        boolean duplicate;
        do {
            card = new Card();
            duplicate = cardExistsInGame(card);
        } while (duplicate);

        cardsInGame.add(card);
        return card;
    }

}
