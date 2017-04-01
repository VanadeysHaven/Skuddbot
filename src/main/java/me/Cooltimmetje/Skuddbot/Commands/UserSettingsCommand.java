package me.Cooltimmetje.Skuddbot.Commands;

import me.Cooltimmetje.Skuddbot.Enums.EmojiEnum;
import me.Cooltimmetje.Skuddbot.Enums.UserSettings;
import me.Cooltimmetje.Skuddbot.Profiles.ProfileManager;
import me.Cooltimmetje.Skuddbot.Profiles.SkuddUser;
import me.Cooltimmetje.Skuddbot.Utilities.MessagesUtils;
import org.apache.commons.lang3.StringUtils;
import sx.blah.discord.handle.obj.IMessage;

import java.text.MessageFormat;

/**
 * This is for users to edit their personal settings.
 *
 * @author Tim (Cooltimmetje)
 * @version v0.4-ALPHA-DEV
 * @since v0.4-ALPHA-DEV
 */
public class UserSettingsCommand {

    /**
     * This is running the !usersettings command. More detail in the code itself.
     *
     * @param message Message that triggered the command.
     */
    @SuppressWarnings("ConstantConditions")
    public static void run(IMessage message) {
        String[] args = message.getContent().split(" ");
        SkuddUser user = ProfileManager.getDiscord(message.getAuthor().getID(), message.getGuild().getID(), true);

        if(args.length == 1){ //No arguments: Show Settings.
            StringBuilder sb = new StringBuilder();

            sb.append(MessageFormat.format("User Settings for **{0}#{1}** | ID: `{2}`\n\n```\n", message.getAuthor().getName(), message.getAuthor().getDiscriminator(), message.getAuthor().getID()));

            int longest = 0;

            for(UserSettings setting : UserSettings.values()){
                int i = setting.toString().length();
                if(i > longest){
                    longest = i;
                }
            }
            if("Setting".length() > longest){
                longest = "Setting".length();
            }
            sb.append("Setting").append(StringUtils.repeat(" ", longest - "Setting".length())).append(" | Value\n");
            sb.append(StringUtils.repeat("-", longest)).append("-|-").append(StringUtils.repeat("-", longest)).append("\n");
            for(UserSettings setting : UserSettings.values()){
                assert user != null;
                sb.append(MessageFormat.format("{0} | {1}\n", setting.toString() + StringUtils.repeat(" ", longest - setting.toString().length()), user.getSetting(setting)));
            }
            sb.append("```\nType `!usersettings <name>` to view more info about it. Type `!usersettings <name> <value>` to change it's value!");

            MessagesUtils.sendPlain(sb.toString().trim(), message.getChannel(), false);
        } else if (args.length == 2){ //1 Argument: Show details.

            try {
                UserSettings setting = UserSettings.valueOf(message.getContent().split(" ")[1].toUpperCase());
                MessagesUtils.sendPlain(MessageFormat.format("```\n" +
                                "Setting:       {0}\n" +
                                "Description:   {1}\n" +
                                "Value:         {2}\n" +
                                "Default Value: {3}\n" +
                                "Value Type:    {4}\n" +
                                "```\n" +
                                "To alter the value type `!usersettings {5} <value>`.",
                        setting.toString(), setting.getDescription(), ProfileManager.getDiscord(message.getAuthor().getID(), message.getGuild().getID(), true).getSetting(setting),
                        setting.getDefaultValue(), setting.getType(), setting.toString()), message.getChannel(), false);
            } catch (IllegalArgumentException e){
                MessagesUtils.sendError("Unknown setting: " + message.getContent().split(" ")[1].toUpperCase(), message.getChannel());
            }

        } else if (args.length >= 3){

            UserSettings setting = null;
            try {
                setting = UserSettings.valueOf(args[1].toUpperCase());
            } catch (IllegalArgumentException e){
                MessagesUtils.sendError("Unknown setting: " + message.getContent().split(" ")[1].toUpperCase(), message.getChannel());
                return;
            }

            String value = args[2];
            String result = user.setSetting(setting, value);

            if(result == null){
                MessagesUtils.addReaction(message, MessageFormat.format("Setting `{0}` has been updated to `{1}`!" + ((setting == UserSettings.TRACK_ME && user.isLinked()) ? "\n" + EmojiEnum.WARNING.getEmoji() + " Since you have Twitch account linked, this change also applies on Twitch." : ""), setting.toString(), value), EmojiEnum.WHITE_CHECK_MARK.getEmoji());
            } else {
                MessagesUtils.sendError(result, message.getChannel());
            }
        }
    }

}
