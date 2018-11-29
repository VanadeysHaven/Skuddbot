package me.Cooltimmetje.Skuddbot.Commands.Useless;

import me.Cooltimmetje.Skuddbot.Enums.DataTypes;
import me.Cooltimmetje.Skuddbot.Utilities.MessagesUtils;
import me.Cooltimmetje.Skuddbot.Utilities.MiscUtils;
import sx.blah.discord.handle.obj.IMessage;

import javax.xml.crypto.Data;
import java.util.HashMap;

/**
 * mmmmmmmmmmmmmm..... Bacon....
 *
 * @author Tim (Cooltimmetje)
 * @version v0.4.41-ALPHA
 * @since v0.4.41-ALPHA
 */
public class BaconCommand {

    public static void run(IMessage message){
        String pictureURL = MiscUtils.getRandomMessage(DataTypes.BACON);
        boolean allowed = MiscUtils.randomCheck(pictureURL);

        while (!allowed){
            pictureURL = MiscUtils.getRandomMessage(DataTypes.BACON);
            allowed = MiscUtils.randomCheck(pictureURL);
        }

        MessagesUtils.sendPlain(":bacon: " + pictureURL , message.getChannel(), false);
    }

}
