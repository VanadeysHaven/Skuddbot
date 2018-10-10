package me.Cooltimmetje.Skuddbot.Enums;

import lombok.Getter;

/**
 * This holds data about user statistics.
 *
 * @author Tim (Cooltimmetje)
 * @version v0.4.33-ALPHA
 * @since v0.4-ALPHA-DEV
 */
@Getter
public enum UserStats {

    MESSAGES_POSTED_STREAK ("message_streak",       "0", "Integer"),
    XP_GAIN_STREAK         ("xp_streak",            "0", "Integer"),
    CHAT_WALL_STREAK       ("chat_wall_streak",     "0", "Integer"),
    CHALLENGE_WIN_STREAK   ("challenge_win_streak", "0", "Integer");

    private String jsonReference;
    private String defaultValue;
    private String type;

    UserStats(String s, String s1, String s2) {
        this.jsonReference = s;
        this.defaultValue = s1;
        this.type = s2;
    }
}
