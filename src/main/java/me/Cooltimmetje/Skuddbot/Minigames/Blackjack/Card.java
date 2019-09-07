package me.Cooltimmetje.Skuddbot.Minigames.Blackjack;

import lombok.Getter;
import lombok.Setter;

/**
 * This represents a regular playing card.
 *
 * @author Tim (Cooltimmetje)
 * @version v0.4.7-ALPHA
 * @since v0.4.5-ALPHA
 */
@Getter
@Setter
public class Card {

    private CardSuits suit;
    private CardRanks rank;

    public Card(){
        this.suit = CardSuits.random();
        this.rank = CardRanks.random();
    }

    public Card(CardSuits suit, CardRanks rank){
        this.suit = suit;
        this.rank = rank;
    }

    @Override
    public String toString(){
        return rank.getEmoji().getEmoji() + " " + suit.getEmoji().getEmoji();
    }

    @Override
    public boolean equals(Object object){
        if(!(object instanceof Card)) return false;
        Card card = (Card) object;

        return card.getRank() == this.rank && card.getSuit() == this.suit;
    }

}