package me.Cooltimmetje.Skuddbot.Commands.Useless;

import me.Cooltimmetje.Skuddbot.Enums.DataTypes;
import me.Cooltimmetje.Skuddbot.Utilities.MessagesUtils;
import me.Cooltimmetje.Skuddbot.Utilities.MiscUtils;
import sx.blah.discord.handle.obj.IMessage;

/**
 * Cake! Who doesn't like cake?
 *
 * @author Tim (Cooltimmetje)
 * @version v0.5.01-ALPHA
 * @since v0.5.01-ALPHA
 */
public class CakeCommand {

    public static void run(IMessage message){
        String pictureURL;
        boolean allowed;

        do {
            pictureURL = MiscUtils.getRandomMessage(DataTypes.CAKE);
            allowed = MiscUtils.randomCheck(pictureURL);
        } while (!allowed);

        MessagesUtils.sendPlain(":cake: " + pictureURL , message.getChannel(), false);
    }

}
