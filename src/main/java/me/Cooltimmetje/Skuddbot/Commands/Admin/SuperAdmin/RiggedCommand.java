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
 * @version v0.4.3-ALPHA
 * @since v0.4.3-ALPHA
 */
public class RiggedCommand {

    public static void run(IMessage message) {
        if(Constants.adminUser.contains(message.getAuthor().getStringID())){
            if(Constants.rigged.contains(message.getAuthor().getStringID())){
                MessagesUtils.addReaction(message, "It's already rigged, go ahead and participate in a challenge, I'll let you win.", EmojiEnum.X);
                return;
            }
            Constants.rigged.add(message.getAuthor().getStringID());
            MessagesUtils.addReaction(message, "Rigged it! - The next !challenge you participate in will be an automatic win for you.", EmojiEnum.EYES);
        }
    }

}
