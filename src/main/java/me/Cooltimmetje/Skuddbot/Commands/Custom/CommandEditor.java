package me.Cooltimmetje.Skuddbot.Commands.Custom;

import me.Cooltimmetje.Skuddbot.Enums.EmojiEnum;
import me.Cooltimmetje.Skuddbot.Profiles.ProfileManager;
import me.Cooltimmetje.Skuddbot.Profiles.Server;
import me.Cooltimmetje.Skuddbot.Profiles.ServerManager;
import me.Cooltimmetje.Skuddbot.Profiles.SkuddUser;
import me.Cooltimmetje.Skuddbot.Utilities.MessagesUtils;
import sx.blah.discord.handle.obj.IMessage;

/**
 * This handles the creating, removing and editing of commands.
 *
 * @author Tim (Cooltimmetje)
 * @version v0.5-ALPHA
 * @since v0.5-ALPHA
 */
public class CommandEditor {

    private static final String COMMAND_USAGE = "!commands <add/edit/remove> <invoker>";

    public static void run(IMessage message){
        SkuddUser user = ProfileManager.getDiscord(message.getAuthor(), message.getGuild(), true);
        Server server = ServerManager.getServer(message.getGuild());
        String[] args = message.getContent().split(" ");

        if(!user.hasElevatedPermissions()){
            MessagesUtils.addReaction(message, "You do not have permission to edit commands.", EmojiEnum.X);
            return;
        }
        if(args.length < 2){
            MessagesUtils.addReaction(message, "Invalid usage. Usage: `" + COMMAND_USAGE + "`", EmojiEnum.X);
            return;
        }

        switch (args[1].toLowerCase()){
            case "add":
                server.addCommand(message);
                break;
            case "edit":
                server.editCommand(message);
                break;
            case "remove":
                break;
            default:
                MessagesUtils.addReaction(message, "Invalid usage. Usage: `" + COMMAND_USAGE + "`", EmojiEnum.X);
                break;
        }
    }



}
