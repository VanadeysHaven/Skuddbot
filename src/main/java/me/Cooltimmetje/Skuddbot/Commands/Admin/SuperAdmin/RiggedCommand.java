package me.Cooltimmetje.Skuddbot.Commands.Admin.SuperAdmin;

import discord4j.core.object.entity.Message;
import me.Cooltimmetje.Skuddbot.Enums.EmojiEnum;
import me.Cooltimmetje.Skuddbot.Utilities.Constants;
import me.Cooltimmetje.Skuddbot.Utilities.MessagesUtils;

/**
 * Nothing to see here, move along.
 * DISCLAIMER: THIS COMMAND IS FOR DEBUGGING PURPOSES ONLY, STAFF WILL NOT ABUSE THIS TO "rig shit".
 *
 * @author Tim (Cooltimmetje)
 * @version v0.5.1-ALPHA
 * @since v0.4.3-ALPHA
 */
public class RiggedCommand {

    public static void run(Message message) {
        if(Constants.adminUser.contains(message.getAuthor().get().getId().asString())){
            boolean inverted = message.getContent().get().toLowerCase().contains("-inv");
            Constants.rigged.put(message.getAuthor().get().getId().asString(), !inverted);
            MessagesUtils.addReaction(message, "Rigged it! - The next !challenge you participate in will be an automatic " + (inverted ? "loss" : "win") + " for you.", EmojiEnum.EYES, false);
        }
    }

}
