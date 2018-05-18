package me.Cooltimmetje.Skuddbot.Commands.Admin.SuperAdmin;

import me.Cooltimmetje.Skuddbot.Commands.ChallengeCommand;
import me.Cooltimmetje.Skuddbot.Enums.EmojiEnum;
import me.Cooltimmetje.Skuddbot.Utilities.Constants;
import me.Cooltimmetje.Skuddbot.Utilities.MessagesUtils;
import sx.blah.discord.handle.obj.IMessage;

/**
 * This command allows admins to clear all cooldowns that there are in the bot.
 *
 * @author Tim (Cooltimmetje)
 * @version v0.4.3-ALPHA
 * @since v0.4.3-ALPHA
 */
public class ClearCooldownCommand {

    public static void run(IMessage message){
        if(Constants.adminUser.contains(message.getAuthor().getStringID())){
            ChallengeCommand.cooldowns.clear();

            MessagesUtils.addReaction(message, "All cooldowns have been cleared.", EmojiEnum.WHITE_CHECK_MARK);
        }
    }

}
