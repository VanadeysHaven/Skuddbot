package me.Cooltimmetje.Skuddbot.Commands.Admin.SuperAdmin;

import me.Cooltimmetje.Skuddbot.Enums.EmojiEnum;
import me.Cooltimmetje.Skuddbot.Minigames.Blackjack.BlackjackManager;
import me.Cooltimmetje.Skuddbot.Minigames.Challenge.ChallengeManager;
import me.Cooltimmetje.Skuddbot.Minigames.FreeForAll.FFAManager;
import me.Cooltimmetje.Skuddbot.Utilities.Constants;
import me.Cooltimmetje.Skuddbot.Utilities.MessagesUtils;
import sx.blah.discord.handle.obj.IMessage;

/**
 * This command allows admins to clear all cooldowns that there are in the bot.
 *
 * @author Tim (Cooltimmetje)
 * @version v0.4.61-ALPHA
 * @since v0.4.3-ALPHA
 */
public class ClearCooldownCommand {

    public static void run(IMessage message){
        if(Constants.adminUser.contains(message.getAuthor().getStringID())){
            ChallengeManager.clearCooldowns();
            FFAManager.clearCooldowns();
            BlackjackManager.clearCooldowns();

            MessagesUtils.addReaction(message, "All cooldowns have been cleared.", EmojiEnum.WHITE_CHECK_MARK, false);
        }
    }

}
