package me.Cooltimmetje.Skuddbot.Commands.Admin.SuperAdmin;

import me.Cooltimmetje.Skuddbot.Main;
import me.Cooltimmetje.Skuddbot.Profiles.ProfileManager;
import me.Cooltimmetje.Skuddbot.Profiles.ServerManager;
import me.Cooltimmetje.Skuddbot.Profiles.SkuddUser;
import me.Cooltimmetje.Skuddbot.Utilities.Constants;
import me.Cooltimmetje.Skuddbot.Utilities.Logger;
import me.Cooltimmetje.Skuddbot.Utilities.MessagesUtils;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

/**
 * CMD: Imports XP form other bots (Like Meme6)
 *
 * @author Tim (Cooltimmetje)
 * @version v0.4.32-ALPHA
 * @since v0.2-ALPHA
 */
public class ImportCommand {

    public static void run(IMessage message) throws IOException {
        if (message.getAuthor().getLongID() == Constants.TIMMY_ID){
            File file = new File("/skuddbot/import.txt");
            int imported = 0;

            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;
                boolean skip = false;
                IUser user = null;
                int xp = 0;
                while ((line = br.readLine()) != null) {

                    String[] args = line.split(",");
                    List<IUser> list = Main.getInstance().getSkuddbot().getGuildByID(message.getGuild().getLongID()).getUsersByName(args[0]);
                    if(list.size() > 0){
                        user = list.get(0);
                    }
                    if(user != null){
                        xp = Integer.parseInt(args[1]);
                        SkuddUser su = ProfileManager.getDiscord(user.getStringID(), message.getGuild().getStringID(), true);
                        su.setXp(su.getXp() + xp);
                        imported++;
                    }

                }
                ServerManager.getServer(message.getGuild().getStringID()).save(false);
                MessagesUtils.sendSuccess("Done! Imported " + imported + " users!", message.getChannel());
            }
        } else {
            Logger.info(message.getAuthor().getName() + " attempted to do something they don't have permission for.");
        }
    }

}
