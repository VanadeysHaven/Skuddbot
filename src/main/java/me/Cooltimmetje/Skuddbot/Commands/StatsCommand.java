package me.Cooltimmetje.Skuddbot.Commands;

import me.Cooltimmetje.Skuddbot.Enums.EmojiEnum;
import me.Cooltimmetje.Skuddbot.Enums.UserStats.UserStats;
import me.Cooltimmetje.Skuddbot.Enums.UserStats.UserStatsCats;
import me.Cooltimmetje.Skuddbot.Profiles.ProfileManager;
import me.Cooltimmetje.Skuddbot.Profiles.SkuddUser;
import me.Cooltimmetje.Skuddbot.Utilities.MessagesUtils;
import me.Cooltimmetje.Skuddbot.Utilities.MiscUtils;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.EmbedBuilder;

/**
 * Command that will generate a stats overview and print it out.
 *
 * @author Tim (Cooltimmetje)
 * @version v0.4.6-ALPHA
 * @since v0.4.42-ALPHA
 */
public class StatsCommand {

    public static void run(IMessage message) {
        IUser user = message.getAuthor();
        IGuild guild = message.getGuild();
        SkuddUser su = ProfileManager.getDiscord(user.getStringID(), guild.getStringID(), false);
        boolean otherUser = message.getMentions().size() >= 1;

        if (otherUser) {
            user = message.getMentions().get(0);
            su = ProfileManager.getDiscord(user.getStringID(), guild.getStringID(), false);
        }

        if (su == null) {
            String debug = otherUser ? "**" + user.getDisplayName(guild) + "** has no stats yet." : "You have no stats yet.";
            MessagesUtils.addReaction(message, debug, EmojiEnum.X);
            return;
        }

        if (su.isStatsPrivate() && otherUser) {
            MessagesUtils.addReaction(message, "**" + user.getDisplayName(guild) + "** has made their stats private.", EmojiEnum.X);
            return;
        }

        EmbedBuilder eb = new EmbedBuilder();

        eb.withAuthorIcon(user.getAvatarURL()).withAuthorName("Stats for: " + user.getDisplayName(guild));
        eb.withColor(MiscUtils.randomInt(0, 255), MiscUtils.randomInt(0, 255), MiscUtils.randomInt(0, 255));
        eb.withDesc("__Server:__ " + guild.getName());

        for (UserStatsCats category : UserStatsCats.values()){
            if(category.isShow()) {
                eb.appendField("\u200B", "__" + category.getName() + ":__",  false);

                for (UserStats stat : UserStats.values()) {
                    if (stat.isShow() && stat.getCategory() == category) {
                        eb.appendField("__" + stat.getDescription() + ":__", su.getStat(stat) + " " + stat.getStatSuffix(), true);
                    }
                }
            }
        }


        message.getChannel().sendMessage("" , eb.build());
    }

}
