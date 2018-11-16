package me.Cooltimmetje.Skuddbot.Commands.Useless;

import me.Cooltimmetje.Skuddbot.Enums.DataTypes;
import me.Cooltimmetje.Skuddbot.Utilities.MessagesUtils;
import me.Cooltimmetje.Skuddbot.Utilities.MiscUtils;
import sx.blah.discord.handle.obj.IMessage;

import java.util.HashMap;

/**
 * This class is responsible for the handling of the puppy command (has some alliasses), it will spit out one random puppy picture from the database.
 *
 * @author Tim (Cooltimmetje)
 * @version v0.4.34-ALPHA
 * @since v0.4.34-ALPHA
 */
public class PuppyCommand {

    private static HashMap<String,Long> lastShown = new HashMap<>();
    public static int puppyPictures = 0;

    public static void run(IMessage message){
        if((puppyPictures * 0.75) < lastShown.size()){
            lastShown.clear();
        }
        String pictureURL = MiscUtils.getRandomMessage(DataTypes.PUPPY);
        boolean allowed = false;

        while(!allowed){
            pictureURL = MiscUtils.getRandomMessage(DataTypes.PUPPY);

            if(lastShown.containsKey(pictureURL)){
                if((System.currentTimeMillis() - lastShown.get(pictureURL)) < 24*60*60*1000){
                    allowed = MiscUtils.randomInt(1,4) == 1;
                } else {
                    allowed = true;
                }
            } else {
                allowed = true;
            }
        }

        lastShown.put(pictureURL, System.currentTimeMillis());

        MessagesUtils.sendPlain(":dog: " + pictureURL , message.getChannel(), false);
    }

}
