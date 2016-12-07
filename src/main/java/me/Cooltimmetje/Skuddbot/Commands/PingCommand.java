package me.Cooltimmetje.Skuddbot.Commands;

import me.Cooltimmetje.Skuddbot.Utilities.Constants;
import me.Cooltimmetje.Skuddbot.Utilities.MessagesUtils;
import sx.blah.discord.handle.obj.IMessage;

/**
 * When you ping, it pongs!
 *
 * @author Tim (Cooltimmetje)
 * @version v0.3-ALPHA-DEV
 * @since v0.3-ALPHA-DEV
 */
public class PingCommand {

    public static void run(IMessage message){
        MessagesUtils.sendSuccess((Constants.awesomePing.containsKey(message.getAuthor().getID()) ?
                Constants.awesomePing.get(message.getAuthor().getID()) : "PONG!"), message.getChannel());
    }

}
