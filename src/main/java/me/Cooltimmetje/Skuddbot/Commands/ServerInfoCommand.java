package me.Cooltimmetje.Skuddbot.Commands;

import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Message;
import discord4j.core.object.util.Image;
import discord4j.core.spec.EmbedCreateSpec;
import me.Cooltimmetje.Skuddbot.Utilities.Constants;
import me.Cooltimmetje.Skuddbot.Utilities.MiscUtils;

import java.awt.*;
import java.util.function.Consumer;

/**
 * CMD: Shows useful server information.
 *
 * @author Tim (Cooltimmetje)
 * @version v0.5.1-ALPHA
 * @since v0.4.31-ALPHA
 */
public class ServerInfoCommand {

    public static void run(Message message){
        Guild guild = message.getGuild().block();
        Consumer<EmbedCreateSpec> template = spec -> {
            spec.setAuthor(guild.getName(), null, guild.getIconUrl(Image.Format.PNG).get());
            spec.setColor(new Color(MiscUtils.randomInt(0,255), MiscUtils.randomInt(0,255), MiscUtils.randomInt(0,255)));
            spec.setThumbnail(guild.getIconUrl(Image.Format.PNG).get());

            int channelCount = guild.getChannels().collectList().block().size();
            spec.addField("__Server ID:__", guild.getId().asString(), false);
            spec.addField("__Server Owner:__", guild.getOwner().block().getUsername()+"#"+guild.getOwner().block().getDiscriminator(), false);
            spec.addField("__User Count:__", guild.getMemberCount().getAsInt()+"", true);
            spec.addField("__Channel count:__", channelCount+"", true);
//            spec.addField("__Category count:__", guild.get.size()+"", true);
//            spec.addField("__Default Channel:__", guild.getChannel().mention(), true);
            spec.addField("__Region:__", guild.getRegion().toString(), true);

            spec.setFooter("All this data is obtained through the public Discord API | Skuddbot " + Constants.config.get("version"), null);
        };

        message.getChannel().block().createMessage(msgSpec -> {
           msgSpec.setEmbed(template);
        });
        
    }

}
