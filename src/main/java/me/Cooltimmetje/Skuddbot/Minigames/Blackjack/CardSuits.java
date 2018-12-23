package me.Cooltimmetje.Skuddbot.Minigames.Blackjack;

import lombok.Getter;
import me.Cooltimmetje.Skuddbot.Enums.EmojiEnum;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@Getter
public enum CardSuits {

    SPADES   ("Spades",   EmojiEnum.SPADES  ),
    CLUBS    ("Clubs",    EmojiEnum.CLUBS   ),
    DIAMONDS ("Diamonds", EmojiEnum.DIAMONDS),
    HEARTS   ("Hearts",   EmojiEnum.HEARTS  );

    private String suitName;
    private EmojiEnum emoji;

    CardSuits(String suitName, EmojiEnum emoji){
        this.suitName = suitName;
        this.emoji = emoji;
    }

    private static final List<CardSuits> VALUES =
            Collections.unmodifiableList(Arrays.asList(values()));
    private static final int SIZE = VALUES.size();
    private static final Random RANDOM = new Random();

    public static CardSuits random()  {
        return VALUES.get(RANDOM.nextInt(SIZE));
    }

}
