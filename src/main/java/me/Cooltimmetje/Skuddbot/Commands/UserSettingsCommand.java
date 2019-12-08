package me.Cooltimmetje.Skuddbot.Commands;

import discord4j.core.object.entity.Message;
import me.Cooltimmetje.Skuddbot.Enums.EmojiEnum;
import me.Cooltimmetje.Skuddbot.Enums.UserSettings;
import me.Cooltimmetje.Skuddbot.Profiles.ProfileManager;
import me.Cooltimmetje.Skuddbot.Profiles.SkuddUser;
import me.Cooltimmetje.Skuddbot.Utilities.MessagesUtils;
import me.Cooltimmetje.Skuddbot.Utilities.TableUtilities.TableArrayGenerator;
import me.Cooltimmetje.Skuddbot.Utilities.TableUtilities.TableDrawer;
import me.Cooltimmetje.Skuddbot.Utilities.TableUtilities.TableRow;

import java.text.MessageFormat;

/**
 * This is for users to edit their personal settings.
 *
 * @author Tim (Cooltimmetje)
 * @version v0.5.1-ALPHA
 * @since v0.4-ALPHA-DEV
 */
public class UserSettingsCommand {

    /**
     * This is running the !usersettings command. More detail in the code itself.
     *
     * @param message Message that triggered the command.
     */
    @SuppressWarnings("ConstantConditions")
    public static void run(Message message) {
        String[] args = message.getContent().get().split(" ");
        SkuddUser user = ProfileManager.getDiscord(message.getAuthor().get().getId().asString(), message.getGuild().block().getId().asString(), true);

        if(args.length == 1){ //No arguments: Show Settings.
            StringBuilder sb = new StringBuilder();

            sb.append(MessageFormat.format("User Settings for **{0}#{1}** | ID: `{2}`\n\n```\n", message.getAuthor().get().getUsername(), message.getAuthor().get().getDiscriminator(), message.getAuthor().get().getId().asString()));

            TableArrayGenerator tag = new TableArrayGenerator(new TableRow("Setting", "Value"));
            for(UserSettings setting : UserSettings.values()){
                tag.addRow(new TableRow(setting.toString(), user.getSetting(setting)));
            }
            sb.append(new TableDrawer(tag).drawTable());

            sb.append("```\nType `!usersettings <name>` to view more info about it. Type `!usersettings <name> <value>` to change it's value!");

            MessagesUtils.sendPlain(sb.toString().trim(), message.getChannel().block(), false);
        } else if (args.length == 2){ //1 Argument: Show details.

            try {
                UserSettings setting = UserSettings.valueOf(message.getContent().get().split(" ")[1].toUpperCase().replace('-','_'));
                MessagesUtils.sendPlain(MessageFormat.format("```\n" +
                                "Setting:       {0}\n" +
                                "Description:   {1}\n" +
                                "Value:         {2}\n" +
                                "Default Value: {3}\n" +
                                "Value Type:    {4}\n" +
                                "```\n" +
                                "To alter the value type `!usersettings {5} <value>`.",
                        setting.toString(), setting.getDescription(), ProfileManager.getDiscord(message.getAuthor().get().getId().asString(), message.getGuild().block().getId().asString(), true).getSetting(setting),
                        setting.getDefaultValue(), setting.getType(), setting.toString()), message.getChannel().block(), false);
            } catch (IllegalArgumentException e){
                MessagesUtils.addReaction(message, "Unknown setting: " + message.getContent().get().split(" ")[1].toUpperCase(), EmojiEnum.X, false);
            }

        } else if (args.length >= 3){ //2 arguments: change value

            UserSettings setting = null;
            try {
                setting = UserSettings.valueOf(args[1].toUpperCase().replace('-','_'));
            } catch (IllegalArgumentException e){
                MessagesUtils.addReaction(message, "Unknown setting: " + message.getContent().get().split(" ")[1].toUpperCase(), EmojiEnum.X, false);
                return;
            }

            String value = args[2];
            String result = user.setSetting(setting, value);

            if(result == null){
                MessagesUtils.addReaction(message, MessageFormat.format("Setting `{0}` has been updated to `{1}`!" + ((setting == UserSettings.TRACK_ME && user.isLinked()) ? "\n" + EmojiEnum.WARNING.getUnicode() + " Since you have Twitch account linked, this change also applies on Twitch." : ""), setting.toString(), value), EmojiEnum.WHITE_CHECK_MARK, false);
            } else {
                MessagesUtils.addReaction(message, result, EmojiEnum.X, false);
            }
        }
    }

}
