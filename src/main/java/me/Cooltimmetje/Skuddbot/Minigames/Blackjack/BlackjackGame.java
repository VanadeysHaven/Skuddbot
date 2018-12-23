package me.Cooltimmetje.Skuddbot.Minigames.Blackjack;

import me.Cooltimmetje.Skuddbot.Enums.EmojiEnum;
import me.Cooltimmetje.Skuddbot.Utilities.MessagesUtils;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

import java.text.MessageFormat;
import java.util.ArrayList;

public class BlackjackGame {

    private IMessage message;
    private IUser user;
    private IGuild guild;
    private ArrayList<Card> playerHand;
    private ArrayList<Card> dealerHand;
    private Card holeCard;

    private String messageFormat = "**BLACKJACK** | *{0}*\n\n" +
            "**DEALER\'S HAND:** (hand value: {1}) *Dealer draws to 16, stands on 17.*\n" +
            "{2}\n\n" +
            "**{0}\'S HAND:** (hand value: {3})\n" +
            "{4}\n\n" +
            "{5}";

    public BlackjackGame(IUser user, IChannel channel){
        this.user = user;
        this.guild = channel.getGuild();

        playerHand = new ArrayList<>();
        playerHand.add(new Card());
        playerHand.add(new Card());

        dealerHand = new ArrayList<>();
        dealerHand.add(new Card());
        holeCard = new Card();

        message = MessagesUtils.sendPlain(buildMessage(), channel, false);
    }

    private String buildMessage(){
        String userDisplayName = user.getDisplayName(guild);
        int dealerHandValue = calculateHandValue(dealerHand);
        String dealerHandString = formatHand(dealerHand, true);
        int playerHandValue = calculateHandValue(playerHand);
        String playerHandString = formatHand(playerHand, false);

        return MessageFormat.format(messageFormat, userDisplayName, dealerHandValue, dealerHandString, playerHandValue, playerHandString, "[playing instructions here]");
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

    private String formatHand(ArrayList<Card> cards, boolean dealerHand){
        StringBuilder sb = new StringBuilder();

        for(Card card : cards){
            sb.append(card.toString()).append("\n");
        }

        if(dealerHand && cards.size() == 1){
            sb.append(EmojiEnum.QUESTION.getEmoji()).append(" face down card\n");
        }

        return sb.toString().trim();
    }



}
