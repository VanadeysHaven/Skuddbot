package me.Cooltimmetje.Skuddbot.Commands;

import me.Cooltimmetje.Skuddbot.Main;
import me.Cooltimmetje.Skuddbot.Profiles.ProfileManager;
import me.Cooltimmetje.Skuddbot.Profiles.ServerManager;
import me.Cooltimmetje.Skuddbot.Profiles.SkuddUser;
import me.Cooltimmetje.Skuddbot.Utilities.MessagesUtils;
import sx.blah.discord.handle.obj.IMessage;

/**
 * Created by Tim on 8/4/2016.
 */
public class XpCommand {

    public static void run(IMessage message) { //return new int[]{exp, getXp(), needed, level};
        boolean mention = false;
        SkuddUser su = null;
        String[] args = message.getContent().split(" ");
        if (args.length > 1) {
            if (message.getMentions().size() >= 1) {
                su = ProfileManager.getDiscord(message.getMentions().get(0).getID(), message.getGuild().getID(), false);
            } else {
                su = ProfileManager.getTwitch(args[1].toLowerCase(), ServerManager.getServer(message.getGuild().getID()).getTwitchChannel(), false);
            }
        }
        if (su == null) {
            su = ProfileManager.getDiscord(message.getAuthor().getID(), message.getGuild().getID(), false);
            mention = true;
        }

        String name = (mention ? message.getAuthor().mention() : (su.getId() != null ? Main.getInstance().getSkuddbot().getUserByID(su.getId()).getDisplayName(message.getGuild()) : su.getTwitchUsername()));

        assert su != null;
        if(su.getTwitchUsername() != null){
            if(su.getTwitchUsername().equals("jaschmedia")){
                name = "JuiceMedia";
            }
        }

        int[] stats = su.calcXP(!mention, message);
        MessagesUtils.sendPlain("**" + name + " | Level: " + stats[3] + " | Level progress: " + stats[0] + "/" + stats[2] + " (" + (int) (((double) stats[0] / (double) stats[2]) * 100) + "%) | Total XP: " + stats[1] + "**", message.getChannel());
    }

}