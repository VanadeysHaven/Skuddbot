package me.Cooltimmetje.Skuddbot.Enums.ServerSettings;

import lombok.Getter;

/**
 * This holds the information for Server Settings.
 *
 * @author Tim (Cooltimmetje)
 * @version v0.4.6-ALPHA
 * @since v0.2-ALPHA
 */
@Getter
public enum ServerSettings {

    XP_MIN               ("xp_min",               "The minimum amount of XP per message on Discord.",                                                                           "10",                   "Integer",                          ServerSettingsCats.XP),
    XP_MAX               ("xp_max",               "The maximum amount of XP per message on Discord.",                                                                           "15",                   "Integer",                          ServerSettingsCats.XP),
    XP_MIN_TWITCH        ("xp_min_twitch",        "The minimum amount of XP per message on Twitch.",                                                                            "10",                   "Integer",                          ServerSettingsCats.XP),
    XP_MAX_TWITCH        ("xp_max_twitch",        "The maximum amount of XP per message on Twitch.",                                                                            "15",                   "Integer",                          ServerSettingsCats.XP),
    XP_BASE              ("xp_base",              "The amount of XP level 1 wil require to level up. (See manual)",                                                             "1500",                 "Integer",                          ServerSettingsCats.XP),
    XP_MULTIPLIER        ("xp_multiplier",        "The multiplier that will be applied to each level. (See manual)",                                                            "1.2",                  "Double",                           ServerSettingsCats.XP),
    TWITCH_CHANNEL       ("twitch_channel",       "This is the Twitch Channel that the bot should track for this server. (See manual)",                                         "NULL",                 "String (of: Twitch Channel Name)", ServerSettingsCats.TWITCH),
    WELCOME_MESSAGE      ("welcome_message",      "This will be posted in the channel that you specified when someone joins the server. (See manual)",                          "NULL",                 "String",                           ServerSettingsCats.WELCOME_GOODBYE),
    WELCOME_MSG_ATTACH   ("welcome_msg_attach",   "This image will be attached to the message that gets posted when someone joins the server.",                                 "NULL",                 "String (of URL to image)",         ServerSettingsCats.WELCOME_GOODBYE),
    GOODBYE_MESSAGE      ("goodbye_message",      "This will be posted in the channel that you specified when someone leaves the server. (See manual)",                         "NULL",                 "String",                           ServerSettingsCats.WELCOME_GOODBYE),
    GOODBYE_MSG_ATTACH   ("goodbye_msg_attach",   "This image will be attached to the message that gets posted when someone leaves the server.",                                "NULL",                 "String (of URL to image)",         ServerSettingsCats.WELCOME_GOODBYE),
    WELCOME_GOODBYE_CHAN ("welcome_goodbye_chan", "This is where the welcome/goodbye messages are posted. (See manual)",                                                        "NULL",                 "Long",                             ServerSettingsCats.WELCOME_GOODBYE),
    ADMIN_ROLE           ("admin_role",           "This role will be able to use the Admin commands of Skuddbot! (See manual)",                                                 "NULL",                 "String (of: Discord Role)",        ServerSettingsCats.DISCORD),
    ROLE_ON_JOIN         ("role_on_join",         "This role will be assigned to people that join the server. (See manual)",                                                    "NULL",                 "String (of: Discord Role)",        ServerSettingsCats.DISCORD),
    VR_MODE              ("vr_mode",              "This mode is for VR streamers, it'll put a exclamation mark and a space in front of messages in Twitch Chat.",               "false",                "Boolean",                          ServerSettingsCats.TWITCH),
    STREAM_LIVE          ("stream_live",          "If set to true, the bot logs the chat for use in analytics. You should not need to change this at all if you have MuxyBot.", "false",                "Boolean",                          ServerSettingsCats.ANALYTICS),
    ALLOW_ANALYTICS      ("allow_analytics",      "This setting defines if analytics are enabled. Automatic analytics require MuxyBot.",                                        "true",                 "Boolean",                          ServerSettingsCats.ANALYTICS),
    ALLOW_REWARDS        ("allow_rewards",        "This setting defines if people will gain rewards from analytics. (See manual)",                                              "true",                 "Boolean",                          ServerSettingsCats.ANALYTICS),
    ARENA_NAME           ("arena_name",           "This setting defines the arena name used in the !challenge command.",                                                        "Skuddbot's Colosseum", "String",                           ServerSettingsCats.MINI_GAMES);

    private String jsonReference;
    private String description;
    private String defaultValue;
    private String type;
    private ServerSettingsCats category;

    ServerSettings(String s, String s1, String s2, String s3, ServerSettingsCats ssc){
        this.jsonReference = s;
        this.description = s1;
        this.defaultValue = s2;
        this.type = s3;
        this.category = ssc;
    }


}
