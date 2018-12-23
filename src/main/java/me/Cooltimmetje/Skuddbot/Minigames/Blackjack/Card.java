package me.Cooltimmetje.Skuddbot.Minigames.Blackjack;

import lombok.Getter;
import lombok.Setter;

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
        return suit.getEmoji().getEmoji() + " " + rank.getRankName() + " of " + suit.getSuitName();
    }

}