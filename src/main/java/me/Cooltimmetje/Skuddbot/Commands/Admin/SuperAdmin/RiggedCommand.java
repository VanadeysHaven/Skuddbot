package me.Cooltimmetje.Skuddbot.Commands.Admin.SuperAdmin;

import me.Cooltimmetje.Skuddbot.Enums.EmojiEnum;
import me.Cooltimmetje.Skuddbot.Utilities.Constants;
import me.Cooltimmetje.Skuddbot.Utilities.MessagesUtils;
import sx.blah.discord.handle.obj.IMessage;

/**
 * Nothing to see here, move along.
 * DISCLAIMER: THIS COMMAND IS FOR DEBUGGING PURPOSES ONLY, STAFF WILL NOT ABUSE THIS TO "rig shit".
 *
 * @author Tim (Cooltimmetje)
 * @version v0.4.32-ALPHA
 * @since v0.4.3-ALPHA
 */
public class RiggedCommand {

    public static void run(IMessage message) {
        if(Constants.adminUser.contains(message.getAuthor().getStringID())){
            boolean inverted = message.getContent().toLowerCase().contains("-inv");
            Constants.rigged.put(message.getAuthor().getStringID(), !inverted);
            MessagesUtils.addReaction(message, "Rigged it! - The next !challenge you participate in will be an automatic " + (inverted ? "loss" : "win") + " for you.", EmojiEnum.EYES);
        }
    }

}
