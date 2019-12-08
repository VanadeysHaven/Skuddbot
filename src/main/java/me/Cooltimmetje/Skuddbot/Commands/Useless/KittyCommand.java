package me.Cooltimmetje.Skuddbot.Commands.Useless;

import discord4j.core.object.entity.Message;
import me.Cooltimmetje.Skuddbot.Enums.DataTypes;
import me.Cooltimmetje.Skuddbot.Utilities.MessagesUtils;
import me.Cooltimmetje.Skuddbot.Utilities.MiscUtils;

/**
 * This class is responsible for the handling of the kitty command (has some alliasses), it will spit out one random kitty picture from the database.
 *
 * @author Tim (Cooltimmetje)
 * @version v0.5.1-ALPHA
 * @since v0.4.41-ALPHA
 */
public class KittyCommand {

    public static void run(Message message) {
        String pictureURL;
        boolean allowed;

        do {
            pictureURL = MiscUtils.getRandomMessage(DataTypes.KITTY);
            allowed = MiscUtils.randomCheck(pictureURL);
        } while (!allowed);

        MessagesUtils.sendPlain(":cat: " + pictureURL , message.getChannel().block(), false);
    }

}
