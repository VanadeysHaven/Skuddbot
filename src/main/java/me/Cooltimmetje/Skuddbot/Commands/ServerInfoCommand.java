package me.Cooltimmetje.Skuddbot.Commands;

import me.Cooltimmetje.Skuddbot.Utilities.Constants;
import me.Cooltimmetje.Skuddbot.Utilities.MiscUtils;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.util.EmbedBuilder;

/**
 * CMD: Shows useful server information.
 *
 * @author Tim (Cooltimmetje)
 * @version v0.4.31-ALPHA
 * @since v0.4.31-ALPHA
 */
public class ServerInfoCommand {

    public static void run(IMessage message){
        IGuild guild = message.getGuild();
        EmbedBuilder embed = new EmbedBuilder();

        embed.withColor(MiscUtils.randomInt(0,255), MiscUtils.randomInt(0,255), MiscUtils.randomInt(0,255));
        embed.withAuthorIcon(guild.getIconURL());
        embed.withAuthorName(guild.getName());
        embed.withThumbnail(guild.getIconURL());

        int channelCount = guild.getChannels().size() + guild.getVoiceChannels().size();
        embed.appendField("__Server ID:__", guild.getStringID(), false);
        embed.appendField("__Server Owner:__", guild.getOwner().getName()+"#"+guild.getOwner().getDiscriminator(), false);
        embed.appendField("__User Count:__", guild.getUsers().size()+"", true);
        embed.appendField("__Channel count:__", channelCount+"", true);
        embed.appendField("__Category count:__", guild.getCategories().size()+"", true);
        embed.appendField("__Default Channel:__", guild.getDefaultChannel().mention(), true);
        embed.appendField("__Region:__", guild.getRegion().toString(), true);

        embed.withFooterText("All this data is obtained through the public Discord API | Skuddbot " + Constants.config.get("version"));

        message.getChannel().sendMessage(embed.build());
    }

}
