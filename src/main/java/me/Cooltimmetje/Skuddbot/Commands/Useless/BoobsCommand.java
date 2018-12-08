package me.Cooltimmetje.Skuddbot.Commands.Useless;

import me.Cooltimmetje.Skuddbot.Enums.DataTypes;
import me.Cooltimmetje.Skuddbot.Utilities.MessagesUtils;
import me.Cooltimmetje.Skuddbot.Utilities.MiscUtils;
import sx.blah.discord.handle.obj.IMessage;

/**
 * Boobies command.
 * Because they asked for it.
 *
 * @author Tim (Cooltimmetje)
 * @version v0.4.41-ALPHA
 * @since v0.4.41-ALPHA
 */
public class BoobsCommand {

    public static void run(IMessage message){
        if(!message.getChannel().isNSFW()){
            return;
        }

        String pictureURL;
        boolean allowed;

        do {
            pictureURL = MiscUtils.getRandomMessage(DataTypes.BOOBS);
            allowed = MiscUtils.randomCheck(pictureURL);
        } while (!allowed);

        MessagesUtils.sendPlain(":peach: " + pictureURL , message.getChannel(), false);
    }

}
