package me.Cooltimmetje.Skuddbot.Commands;

import me.Cooltimmetje.Skuddbot.Enums.DataTypes;
import me.Cooltimmetje.Skuddbot.Profiles.MySqlManager;
import me.Cooltimmetje.Skuddbot.Utilities.Constants;
import me.Cooltimmetje.Skuddbot.Utilities.MessagesUtils;
import sx.blah.discord.handle.obj.IMessage;

/**
 * Allows awesome users to add stuff to the pool of messages!
 *
 * @author Tim (Cooltimmetje)
 * @version v0.4.01-ALPHA-DEV
 * @since v0.3-ALPHA-DEV
 */
public class AddMessageCommand {


    /**
     * CMD: Allows awesome users to add stuff to the pool of messages!
     *
     * @param message The message that triggered this command.
     */
    public static void run(IMessage message){
        if(Constants.awesomeUser.contains(message.getAuthor().getStringID())){
            String[] args = message.getContent().split(" ");
            if(args.length > 2){
                DataTypes dataType;

                try {
                    dataType = DataTypes.valueOf(args[1].toUpperCase());
                } catch (IllegalArgumentException e){
                    MessagesUtils.sendError("Unknown type: " + args[1].toUpperCase(), message.getChannel());
                    return;
                }

                StringBuilder sb = new StringBuilder();
                for(int i = 2; i<args.length; i++){
                    sb.append(args[i]).append(" ");
                }

                String input = sb.toString().trim();
                String trimmed = input.substring(0, Math.min(input.length(), dataType.getMaxLength()));

                if(Constants.awesomeStrings.containsKey(trimmed)){
                    MessagesUtils.sendError("This message already exists!", message.getChannel());
                    return;
                }

                if(input.length() > dataType.getMaxLength()){
                        MessagesUtils.sendPlain(":warning: Your message is exceeding the __" + dataType.getMaxLength() + " character limit__. To add it you need to make it shorter." +
                                "For your convenience: This is your message trimmed down to the correct length:\n```\n" + trimmed + "\n```", message.getChannel(), false);
                } else {
                    MessagesUtils.sendSuccess("Added `" + trimmed + "` as a `" + dataType.toString().toUpperCase() + "` message!", message.getChannel());
                    MySqlManager.addAwesomeString(dataType, trimmed, message.getAuthor().getStringID());
                    Constants.awesomeStrings.put(trimmed, dataType);
                }
            } else {
                MessagesUtils.sendError("Not enough arguments: !addmsg <type> <message>", message.getChannel());
            }
        }
    }

}