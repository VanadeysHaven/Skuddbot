package me.Cooltimmetje.Skuddbot.Commands.Admin;

import me.Cooltimmetje.Skuddbot.Main;
import me.Cooltimmetje.Skuddbot.Profiles.MySqlManager;
import me.Cooltimmetje.Skuddbot.Profiles.Server;
import me.Cooltimmetje.Skuddbot.Profiles.ServerManager;
import me.Cooltimmetje.Skuddbot.Utilities.Constants;
import me.Cooltimmetje.Skuddbot.Utilities.Logger;
import me.Cooltimmetje.Skuddbot.Utilities.MessagesUtils;
import me.Cooltimmetje.Skuddbot.Utilities.MiscUtils;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.Permissions;

import java.text.MessageFormat;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by Tim on 8/22/2016.
 */
public class InitializeCommand {
    
    public static void run(final IMessage message){
        boolean hasAdmin = false;
        List<IRole> roles = message.getAuthor().getRolesForGuild(message.getGuild());
        for(IRole role : roles){
            if(role.getPermissions().contains(Permissions.ADMINISTRATOR)){
                hasAdmin = true;
            }
            if(hasAdmin) {
                break;
            }
        }
        if(!hasAdmin){
            hasAdmin = (message.getAuthor() == message.getGuild().getOwner()) || (message.getAuthor().getID().equals(Constants.TIMMY_ID));
        }
        boolean weHaveAdmin = false;
        List<IRole> ourRoles = Main.getInstance().getSkuddbot().getOurUser().getRolesForGuild(message.getGuild());
        for(IRole role : ourRoles){
            if(role.getPermissions().contains(Permissions.ADMINISTRATOR)){
                weHaveAdmin = true;
            }
            if(weHaveAdmin) {
                break;
            }
        }

        if(hasAdmin){
            if(weHaveAdmin){
                Server server = ServerManager.getServer(message.getGuild().getID());
                if(!server.isServerInitialized()){
                    MessagesUtils.sendSuccess("Allright, I'll initialize this server for you. This process can take up to 2 minutes, but should be finished in about 15 seconds, please be patient either way. I'll report back here when I'm ready.", message.getChannel());

                    int eta = MiscUtils.randomInt(10,30);
                    Logger.info(MessageFormat.format("Initializing {0} (ID: {1}) - ETA: {2} seconds",message.getGuild().getName(),message.getGuild().getID(),eta));
                    ScheduledThreadPoolExecutor exec = new ScheduledThreadPoolExecutor(1);
                    exec.schedule(()->{

                        MessagesUtils.sendSuccess("I'm done! Have fun using Skuddbot!\n\n" +
                                "The next thing you wanna do is configure Skuddbot's settings! Type `!settings`, you can always refer to the manual to get help! Type `!about` to find it!", message.getChannel());
                        server.clearProfiles();
                        MySqlManager.createServerTables(message.getGuild().getID());
                        server.setServerInitialized(true);
                    }, eta, TimeUnit.SECONDS);
                }
            } else {
                MessagesUtils.sendError("I don't have the ADMINISTRATOR permission!", message.getChannel());
            }
        }
    }
    
}
