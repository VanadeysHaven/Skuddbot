package me.Cooltimmetje.Skuddbot.Enums;

import lombok.Getter;

/**
 * This holds data about user statistics.
 *
 * @author Tim (Cooltimmetje)
 * @version v0.4.41-ALPHA
 * @since v0.4-ALPHA-DEV
 */
@Getter
public enum UserStats {

    MESSAGES_POSTED_STREAK   ("message_streak",           "0", "Integer", false, "",                           ""        ),
    XP_GAIN_STREAK           ("xp_streak",                "0", "Integer", false, "",                           ""        ),
    CHAT_WALL_STREAK         ("chat_wall_streak",         "0", "Integer", false, "",                           ""        ),
    CHALLENGE_WINS           ("challenge_wins",           "0", "Integer", true,  "Challenge wins",             "wins"    ),
    CHALLENGE_LOSSES         ("challenge_losses",         "0", "Integer", true,  "Challenge losses",           "losses"  ),
    CHALLENGE_WIN_STREAK     ("challenge_win_streak",     "0", "Integer", true,  "Current challenge winstreak","wins"    ),
    CHALLENGE_LONGEST_STREAK ("challenge_longest_streak", "0", "Integer", true,  "Longest challenge winstreak","wins"    ),
    FFA_WINS                 ("ffa_wins",                 "0", "Integer", true,  "Free for all wins",          "wins"    ),
    FFA_LOSSES               ("ffa_losses",               "0", "Integer", true,  "Free for all losses",        "losses"  ),
    FFA_MOST_WIN             ("ffa_most_win",             "0", "Integer", true,  "FFA highest entrants win",   "entrants"),
    BLACKJACK_WINS           ("blackjack_wins",           "0", "Integer", true,  "Blackjack wins",             "wins"    ),
    BLACKJACK_PUSHES         ("blackjack_pushes",         "0", "Integer", true,  "Blackjack pushes",           "pushes"  ),
    BLACKJACK_TWENTY_ONES    ("blackjack_twenty_one",     "0", "Integer", true,  "Blackjack 21's",             "21's"    ),
    BLACKJACK_LOSSES         ("blackjack_losses",         "0", "Integer", true,  "Blackjack losses",           "losses"  );

    private String jsonReference;
    private String defaultValue;
    private String type;
    private boolean showInStats;
    private String description;
    private String statSuffix;

    UserStats(String s, String s1, String s2, boolean b, String s3, String s4) {
        this.jsonReference = s;
        this.defaultValue = s1;
        this.type = s2;
        this.showInStats = b;
        this.description = s3;
        this.statSuffix = s4;
    }
}
