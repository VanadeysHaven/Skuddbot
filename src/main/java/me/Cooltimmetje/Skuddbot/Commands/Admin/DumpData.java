package me.Cooltimmetje.Skuddbot.Commands.Admin;

import me.Cooltimmetje.Skuddbot.Utilities.MessagesUtils;
import sx.blah.discord.handle.obj.IMessage;

/**
 * CMD: Creates JSON dumps of the database.
 *
 * @author Tim (Cooltimmetje)
 * @version v0.1-ALPHA
 * @since v0.1-ALPHA
 */
public class DumpData {

    public static void run(IMessage message){
//        if(message.getAuthor().getRolesForGuild(message.getGuild()).contains(message.getGuild().getRolesByName("Mods").get(0))){
//            message.getChannel().toggleTypingStatus();
////            ProfileManager.saveAll();
//            MySqlManager.dumpDataToJSON();
//
//            try {
//                message.getChannel().sendFile(new File("dump.json"));
//            } catch (IOException | MissingPermissionsException | DiscordException | RateLimitException e) {
//                e.printStackTrace();
//            }
//        } else {
//            MessagesUtils.sendError("You do not have permission to do this!", message.getChannel());
//        }
        MessagesUtils.sendError("This command is currently disabled!", message.getChannel());
    }

}
