package me.Cooltimmetje.Skuddbot.Commands.Useless;

import me.Cooltimmetje.Skuddbot.Enums.DataTypes;
import me.Cooltimmetje.Skuddbot.Utilities.MessagesUtils;
import me.Cooltimmetje.Skuddbot.Utilities.MiscUtils;
import sx.blah.discord.handle.obj.IMessage;

/**
 * This class is responsible for the handling of the puppy command (has some alliasses), it will spit out one random puppy picture from the database.
 *
 * @author Tim (Cooltimmetje)
 * @version v0.4.34-ALPHA
 * @since v0.4.34-ALPHA
 */
public class PuppyCommand {

    public static void run(IMessage message){
        MessagesUtils.sendPlain(":dog:" + MiscUtils.getRandomMessage(DataTypes.PUPPY), message.getChannel(), false);
    }

}
