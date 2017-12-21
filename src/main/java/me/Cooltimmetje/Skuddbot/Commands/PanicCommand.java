package me.Cooltimmetje.Skuddbot.Commands;

import me.Cooltimmetje.Skuddbot.Main;
import me.Cooltimmetje.Skuddbot.Utilities.MessagesUtils;
import me.Cooltimmetje.Skuddbot.Utilities.MiscUtils;
import sx.blah.discord.handle.obj.IEmoji;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;

import java.util.List;

/**
 * Panic command, just for lockstar...
 *
 * @author jasch
 * @version I DON'T KNOW WHAT VERSION THIS IS!
 */
public class PanicCommand {
    public static void run(IMessage message){
        IGuild guild = message.getGuild();
        List<IEmoji> emojis = guild.getEmojis();

        if(message.getAuthor().getLongID() == 148376320726794240L){
            MessagesUtils.sendPlain("Go away jasch...", message.getChannel(), false);
        } else {
            MessagesUtils.sendPlain(((emojis.size() == 0) ? ("No emotes? PANIC!") : ("EVERYONE PANIC " + emojis.get(MiscUtils.randomInt(0, emojis.size())).toString())), message.getChannel(), false);
        }
    }
}
