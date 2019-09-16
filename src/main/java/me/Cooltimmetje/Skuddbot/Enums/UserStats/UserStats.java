package me.Cooltimmetje.Skuddbot.Enums.UserStats;

import lombok.Getter;

/**
 * This holds data about user statistics.
 *
 * @author Tim (Cooltimmetje)
 * @version v0.4.7-ALPHA
 * @since v0.4-ALPHA-DEV
 */
@Getter
public enum UserStats {

    MESSAGES_POSTED_STREAK   ("message_streak",           "0",  "Integer", false, "",                     "",         UserStatsCats.NO_CATEGORY,     false),
    XP_GAIN_STREAK           ("xp_streak",                "0",  "Integer", false, "",                     "",         UserStatsCats.NO_CATEGORY,     false),
    CHAT_WALL_STREAK         ("chat_wall_streak",         "0",  "Integer", false, "",                     "",         UserStatsCats.NO_CATEGORY,     false),
    CHALLENGE_WINS           ("challenge_wins",           "0",  "Integer", true,  "Wins",                 "wins",     UserStatsCats.CHALLENGE,       true ),
    CHALLENGE_LOSSES         ("challenge_losses",         "0",  "Integer", true,  "Losses",               "losses",   UserStatsCats.CHALLENGE,       true ),
    CHALLENGE_WIN_STREAK     ("challenge_win_streak",     "0",  "Integer", true,  "Current winstreak",    "wins",     UserStatsCats.CHALLENGE,       true ),
    CHALLENGE_LONGEST_STREAK ("challenge_longest_streak", "0",  "Integer", true,  "Longest winstreak",    "wins",     UserStatsCats.CHALLENGE,       true ),
    FFA_WINS                 ("ffa_wins",                 "0",  "Integer", true,  "Wins",                 "wins",     UserStatsCats.FREE_FOR_ALL,    true ),
    FFA_LOSSES               ("ffa_losses",               "0",  "Integer", true,  "Losses",               "losses",   UserStatsCats.FREE_FOR_ALL,    true ),
    FFA_MOST_WIN             ("ffa_most_win",             "0",  "Integer", true,  "Highest entrants win", "entrants", UserStatsCats.FREE_FOR_ALL,    true ),
    FFA_KILLS                ("ffa_kills",                "0",  "Integer", true,  "Kills",                "kills",    UserStatsCats.FREE_FOR_ALL,    true ),
    BJ_WINS                  ("blackjack_wins",           "0",  "Integer", true,  "Wins",                 "wins",     UserStatsCats.BLACKJACK,       true ),
    BJ_PUSHES                ("blackjack_pushes",         "0",  "Integer", true,  "Pushes",               "pushes",   UserStatsCats.BLACKJACK,       true ),
    BJ_TWENTY_ONES           ("blackjack_twenty_one",     "0",  "Integer", true,  "21's",                 "21's",     UserStatsCats.BLACKJACK,       true ),
    BJ_LOSSES                ("blackjack_losses",         "0",  "Integer", true,  "Losses",               "losses",   UserStatsCats.BLACKJACK,       true ),
    TD_WINS                  ("td_wins",                  "0",  "Integer", true,  "Wins",                 "wins",     UserStatsCats.TEAM_DEATHMATCH, true ),
    TD_LOSSES                ("td_losses",                "0",  "Integer", true,  "Losses",               "losses",   UserStatsCats.TEAM_DEATHMATCH, true ),
    TD_SAVES                 ("td_saves",                 "0",  "Integer", true,  "Teammate defences",    "defences", UserStatsCats.TEAM_DEATHMATCH, true ),
    TD_MOST_WIN              ("td_most_win",              "0",  "Integer", true,  "Highest entrants win", "entrants", UserStatsCats.TEAM_DEATHMATCH, true ),
    TD_ALL_SURVIVED          ("td_all_survived",          "0",  "Integer", true,  "Entire team survived", "times",    UserStatsCats.TEAM_DEATHMATCH, true ),
    TD_KILLS                 ("td_kills",                 "0",  "Integer", true,  "Kills",                "kills",    UserStatsCats.TEAM_DEATHMATCH, true ),
    TD_FAV_TEAMMATE          ("td_fav_teammate",          "{}", "String",  true,  "Favourite Teammate",   "",         UserStatsCats.TEAM_DEATHMATCH, false);

    private String jsonReference;
    private String defaultValue;
    private String type;
    private boolean show;
    private String description;
    private String statSuffix;
    private UserStatsCats category;
    private boolean hasLeaderboard;

    UserStats(String jsonReference, String defaultValue, String type, boolean show, String description, String statSuffix, UserStatsCats category, boolean hasLeaderboard) {
        this.jsonReference = jsonReference;
        this.defaultValue = defaultValue;
        this.type = type;
        this.show = show;
        this.description = description;
        this.statSuffix = statSuffix;
        this.category = category;
        this.hasLeaderboard = hasLeaderboard;
    }
}
