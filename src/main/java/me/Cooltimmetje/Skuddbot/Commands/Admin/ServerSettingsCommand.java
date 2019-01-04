package me.Cooltimmetje.Skuddbot.Commands.Admin;

import me.Cooltimmetje.Skuddbot.Enums.EmojiEnum;
import me.Cooltimmetje.Skuddbot.Enums.ServerSettings;
import me.Cooltimmetje.Skuddbot.Enums.ServerSettingsCats;
import me.Cooltimmetje.Skuddbot.Profiles.ProfileManager;
import me.Cooltimmetje.Skuddbot.Profiles.Server;
import me.Cooltimmetje.Skuddbot.Profiles.ServerManager;
import me.Cooltimmetje.Skuddbot.Profiles.SkuddUser;
import me.Cooltimmetje.Skuddbot.Utilities.Constants;
import me.Cooltimmetje.Skuddbot.Utilities.Logger;
import me.Cooltimmetje.Skuddbot.Utilities.MessagesUtils;
import org.apache.commons.lang3.StringUtils;
import sx.blah.discord.handle.obj.IMessage;

import java.text.MessageFormat;

/**
 * This class allows server owners to view and alter settings to their liking.
 *
 * @author Tim (Cooltimmetje)
 * @version v0.4.51-ALPHA
 * @since v0.2-ALPHA
 */
public class ServerSettingsCommand {

    /**
     * CMD: Server owners can view and alter settings.
     * Please see the documentation in the code for more detail.
     *
     * @param message The message that this command got triggered off.
     */
    public static void run(IMessage message) {
        Server server = ServerManager.getServer(message.getGuild().getStringID());
        boolean allowAccess = ProfileManager.getDiscord(message.getAuthor(), message.getGuild(), true).hasElevatedPermissions();

        if (allowAccess) {
            if(message.getContent().split(" ").length == 1){ //No arguments: Show list of the settings.

                getSettings(null, message, server);

            } else if (message.getContent().split(" ").length == 2){ //1 argument: Show the setting that got specified in more detail or show the category

                try {
                    ServerSettingsCats category = ServerSettingsCats.valueOf(message.getContent().split(" ")[1].toUpperCase().replace('-','_'));
                    getSettings(category, message, server);
                } catch (IllegalArgumentException e){
                    try {
                        ServerSettings setting = ServerSettings.valueOf(message.getContent().split(" ")[1].toUpperCase().replace('-','_'));
                        MessagesUtils.sendPlain(MessageFormat.format("```\n" +
                                        "Setting:       {0}\n" +
                                        "Description:   {1}\n" +
                                        "Value:         {2}\n" +
                                        "Default Value: {3}\n" +
                                        "Value Type:    {4}\n" +
                                        "Category:      {5}\n" +
                                        "```\n" +
                                        "To alter the value type `!serversettings {6} <value>`.",
                                setting.toString(), setting.getDescription(), ServerManager.getServer(message.getGuild().getStringID()).getSetting(setting),
                                setting.getDefaultValue(), setting.getType(), setting.getCategory().toString(), setting.toString()), message.getChannel(), false);
                    } catch (IllegalArgumentException ex){
                        MessagesUtils.addReaction(message, "Unknown setting/category: " + message.getContent().split(" ")[1].toUpperCase().replace('-','_'), EmojiEnum.X);
                    }
                }



            } else if(message.getContent().split(" ").length > 2){ //2 or more arguments: Change the specified setting to the specified value.

                ServerSettings setting = null;
                try {
                    setting = ServerSettings.valueOf(message.getContent().split(" ")[1].toUpperCase().replace('-','_'));
                } catch (IllegalArgumentException e){
                    MessagesUtils.addReaction(message, "Unknown setting: " + message.getContent().split(" ")[1].toUpperCase().replace('-','_'), EmojiEnum.X);
                    return;
                }

                if(setting == ServerSettings.TWITCH_CHANNEL && !message.getContent().split(" ")[2].equalsIgnoreCase("null")){
                    SkuddUser su = ProfileManager.getDiscord(message.getAuthor().getStringID(), message.getGuild().getStringID(), true);
                    assert su != null; //FUCK YOU INTELLIJ, FUCK YOOOOUUUU (╯°□°）╯︵ ┻━┻
                    if(su.isLinked() && !Constants.adminUser.contains(message.getAuthor().getStringID())){
                        MessagesUtils.addReaction(message, "You do not have a Twitch Account linked, type '!twitch' to get started with linking!", EmojiEnum.X);
                        return;
                    }
                    if(!su.getTwitchUsername().equalsIgnoreCase(message.getContent().split(" ")[2]) && !Constants.adminUser.contains(message.getAuthor().getStringID())){
                        MessagesUtils.addReaction(message, "You can only set this value to your linked Twitch Account, which is " + su.getTwitchUsername() + "! (If this is incorrect, please contact a Skuddbot Admin.)", EmojiEnum.X);
                        return;
                    }
                }
                String value = message.getContent().split(" ")[2];
                if(setting == ServerSettings.WELCOME_MESSAGE || setting == ServerSettings.GOODBYE_MESSAGE || setting == ServerSettings.ADMIN_ROLE || setting == ServerSettings.ROLE_ON_JOIN || setting == ServerSettings.ARENA_NAME){
                    StringBuilder sb = new StringBuilder();
                    String[] args = message.getContent().split(" ");
                    for(int i=2; i < args.length; i++){
                        sb.append(args[i]).append(" ");
                    }
                    value = sb.toString().trim();
                }
                String result = null;
                if(setting == ServerSettings.TWITCH_CHANNEL) {
                    server.setTwitch(value);
                } else if (setting == ServerSettings.STREAM_LIVE && value.equalsIgnoreCase("false")){
                    server.runAnalytics(message.getChannel());
                    return;
                } else {
                    result = ServerManager.getServer(message.getGuild().getStringID()).setSetting(setting, value, false);
                }

                if(result == null){
                    MessagesUtils.addReaction(message, MessageFormat.format("Setting `{0}` has been updated to `{1}`!", setting.toString(), value), EmojiEnum.WHITE_CHECK_MARK);
                } else {
                    MessagesUtils.addReaction(message, result, EmojiEnum.X);
                }

            }
        } else {
            Logger.info(message.getAuthor().getName() + " attempted to do something they don't have permission for.");
        }
    }

    private static void getSettings(ServerSettingsCats category, IMessage message, Server server){
        StringBuilder sb = new StringBuilder();

        sb.append(MessageFormat.format("Server Settings for **{0}** {1} | ID: `{2}`\n\n```".trim(), message.getGuild().getName(), category != null ? "*(category: " + category.toString() + ")*" : "",message.getGuild().getStringID()));

        int longest = 0;

        for(ServerSettings setting : ServerSettings.values()){
            if(category == null || category == setting.getCategory()) {
                int i = setting.toString().length();
                if (i > longest) {
                    longest = i;
                }
            }
        }
        if("Setting".length() > longest){
            longest = "Setting".length();
        }
        sb.append("Setting").append(StringUtils.repeat(" ", longest - "Setting".length())).append(" | Value\n");
        sb.append(StringUtils.repeat("-", longest)).append("-|-").append(StringUtils.repeat("-", longest)).append("\n");
        for(ServerSettings setting : ServerSettings.values()){
            if(category == null || category == setting.getCategory()) {
                sb.append(MessageFormat.format("{0} | {1}\n", setting.toString() + StringUtils.repeat(" ", longest - setting.toString().length()),
                        ((((setting == ServerSettings.WELCOME_MESSAGE || setting == ServerSettings.GOODBYE_MESSAGE) && server.getSetting(setting) != null) && server.getSetting(setting).length() > longest) ? "<hidden - view info>" : server.getSetting(setting))));
            }
        }

        sb.append("```\nType `!serversettings <name>` to view more info about it. Type `!serversettings <name> <value>` to change it's value!");

        MessagesUtils.sendPlain(sb.toString(), message.getChannel(), false);
    }

}
