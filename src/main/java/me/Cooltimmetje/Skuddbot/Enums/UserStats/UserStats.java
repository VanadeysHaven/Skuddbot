package me.Cooltimmetje.Skuddbot.Enums.UserStats;

import lombok.Getter;

/**
 * This holds data about user statistics.
 *
 * @author Tim (Cooltimmetje)
 * @version v0.4.61-ALPHA
 * @since v0.4-ALPHA-DEV
 */
@Getter
public enum UserStats {

    MESSAGES_POSTED_STREAK   ("message_streak",           "0", "Integer", false, "",                           "",         UserStatsCats.NO_CATEGORY ),
    XP_GAIN_STREAK           ("xp_streak",                "0", "Integer", false, "",                           "",         UserStatsCats.NO_CATEGORY ),
    CHAT_WALL_STREAK         ("chat_wall_streak",         "0", "Integer", false, "",                           "",         UserStatsCats.NO_CATEGORY ),
    CHALLENGE_WINS           ("challenge_wins",           "0", "Integer", true,  "Challenge wins",             "wins",     UserStatsCats.CHALLENGE   ),
    CHALLENGE_LOSSES         ("challenge_losses",         "0", "Integer", true,  "Challenge losses",           "losses",   UserStatsCats.CHALLENGE   ),
    CHALLENGE_WIN_STREAK     ("challenge_win_streak",     "0", "Integer", true,  "Current challenge winstreak","wins",     UserStatsCats.CHALLENGE   ),
    CHALLENGE_LONGEST_STREAK ("challenge_longest_streak", "0", "Integer", true,  "Longest challenge winstreak","wins",     UserStatsCats.CHALLENGE   ),
    FFA_WINS                 ("ffa_wins",                 "0", "Integer", true,  "Free for all wins",          "wins",     UserStatsCats.FREE_FOR_ALL),
    FFA_LOSSES               ("ffa_losses",               "0", "Integer", true,  "Free for all losses",        "losses",   UserStatsCats.FREE_FOR_ALL),
    FFA_MOST_WIN             ("ffa_most_win",             "0", "Integer", true,  "FFA highest entrants win",   "entrants", UserStatsCats.FREE_FOR_ALL),
    FFA_KILLS                ("ffa_kills",                "0", "Integer", true,  "Free for all kills",         "kills",    UserStatsCats.FREE_FOR_ALL),
    BLACKJACK_WINS           ("blackjack_wins",           "0", "Integer", true,  "Blackjack wins",             "wins",     UserStatsCats.BLACKJACK   ),
    BLACKJACK_PUSHES         ("blackjack_pushes",         "0", "Integer", true,  "Blackjack pushes",           "pushes",   UserStatsCats.BLACKJACK   ),
    BLACKJACK_TWENTY_ONES    ("blackjack_twenty_one",     "0", "Integer", true,  "Blackjack 21's",             "21's",     UserStatsCats.BLACKJACK   ),
    BLACKJACK_LOSSES         ("blackjack_losses",         "0", "Integer", true,  "Blackjack losses",           "losses",   UserStatsCats.BLACKJACK   );

    private String jsonReference;
    private String defaultValue;
    private String type;
    private boolean show;
    private String description;
    private String statSuffix;
    private UserStatsCats category;

    UserStats(String jsonReference, String defaultValue, String type, boolean show, String description, String statSuffix, UserStatsCats category) {
        this.jsonReference = jsonReference;
        this.defaultValue = defaultValue;
        this.type = type;
        this.show = show;
        this.description = description;
        this.statSuffix = statSuffix;
        this.category = category;
    }
}
