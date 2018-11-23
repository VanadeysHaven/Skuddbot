package me.Cooltimmetje.Skuddbot.Enums;

import lombok.Getter;

/**
 * Holds the info for User Settings.
 *
 * @author Tim (Cooltimmetje)
 * @version v0.4.2-ALPHA
 * @since v0.4-ALPHA-DEV
 */
@Getter
public enum UserSettings {

    LEVEL_UP_NOTIFY  ("level_up_notify",   "This defines how level up's will be announced for you, please see the manual what settings you can put here.", "0",     "Integer"),
    TRACK_ME         ("track_me",          "Defines if the bot will track you, think about XP and Analytics. Turning off PAUSES progress.",                "true",  "Boolean"),
    ANALYTICS_MENTION("analytics_mention", "Defines if the bot will mention you when you are on the analytics leaderboard!.",                              "true",  "Boolean"),
    STATS_PRIVATE    ("xp_private",        "This setting will turn off other users being able to see your stats using the !xp and !stats command",         "false", "Boolean"),
    MENTION_ME       ("mention_me",        "Defines if you will be mentioned in so called \"useless\" commands.",                                          "false", "Boolean");

    private String jsonReference;
    private String description;
    private String defaultValue;
    private String type;

    UserSettings(String s, String s1, String s2, String s3){
        this.jsonReference = s;
        this.description = s1;
        this.defaultValue = s2;
        this.type = s3;
    }

}
