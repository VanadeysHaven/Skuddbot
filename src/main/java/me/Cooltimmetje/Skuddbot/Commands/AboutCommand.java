package me.Cooltimmetje.Skuddbot.Commands;

import me.Cooltimmetje.Skuddbot.Utilities.Constants;
import me.Cooltimmetje.Skuddbot.Utilities.MiscUtils;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;

/**
 * This class shows some information about the bot, such as the current branch, commit, and the manual.
 *
 * @author Tim (Cooltimmetje)
 * @version v0.3-ALPHA
 * @since v0.2-ALPHA
 */
public class AboutCommand {

    /**
     * CMD: This shows some information about Skuddbot.
     *
     * @param message This is the message that triggered the command.
     */
    public static void run(IMessage message){
        EmbedBuilder embed = new EmbedBuilder();
        embed.withAuthorIcon("http://i.imgur.com/v1vlVru.png").withAuthorName("Skuddbot " + Constants.config.get("version")).withThumbnail("http://i.imgur.com/v1vlVru.png");

        embed.withColor(MiscUtils.randomInt(0,255), MiscUtils.randomInt(0,255), MiscUtils.randomInt(0,255));

        embed.appendField("__Deployed on:__", Constants.config.get("deployed_on"), true);
        embed.appendField("__Built on:__", Constants.config.get("built_on"), true);
        embed.appendField("__Branch:__", Constants.config.get("branch"), true);
        embed.appendField("__Deployed from:__", "`" + Constants.config.get("deployed_from") + "`", true);
        embed.appendField("__Users in memory:__", Constants.PROFILES_IN_MEMORY+"", true);
        embed.appendField("__Manual:__", Constants.config.get("manual"), true);
        embed.appendField("__Privacy Statement:__", Constants.config.get("privacy_statement"), true);
        embed.appendField("__Changelog:__", Constants.config.get("changelog"), true);

        try {
            message.getChannel().sendMessage("", embed.build(), false);
        } catch (RateLimitException | DiscordException | MissingPermissionsException e) {
            e.printStackTrace();
        }
    }

}
