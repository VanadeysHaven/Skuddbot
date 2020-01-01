package me.Cooltimmetje.Skuddbot.Commands.Admin.SuperAdmin;

import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.MessageChannel;
import me.Cooltimmetje.Skuddbot.Utilities.MessagesUtils;

/**
 * Command used for leaving servers.
 *
 * @author Tim (Cooltimmetje)
 * @version ALPHA-2.0
 * @since ALPHA-2.0
 */
public class LeaveCommand {

    public static void run(Message message){
        MessagesUtils.sendSuccess("Okay, I will leave this server. Goodbye.", (MessageChannel) message.getChannel());
        message.getGuild().block().leave().block();
    }

}
