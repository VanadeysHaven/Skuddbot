package me.Cooltimmetje.Skuddbot.Utilities;

import me.Cooltimmetje.Skuddbot.Profiles.SkuddUser;

import java.util.HashMap;

/**
 * Created by Tim on 8/2/2016.
 */
public class Constants {

    public static String SKUDDBOT_MENTION = "<@209779500018434058>";
    public static String TIMMY_ID = "76593288865394688";
    public static String JASCH_ID = "148376320726794240";
    public static String twitchBot;
    public static String twitchOauth;

    public static int PROFILES_IN_MEMORY = 0;
    public static long STARTUP_TIME;

    public static int BASE_LEVEL = 1500;
    public static double LEVEL_MULTIPLIER = 1.2;
    public static int MIN_GAIN = 10;
    public static int MIN_GAIN_TWITCH = 10;
    public static int MAX_GAIN = 15;
    public static int MAX_GAIN_TWITCH = 15;

    public static boolean MUTED = false;

    public static int INACTIVE_DELAY = 600000;

    public static HashMap<String,SkuddUser> verifyCodes = new HashMap<>();
    public static HashMap<String,String> config = new HashMap<>();

}
