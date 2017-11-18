package me.Cooltimmetje.Skuddbot.Commands;

import me.Cooltimmetje.Skuddbot.Enums.EmojiEnum;
import me.Cooltimmetje.Skuddbot.Main;
import me.Cooltimmetje.Skuddbot.Profiles.ProfileManager;
import me.Cooltimmetje.Skuddbot.Profiles.ServerManager;
import me.Cooltimmetje.Skuddbot.Profiles.SkuddUser;
import me.Cooltimmetje.Skuddbot.Utilities.MessagesUtils;
import sx.blah.discord.handle.obj.IMessage;

/**
 * This class shows the user their XP and levels.
 *
 * @author Tim (Cooltimmetje)
 * @version v0.5-ALPHA-DEV
 * @since v0.1-ALPHA
 */
public class XpCommand {

    /**
     * CMD: Show the user their XP on the current server.
     *
     * @param message The message that triggered this command.
     */
    public static void run(IMessage message) { //return new int[]{exp, getXp(), needed, level};
        boolean mention = false;
        SkuddUser su = null;
        String[] args = message.getContent().split(" ");
        if (args.length > 1) {
            if (message.getMentions().size() >= 1) {
                su = ProfileManager.getDiscord(message.getMentions().get(0).getStringID(), message.getGuild().getStringID(), false);
            } else {
                su = ProfileManager.getTwitch(args[1].toLowerCase(), ServerManager.getServer(message.getGuild().getStringID()).getTwitchChannel(), false);
            }

            if (su == null) {
                MessagesUtils.addReaction(message, "The user you defined has no XP or doesn't exist.", EmojiEnum.X);
                return;
            }
            if(su.isXpPrivate()){
                MessagesUtils.addReaction(message, "This user has set their XP to private.", EmojiEnum.X);
                return;
            }
        } else {
            su = ProfileManager.getDiscord(message.getAuthor().getStringID(), message.getGuild().getStringID(), false);
            if(su == null){
                MessagesUtils.addReaction(message ,"It seems you haven't been chatting. So you don't have any XP :(", EmojiEnum.X);
                return;
            }
        }

        String name = (mention ? message.getAuthor().mention() : (su.getId() != null ? Main.getInstance().getSkuddbot().getUserByID(Long.parseLong(su.getId())).getDisplayName(message.getGuild()) : su.getTwitchUsername()));

        if(su.getTwitchUsername() != null){
            if(su.getTwitchUsername().equals("jaschmedia")){
                name = "JuiceMedia";
            }
        }

        int[] stats = su.calcXP(!mention, message);
        MessagesUtils.sendPlain("**" + name + " | Level: " + stats[3] + " | Level progress: " + stats[0] + "/" + stats[2] + " (" + (int) (((double) stats[0] / (double) stats[2]) * 100) + "%) | Total XP: " + stats[1] + "**", message.getChannel(), false);
    }

}