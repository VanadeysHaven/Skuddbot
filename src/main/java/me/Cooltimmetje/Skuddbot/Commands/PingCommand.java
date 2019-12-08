package me.Cooltimmetje.Skuddbot.Commands;

import discord4j.core.object.entity.Message;
import me.Cooltimmetje.Skuddbot.Utilities.Constants;
import me.Cooltimmetje.Skuddbot.Utilities.MessagesUtils;

/**
 * When you ping, it pongs!
 *
 * @author Tim (Cooltimmetje)
 * @version v0.5.1-ALPHA
 * @since v0.3-ALPHA-DEV
 */
public class PingCommand {

    /**
     * PONG MOTHERFUCKER!
     *
     * @param message The message that pinged.
     */
    public static void run(Message message){
        MessagesUtils.sendSuccess((Constants.awesomePing.getOrDefault(message.getAuthor().get().getId().asString(), "PONG!")), message.getChannel().block());
    }

}
