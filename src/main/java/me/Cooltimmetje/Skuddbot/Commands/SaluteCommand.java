package me.Cooltimmetje.Skuddbot.Commands;

import me.Cooltimmetje.Skuddbot.Utilities.MessagesUtils;
import me.Cooltimmetje.Skuddbot.Utilities.MiscUtils;
import sx.blah.discord.handle.obj.IEmoji;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;

import java.util.List;

/**
 * o7
 *
 * @author Tim (Cooltimmetje)
 * @version v0.4.01-ALPHA-DEV
 * @since v0.4.01-ALPHA-DEV
 */
public class SaluteCommand {

    public static void run(IMessage message){
        IGuild guild = message.getGuild();
        List<IEmoji> emojis = guild.getEmojis();

        if((message.getAuthor().getLongID() == 91949596737011712L) && (guild.getLongID() == 157774629975490561L)){
            MessagesUtils.sendPlain(guild.getEmojiByID(293018187665244160L).toString() + "7", message.getChannel(), false);
        } else {
            MessagesUtils.sendPlain(((emojis.size() == 0) ? ("o") : (emojis.get(MiscUtils.randomInt(0, emojis.size())).toString())) + "7", message.getChannel(), false);
        }
    }

}
