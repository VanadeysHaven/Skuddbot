package me.Cooltimmetje.Skuddbot.Commands.Admin.SuperAdmin;

import me.Cooltimmetje.Skuddbot.Enums.EmojiEnum;
import me.Cooltimmetje.Skuddbot.Profiles.MySqlManager;
import me.Cooltimmetje.Skuddbot.Utilities.Constants;
import me.Cooltimmetje.Skuddbot.Utilities.MessagesUtils;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

import java.text.MessageFormat;

/**
 * This will class will allow "SuperAdmins" to add and remove "Awesome People", these persons get various benefits.
 *
 * @author Tim (Cooltimmetje)
 * @version v0.4.41-ALPHA
 * @since v0.3-ALPHA-DEV
 */
public class AwesomeManager {

    /**
     * CMD: This will add a person to the Awesome list and notify them about it with a PM.
     *
     * @param message This is the message where the command got triggered off.
     */
    public static void add(IMessage message){
        if(Constants.adminUser.contains(message.getAuthor().getStringID())) { //Check permissions
            if(message.getContent().split(" ").length > 1){ //Check arguments
                if(message.getMentions().size() > 0){ //Check if there is a mention
                    if(message.getContent().split(" ")[1].replace("<@!", "<@").equals(message.getMentions().get(0).mention().replace("<@!", "<@"))){ //Check if mention is in the right position.
                        IUser user = message.getMentions().get(0); //Define user form mention.
                        if(!Constants.awesomeUser.contains(user.getStringID())){ //Check if user is already awesome.
                            Constants.awesomeUser.add(user.getStringID()); //Add to awesome memory.
                            MySqlManager.addAwesome(user.getStringID()); //Add to awesome database.
                            MessagesUtils.sendPM(user, MessageFormat.format( //Send PM
                                    "Hello **{0}**,\n\n" +
                                            "You have been added as a awesome user in Skuddbot, that means you get various benefits, Whoohoo!\n" +
                                            "These benefits include but are not limited to:\n\n" +
                                            "- Add messages to the playing status pool. - `!addmsg playing <message>` *pm only*\n" +
                                            "- Add messages to the error messages pool. - `!addmsg error <message>` *pm only*\n" +
                                            "- Add messages to the \"I'm alive\" messages pool. - `!addmsg alive <message>` *pm only*\n" +
                                            "- Add messages to the \"Puppy\" images pool. - `!addmsg puppy <url to image>` *pm only*\n" +
                                            "- Add messages to the \"Kitty\" images pool. - `!addmsg kitty <url to image>` *pm only*\n" +
                                            "- Set a custom `!ping` message! - `!setping <message>` *pm only*\n" +
                                            "- Gain access to the `!game` command.\n\n" +
                                            "**VERY IMPORTANT NOTE: Once you added a message, it cannot be removed unless you ask Timmy.** Please think twice before you add something. We also track who added what. Just saying...\n\n" +
                                            "For more information please refer to the manual or ask Timmy!\n" +
                                            "Manual: " + Constants.config.get("manual"),
                                    user.getName()));

                                    MessagesUtils.sendSuccess(MessageFormat.format("User **{0}** `(ID: {1})` has been added as Awesome!", user.getName(), user.getStringID()), message.getChannel()); //send confirm

                        } else {
                            MessagesUtils.addReaction(message,"This user is already Awesome! - Use '!removeawesome <mention>' if you wish to remove them.", EmojiEnum.X);
                        }
                    } else {
                        MessagesUtils.addReaction(message,"Invalid arguments: !addawesome <mention>", EmojiEnum.X);
                    }
                } else {
                    MessagesUtils.addReaction(message,"Please mention a person: !addawesome <mention>", EmojiEnum.X);
                }
            } else {
                MessagesUtils.addReaction(message,"Not enough arguments: !addawesome <mention>", EmojiEnum.X);
            }
        }
    }

    /**
     * CMD: This will remove a person to from Awesome list.
     *
     * @param message This is the message where the command got triggered off.
     */
    public static void remove(IMessage message){
        if(Constants.adminUser.contains(message.getAuthor().getStringID())) { //Check permissions
            if(message.getContent().split(" ").length > 1){ //Check arguments
                if(message.getMentions().size() > 0){ //Check if there is a mention
                    if(message.getContent().split(" ")[1].replace("<@!", "<@").equals(message.getMentions().get(0).mention().replace("<@!", "<@"))){ //Check if mention is in the right position.
                        IUser user = message.getMentions().get(0); //Define user form mention.
                        if(Constants.awesomeUser.contains(user.getStringID())){ //Check if user is awesome.
                            Constants.awesomeUser.remove(user.getStringID()); //Remove from awesome memory.
                            MySqlManager.removeAwesome(user.getStringID()); //Remove from awesome database.

                            MessagesUtils.sendSuccess(MessageFormat.format("User **{0}** `(ID: {1})` has been removed as Awesome!", user.getName(), user.getStringID()), message.getChannel()); //send confirm

                        } else {
                            MessagesUtils.addReaction(message,"This user is not Awesome! - Use '!addawesome <mention>' if you wish to add them.", EmojiEnum.X);
                        }
                    } else {
                        MessagesUtils.addReaction(message,"Invalid arguments: !removeawesome <mention>", EmojiEnum.X);
                    }
                } else {
                    MessagesUtils.addReaction(message,"Please mention a person: !removeawesome <mention>", EmojiEnum.X);
                }
            } else {
                MessagesUtils.addReaction(message,"Not enough arguments: !removeawesome <mention>", EmojiEnum.X);
            }
        }
    }

    /**
     * CMD: Reload all strings form the database.
     *
     * @param message The message that triggered this command.
     */
    public static void reload(IMessage message){
        if(Constants.adminUser.contains(message.getAuthor().getStringID())) { //Check permissions
            long start = System.currentTimeMillis();
            Constants.awesomeStrings.clear();
            MySqlManager.loadAwesomeData();
            MessagesUtils.sendSuccess("Reloaded! Took `" + (System.currentTimeMillis() - start) + "`", message.getChannel());
        }
    }

}
