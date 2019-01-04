package me.Cooltimmetje.Skuddbot.Minigames.Blackjack;

import lombok.Getter;
import me.Cooltimmetje.Skuddbot.Enums.EmojiEnum;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Contains all ranks of playing cards, and their blackjack value.
 *
 * @author Tim (Cooltimmetje)
 * @version v0.4.51-ALPHA
 * @since v0.4.5-ALPHA
 */
@Getter
public enum CardRanks {

    ACE   (-1, "Ace",   EmojiEnum.A     ),
    TWO   (2,  "Two",   EmojiEnum.TWO   ),
    THREE (3,  "Three", EmojiEnum.THREE ),
    FOUR  (4,  "Four",  EmojiEnum.FOUR  ),
    FIVE  (5,  "Five",  EmojiEnum.FIVE  ),
    SIX   (6,  "Six",   EmojiEnum.SIX   ),
    SEVEN (7,  "Seven", EmojiEnum.SEVEN ),
    EIGHT (8,  "Eight", EmojiEnum.EIGHT ),
    NINE  (9,  "Nine",  EmojiEnum.NINE  ),
    TEN   (10, "Ten",   EmojiEnum.TEN   ),
    JACK  (10, "Jack",  EmojiEnum.J     ),
    QUEEN (10, "Queen", EmojiEnum.Q     ),
    KING  (10, "King",  EmojiEnum.K     );

    private int value;
    private String rankName;
    private EmojiEnum emoji;

    CardRanks(int value, String rankName, EmojiEnum emoji){
        this.value = value;
        this.rankName = rankName;
        this.emoji = emoji;
    }

    private static final List<CardRanks> VALUES =
            Collections.unmodifiableList(Arrays.asList(values()));
    private static final int SIZE = VALUES.size();
    private static final Random RANDOM = new Random();

    public static CardRanks random()  {
        return VALUES.get(RANDOM.nextInt(SIZE));
    }

}
