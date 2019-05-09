package me.Cooltimmetje.Skuddbot.Enums.UserStats;

import lombok.Getter;

/**
 * Divides the statistics in categories.
 *
 * @author Tim (Cooltimmetje)
 * @version v0.4.6-ALPHA
 * @since v0.4.6-ALPHA
 */
@Getter
public enum UserStatsCats {

    NO_CATEGORY  ("No category",  false),
    CHALLENGE    ("Challenge",    true ),
    FREE_FOR_ALL ("Free for all", true ),
    BLACKJACK    ("Blackjack",    true );

    private String name;
    private boolean show;

    UserStatsCats(String name, boolean show){
        this.name = name;
        this.show = show;
    }

}
