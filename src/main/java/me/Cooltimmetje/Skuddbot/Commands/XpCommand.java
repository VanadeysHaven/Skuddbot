package me.Cooltimmetje.Skuddbot.Commands;

import discord4j.core.object.entity.Message;
import discord4j.core.object.util.Snowflake;
import me.Cooltimmetje.Skuddbot.Enums.EmojiEnum;
import me.Cooltimmetje.Skuddbot.Main;
import me.Cooltimmetje.Skuddbot.Profiles.ProfileManager;
import me.Cooltimmetje.Skuddbot.Profiles.ServerManager;
import me.Cooltimmetje.Skuddbot.Profiles.SkuddUser;
import me.Cooltimmetje.Skuddbot.Utilities.EmojiHelper;
import me.Cooltimmetje.Skuddbot.Utilities.MessagesUtils;

import java.text.MessageFormat;

/**
 * This class shows the user their XP and levels.
 *
 * @author Tim (Cooltimmetje)
 * @version v0.5.1-ALPHA
 * @since v0.1-ALPHA
 */
public class XpCommand {

    /**
     * CMD: Show the user their XP on the current server.
     *
     * @param message The message that triggered this command.
     */
    public static void run(Message message) { //return new int[]{exp, getXp(), needed, level};
        boolean mention = false;
        SkuddUser su = null;
        String[] args = message.getContent().get().split(" ");
        if (args.length > 1) {
            if (message.getUserMentions().collectList().block().size() >= 1) {
                su = ProfileManager.getDiscord(message.getUserMentions().collectList().block().get(0).getId().asString(), message.getGuild().block().getId().asString(), false);
            } else {
                su = ProfileManager.getTwitch(args[1].toLowerCase(), ServerManager.getServer(message.getGuild().block().getId().asString()).getTwitchChannel(), false);
            }

            if (su == null) {
                MessagesUtils.addReaction(message, "The user you defined has no XP or doesn't exist.", EmojiEnum.X, false);
                return;
            }
            if(su.isStatsPrivate()){
                MessagesUtils.addReaction(message, "This user has set their XP to private.", EmojiEnum.X, false);
                return;
            }
        } else {
            su = ProfileManager.getDiscord(message.getAuthor().get().getId().asString(), message.getGuild().block().getId().asString(), false);
            if(su == null){
                MessagesUtils.addReaction(message ,"It seems you haven't been chatting. So you don't have any XP :(", EmojiEnum.X, false);
                return;
            }
        }

        String name = (mention ? message.getAuthor().get().getMention() : (su.getId() != null ? Main.getInstance().getSkuddbot().getUserById(Snowflake.of(su.getId())).block().asMember(message.getGuild().block().getId()).block().getDisplayName() : su.getTwitchUsername()));

        if(su.getTwitchUsername() != null){
            if(su.getTwitchUsername().equals("jaschmedia")){
                name = "JuiceMedia";
            }
        }

        int[] stats = su.calcXP(mention, message);
        int progress = (int) (((double) stats[0] / (double) stats[2]) * 100);
        MessagesUtils.sendPlain(MessageFormat.format("{0} **{1} | Level: {2} | Level progress: {3}/{4} ({5}%) | Total XP: {6}**",
                EmojiHelper.getEmoji("xp_icon"), name, stats[3]+"", stats[0]+"", stats[2]+"", progress+"", stats[1]+""), message.getChannel().block(), false);
    }

}