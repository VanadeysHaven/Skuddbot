package me.Cooltimmetje.Skuddbot.Minigames.Blackjack;

import lombok.Getter;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@Getter
public enum CardRanks {

    ACE   (-1, "Ace"   ),
    TWO   (2,  "Two"   ),
    THREE (3,  "Three" ),
    FOUR  (4,  "Four"  ),
    FIVE  (5,  "Five"  ),
    SIX   (6,  "Six"   ),
    SEVEN (7,  "Seven" ),
    EIGHT (8,  "Eight" ),
    NINE  (9,  "Nine"  ),
    TEN   (10, "Ten"   ),
    JACK  (10, "Jack"  ),
    QUEEN (10, "Queen" ),
    KING  (10, "King"  );

    private int value;
    private String rankName;

    CardRanks(int value, String rankName){
        this.value = value;
        this.rankName = rankName;
    }

    private static final List<CardRanks> VALUES =
            Collections.unmodifiableList(Arrays.asList(values()));
    private static final int SIZE = VALUES.size();
    private static final Random RANDOM = new Random();

    public static CardRanks random()  {
        return VALUES.get(RANDOM.nextInt(SIZE));
    }

}
