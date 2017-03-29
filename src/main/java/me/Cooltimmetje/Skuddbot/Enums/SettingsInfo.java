package me.Cooltimmetje.Skuddbot.Enums;

import lombok.Getter;

/**
 * Created by Tim on 9/8/2016.
 */
@Getter
public enum SettingsInfo {

    XP_MIN               ("The maximum amount of XP per message on Discord.",                                                                    "10",    "Integer"                         ),
    XP_MAX               ("The maximum amount of XP per message on Discord.",                                                                    "15",    "Integer"                         ),
    XP_TWITCH_MIN        ("The minimum amount of XP per message on Twitch.",                                                                     "10",    "Integer"                         ),
    XP_TWITCH_MAX        ("The maximum amount of XP per message on Twitch.",                                                                     "15",    "Integer"                         ),
    XP_BASE              ("The amount of XP level 1 wil require to level up. (See manual)",                                                      "1500",  "Integer"                         ),
    XP_MULTIPLIER        ("The multiplier that will be applied to each level. (See manual)",                                                     "1.2",   "Double"                          ),
    TWITCH_CHANNEL       ("This is the Twitch Channel that the bot should track for this server. (See manual)",                                  "NULL",  "String (of: Twitch Channel Name)"),
    WELCOME_MESSAGE      ("This will be posted in the channel that you specified when someone joins the server. (See manual)",                   "NULL",  "String"                          ),
    GOODBYE_MESSAGE      ("This will be posted in the channel that you specified when someone leaves the server. (See manual)",                  "NULL",  "String"                          ),
    WELCOME_GOODBYE_CHAN ("This is where the welcome/goodbye messages are posted. (See manual)",                                                 "NULL",  "String (of: Discord Channel ID)" ),
    ADMIN_ROLE           ("This role will be able to use the Admin commands of Skuddbot! (See manual)",                                          "NULL",  "String (of: Discord Role)"       ),
    ROLE_ON_JOIN         ("This role will be assigned to people that join the server. (See manual)",                                             "NULL",  "String (of: Discord Role)"       ),
    VR_MODE              ("This mode is for VR streamers, it'll put a exclamation mark and a space in front of messages in Twitch Chat.",        "false", "Boolean"                         );


    private String description;
    private String defaultValue;
    private String type;

    SettingsInfo(String s, String s1, String s2){
        this.description = s;
        this.defaultValue = s1;
        this.type = s2;
    }


}
