package me.Cooltimmetje.Skuddbot.Commands.Admin.SuperAdmin;

import discord4j.core.object.entity.Message;
import me.Cooltimmetje.Skuddbot.Utilities.Constants;
import me.Cooltimmetje.Skuddbot.Utilities.Logger;
import me.Cooltimmetje.Skuddbot.Utilities.MessagesUtils;
import me.Cooltimmetje.Skuddbot.Utilities.MiscUtils;

import java.text.MessageFormat;

/**
 * This class contains a test for the RNG. This will roll a dice 1200 times, and spit out the results, if the RNG is fair, each face should have been rolled about 200 times.
 *
 * @author Tim (Cooltimmetje)
 * @version v0.5.1-ALPHA
 * @since v0.4.32-ALPHA
 */
public class RandomTestCommand {

    public static void run(Message message){
        if(!Constants.adminUser.contains(message.getAuthor().get().getId().asString())){
            return;
        }

        Logger.info("Running random test.");
        message.getChannel().block().type().subscribe();
        int one = 0;
        int two = 0;
        int three = 0;
        int four = 0;
        int five = 0;
        int six = 0;

        for(int i=0; i < 1200; i++){
            int face = MiscUtils.randomInt(1,6);
            switch (face){
                case 1:
                    one++;
                    Logger.info("Rolled " + face + " (" + one + "times sofar)");
                    break;
                case 2:
                    two++;
                    Logger.info("Rolled " + face + " (" + two + "times sofar)");
                    break;
                case 3:
                    three++;
                    Logger.info("Rolled " + face + " (" + three + "times sofar)");
                    break;
                case 4:
                    four++;
                    Logger.info("Rolled " + face + " (" + four + "times sofar)");
                    break;
                case 5:
                    five++;
                    Logger.info("Rolled " + face + " (" + five + "times sofar)");
                    break;
                case 6:
                    six++;
                    Logger.info("Rolled " + face + " (" + six + "times sofar)");
                    break;
            }
        }

        MessagesUtils.sendPlain(MessageFormat.format("Random test: Dice roll x1200\n```\n1: {0}\n2: {1}\n3: {1}\n4: {2}\n5: {4}\n6: {5}\n```", one, two, three, four, five, six), message.getChannel().block(), false);
    }

}
