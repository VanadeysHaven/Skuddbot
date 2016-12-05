package me.Cooltimmetje.Skuddbot.Commands.Admin;

import me.Cooltimmetje.Skuddbot.Profiles.MySqlManager;
import me.Cooltimmetje.Skuddbot.Utilities.MessagesUtils;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;

import java.io.File;
import java.io.IOException;

/**
 * Created by Tim on 9/4/2016.
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
