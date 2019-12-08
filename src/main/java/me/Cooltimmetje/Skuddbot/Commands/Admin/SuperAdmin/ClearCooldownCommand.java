package me.Cooltimmetje.Skuddbot.Commands.Admin.SuperAdmin;

import discord4j.core.object.entity.Message;
import me.Cooltimmetje.Skuddbot.Enums.EmojiEnum;
import me.Cooltimmetje.Skuddbot.Minigames.Blackjack.BlackjackManager;
import me.Cooltimmetje.Skuddbot.Minigames.Challenge.ChallengeManager;
import me.Cooltimmetje.Skuddbot.Minigames.FreeForAll.FFAManager;
import me.Cooltimmetje.Skuddbot.Minigames.TeamDeathmatch.TdManager;
import me.Cooltimmetje.Skuddbot.Utilities.Constants;
import me.Cooltimmetje.Skuddbot.Utilities.MessagesUtils;

/**
 * This command allows admins to clear all cooldowns that there are in the bot.
 *
 * @author Tim (Cooltimmetje)
 * @version v0.5.1-ALPHA
 * @since v0.4.3-ALPHA
 */
public class ClearCooldownCommand {

    public static void run(Message message){
        if(Constants.adminUser.contains(message.getId().asString())){
            ChallengeManager.clearCooldowns();
            FFAManager.clearCooldowns();
            BlackjackManager.clearCooldowns();
            TdManager.clearCooldowns();

            MessagesUtils.addReaction(message, "All cooldowns have been cleared.", EmojiEnum.WHITE_CHECK_MARK, false);
        }
    }

}
