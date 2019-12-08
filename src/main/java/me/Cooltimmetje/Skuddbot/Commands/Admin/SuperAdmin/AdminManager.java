package me.Cooltimmetje.Skuddbot.Commands.Admin.SuperAdmin;

import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import me.Cooltimmetje.Skuddbot.Enums.EmojiEnum;
import me.Cooltimmetje.Skuddbot.Profiles.MySqlManager;
import me.Cooltimmetje.Skuddbot.Utilities.Constants;
import me.Cooltimmetje.Skuddbot.Utilities.MessagesUtils;

import java.text.MessageFormat;

/**
 * This class will be able to add and remove SuperAdmins. Only for timmy though.
 *
 * @author Tim (Cooltimmetje)
 * @version v0.5.1-ALPHA
 * @since v0.3-ALPHA-DEV
 */
public class AdminManager {

    /**
     * CMD: This will add a person to the Admin list and notify them about it with a PM.
     *
     * @param message This is the message where the command got triggered off.
     */
    public static void add(Message message){
        if(message.getAuthor().get().getId().asLong() == Constants.TIMMY_ID) { //Check permissions
            if(message.getContent().get().split(" ").length > 1){ //Check arguments
                if(message.getUserMentions().collectList().block().size() > 0){ //Check if there is a mention
                    if(message.getContent().get().split(" ")[1].replace("<@!", "<@").equals(message.getUserMentions().collectList().block().get(0).getMention().replace("<@!", "<@"))){ //Check if mention is in the right position.
                        User user = message.getUserMentions().collectList().block().get(0); //Define user form mention.
                        if(!Constants.adminUser.contains(user.getId().asString())){ //Check if user is already a admin.
                            Constants.adminUser.add(user.getId().asString()); //Add to awesome memory.
                            MySqlManager.addAdmin(user.getId().asString()); //Add to awesome database.
                            MessagesUtils.sendPM(user, MessageFormat.format( //Send PM
                                    "Hello **{0}**,\n\n" +
                                            "You have been added as a admin in Skuddbot, that means you can now **OVERRIDE ALL PERMISSIONS** (well... not all... but most of them...) in Skuddbot!\n" +
                                            "This obviously comes with great responsibility, which you have (I hope...), if you have questions you can bug Timmy about it.", user.getUsername()));

                            MessagesUtils.sendSuccess(MessageFormat.format("User **{0}** `(ID: {1})` has been added as a admin!", user.getUsername(), user.getId().asString()), message.getChannel().block()); //send confirm

                        } else {
                            MessagesUtils.addReaction(message, "This user is already a admin! - Use '!removeadmin <mention>' if you wish to remove them.", EmojiEnum.X, false);
                        }
                    } else {
                        MessagesUtils.addReaction(message, "Invalid arguments: !addadmin <mention>", EmojiEnum.X, false);
                    }
                } else {
                    MessagesUtils.addReaction(message, "Please mention a person: !addadmin <mention>", EmojiEnum.X, false);
                }
            } else {
                MessagesUtils.addReaction(message, "Not enough arguments: !addadmin <mention>", EmojiEnum.X, false);
            }
        }
    }

    /**
     * CMD: This will remove a person to from Admin list.
     *
     * @param message This is the message where the command got triggered off.
     */
    public static void remove(Message message){
        if(message.getAuthor().get().getId().asLong() == Constants.TIMMY_ID) { //Check permissions
            if(message.getContent().get().split(" ").length > 1){ //Check arguments
                if(message.getUserMentions().collectList().block().size() > 0){ //Check if there is a mention
                    if(message.getContent().get().split(" ")[1].replace("<@!", "<@").equals(message.getUserMentions().collectList().block().get(0).getMention().replace("<@!", "<@"))){ //Check if mention is in the right position.
                        User user = message.getUserMentions().collectList().block().get(0); //Define user form mention.
                        if(Constants.adminUser.contains(user.getId().asString())){ //Check if user is admin.
                            Constants.adminUser.remove(user.getId().asString()); //Remove from admin memory.
                            MySqlManager.removeAdmin(user.getId().asString()); //Remove from admin database.
                            MessagesUtils.sendSuccess(MessageFormat.format("User **{0}** `(ID: {1})` has been removed as a admin!", user.getUsername(), user.getId().asString()), message.getChannel().block()); //send confirm
                        } else {
                            MessagesUtils.addReaction(message, "This user is not a admin! - Use '!addadmin <mention>' if you wish to add them.", EmojiEnum.X, false);
                        }
                    } else {
                        MessagesUtils.addReaction(message, "Invalid arguments: !removeadmin <mention>", EmojiEnum.X, false);
                    }
                } else {
                    MessagesUtils.addReaction(message, "Please mention a person: !removeadmin <mention>", EmojiEnum.X, false);
                }
            } else {
                MessagesUtils.addReaction(message, "Not enough arguments: !removeadmin <mention>", EmojiEnum.X, false);
            }
        }
    }

}
