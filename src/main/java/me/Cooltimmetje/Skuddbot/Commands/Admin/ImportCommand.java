package me.Cooltimmetje.Skuddbot.Commands.Admin;

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
 * @version v0.2-ALPHA
 * @since v0.2-ALPHA
 */
public class ImportCommand {

    public static void run(IMessage message) throws IOException {
        if (message.getAuthor().getID().equals(Constants.TIMMY_OVERRIDE)){
            File file = new File("/skuddbot/import.txt");
            int imported = 0;

            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;
                boolean skip = false;
                IUser user = null;
                int xp = 0;
                while ((line = br.readLine()) != null) {

                    String[] args = line.split(",");
                    List<IUser> list = Main.getInstance().getSkuddbot().getGuildByID(message.getGuild().getID()).getUsersByName(args[0]);
                    if(list.size() > 0){
                        user = list.get(0);
                    }
                    if(user != null){
                        xp = Integer.parseInt(args[1]);
                        SkuddUser su = ProfileManager.getDiscord(user.getID(), message.getGuild().getID(), true);
                        su.setXp(su.getXp() + xp);
                        imported++;
                    }

                }
                ServerManager.getServer(message.getGuild().getID()).save();
                MessagesUtils.sendSuccess("Done! Imported " + imported + " users!", message.getChannel());
            }
        } else {
            Logger.info(message.getAuthor().getName() + " attempted to do something they don't have permission for.");
        }
    }

}
