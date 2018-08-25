package me.Cooltimmetje.Skuddbot.Commands.Admin.SuperAdmin;

import me.Cooltimmetje.Skuddbot.Enums.EmojiEnum;
import me.Cooltimmetje.Skuddbot.Profiles.MySqlManager;
import me.Cooltimmetje.Skuddbot.Utilities.Constants;
import me.Cooltimmetje.Skuddbot.Utilities.MessagesUtils;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

import java.text.MessageFormat;

/**
 * This class will be able to add and remove SuperAdmins. Only for timmy though.
 *
 * @author Tim (Cooltimmetje)
 * @version v0.32-ALPHA
 * @since v0.3-ALPHA-DEV
 */
public class AdminManager {

    /**
     * CMD: This will add a person to the Admin list and notify them about it with a PM.
     *
     * @param message This is the message where the command got triggered off.
     */
    public static void add(IMessage message){
        if(message.getAuthor().getStringID().equals(Constants.TIMMY_ID)) { //Check permissions
            if(message.getContent().split(" ").length > 1){ //Check arguments
                if(message.getMentions().size() > 0){ //Check if there is a mention
                    if(message.getContent().split(" ")[1].replace("<@!", "<@").equals(message.getMentions().get(0).mention().replace("<@!", "<@"))){ //Check if mention is in the right position.
                        IUser user = message.getMentions().get(0); //Define user form mention.
                        if(!Constants.adminUser.contains(user.getStringID())){ //Check if user is already a admin.
                            Constants.adminUser.add(user.getStringID()); //Add to awesome memory.
                            MySqlManager.addAdmin(user.getStringID()); //Add to awesome database.
                            MessagesUtils.sendPM(user, MessageFormat.format( //Send PM
                                    "Hello **{0}**,\n\n" +
                                            "You have been added as a admin in Skuddbot, that means you can now **OVERRIDE ALL PERMISSIONS** (well... not all... but most of them...) in Skuddbot!\n" +
                                            "This obviously comes with great responsibility, which you have (I hope...), if you have questions you can bug Timmy about it.", user.getName()));

                            MessagesUtils.sendSuccess(MessageFormat.format("User **{0}** `(ID: {1})` has been added as a admin!", user.getName(), user.getStringID()), message.getChannel()); //send confirm

                        } else {
                            MessagesUtils.addReaction(message, "This user is already a admin! - Use '!removeadmin <mention>' if you wish to remove them.", EmojiEnum.X);
                        }
                    } else {
                        MessagesUtils.addReaction(message, "Invalid arguments: !addadmin <mention>", EmojiEnum.X);
                    }
                } else {
                    MessagesUtils.addReaction(message, "Please mention a person: !addadmin <mention>", EmojiEnum.X);
                }
            } else {
                MessagesUtils.addReaction(message, "Not enough arguments: !addadmin <mention>", EmojiEnum.X);
            }
        }
    }

    /**
     * CMD: This will remove a person to from Admin list.
     *
     * @param message This is the message where the command got triggered off.
     */
    public static void remove(IMessage message){
        if(message.getAuthor().getStringID().equals(Constants.TIMMY_ID)) { //Check permissions
            if(message.getContent().split(" ").length > 1){ //Check arguments
                if(message.getMentions().size() > 0){ //Check if there is a mention
                    if(message.getContent().split(" ")[1].replace("<@!", "<@").equals(message.getMentions().get(0).mention().replace("<@!", "<@"))){ //Check if mention is in the right position.
                        IUser user = message.getMentions().get(0); //Define user form mention.
                        if(Constants.adminUser.contains(user.getStringID())){ //Check if user is admin.
                            Constants.adminUser.remove(user.getStringID()); //Remove from admin memory.
                            MySqlManager.removeAdmin(user.getStringID()); //Remove from admin database.
                            MessagesUtils.sendSuccess(MessageFormat.format("User **{0}** `(ID: {1})` has been removed as a admin!", user.getName(), user.getStringID()), message.getChannel()); //send confirm
                        } else {
                            MessagesUtils.addReaction(message, "This user is not a admin! - Use '!addadmin <mention>' if you wish to add them.", EmojiEnum.X);
                        }
                    } else {
                        MessagesUtils.addReaction(message, "Invalid arguments: !removeadmin <mention>", EmojiEnum.X);
                    }
                } else {
                    MessagesUtils.addReaction(message, "Please mention a person: !removeadmin <mention>", EmojiEnum.X);
                }
            } else {
                MessagesUtils.addReaction(message, "Not enough arguments: !removeadmin <mention>", EmojiEnum.X);
            }
        }
    }

}
