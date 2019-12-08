package me.Cooltimmetje.Skuddbot.Commands.Useless;

import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.GuildEmoji;
import discord4j.core.object.entity.Message;
import me.Cooltimmetje.Skuddbot.Utilities.Constants;
import me.Cooltimmetje.Skuddbot.Utilities.MessagesUtils;
import me.Cooltimmetje.Skuddbot.Utilities.MiscUtils;

import java.util.List;

/**
 * Panic command, just for lockstar...
 *
 * @author jasch, Tim
 * @version v0.5.1-ALPHA
 * @since v0.4.1-ALPHA
 */
public class PanicCommand {
    public static void run(Message message){
        Guild guild = message.getGuild().block();
        List<GuildEmoji> emojis = guild.getEmojis().collectList().block();

        if(message.getAuthor().get().getId().asLong() == Constants.JASCH_ID){
            MessagesUtils.sendPlain("Go away jasch...", message.getChannel().block(), false);
        } else {
            MessagesUtils.sendPlain(((emojis.size() == 0) ? ("No emotes? PANIC!") : ("EVERYONE PANIC " + emojis.get(MiscUtils.randomInt(0, emojis.size())).toString())), message.getChannel().block(), false);
        }
    }
}
