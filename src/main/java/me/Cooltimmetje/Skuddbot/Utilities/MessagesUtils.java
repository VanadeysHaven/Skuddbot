package me.Cooltimmetje.Skuddbot.Utilities;

import discord4j.core.event.domain.message.ReactionAddEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.MessageChannel;
import discord4j.core.object.entity.User;
import discord4j.core.object.reaction.ReactionEmoji;
import discord4j.core.object.util.Snowflake;
import me.Cooltimmetje.Skuddbot.Enums.EmojiEnum;
import me.Cooltimmetje.Skuddbot.Main;
import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.List;

/**
 * This class is used for message sending, there is a separate class for this so that I don't have try-catches all over my code. Keep it nice and tidy. SeemsGood
 *
 * @author Tim (Cooltimmetje)
 * @version v0.5.1-ALPHA
 * @since v0.1-ALPHA
 */
public class MessagesUtils {

    /**
     * This HashMap is to save messages that have been reacted to, to recall their original message so we can print it out when people react to it.
     */
    public static HashMap<Message, JSONObject> reactions = new HashMap<>();

    /**
     * This adds a reaction to the specified message with the specified emoji. The debug string get's saved to recall later and will be posted upon reaction from the original author with the same emoji.
     * @param message The message that we want to add the reaction to.
     * @param debug   The message that will be saved. (Debug String, not mandatory)
     * @param emoji   The emoji that we want to add.
     * @param ignoreUser (optional, default=false) If this is true, any user can click the emoji to display the debug.
     * @param expireTime (optional, default=1800000) This sets the the time for how long the debug string will be stored for in the HashMap.
     */
    @SuppressWarnings("unchecked")
    public static void addReaction(Message message, String debug, EmojiEnum emoji, boolean ignoreUser, long expireTime) {
        message.addReaction(ReactionEmoji.unicode(emoji.getUnicode()));

        JSONObject obj = new JSONObject();

        obj.put("time", System.currentTimeMillis());
        obj.put("debug", debug);
        obj.put("emoji", emoji.getUnicode());
        obj.put("ignoreUser", ignoreUser);
        obj.put("expireTime", expireTime);

        reactions.put(message, obj);
    }


    public static void addReaction(Message message, String debug, EmojiEnum emoji, boolean ignoreUser){
        addReaction(message, debug, emoji, ignoreUser, 30*60*1000);
    }

    public static void addReaction(Message message, String debug, EmojiEnum emoji){
        addReaction(message, debug, emoji, false, 30*60*1000);
    }

    /**
     * EVENT: This will fire when a reaction was added to a message, then we will check if it meets the requirements to post the debug string.
     *
     * @param event The event that fires this.
     */
    public static void onReaction(ReactionAddEvent event) {
        if(event.getUser().block().isBot()) return;
        if(reactions.containsKey(event.getMessage().block())) { //Check if the message is actually eligible for a "debug" string.
            JSONObject obj = reactions.get(event.getMessage()); //Save it for sake of code tidyness.
            List<User> reactors = event.getMessage().block().getReactors(event.getEmoji()).collectList().block();
            if (reactors.contains(Main.getInstance().getSkuddbot().getSelf().block())) { //Check if the bot reacted the same.
                if (reactors.contains(event.getMessage().block().getAuthor().get()) || Boolean.parseBoolean(String.valueOf(obj.get("ignoreUser")))) { //Check if the original author reacted or if we should ignore users.
                    if (obj.get("debug") != null) { //Check if there's a debug string.
                        sendPlain(obj.get("emoji") + " " + obj.get("debug"), event.getMessage().block().getChannel().block(), false); //Post the message.
                    }

                    reactions.remove(event.getMessage()); //Remove it from the HashMap as we no longer need it there.
                }
            }
        }
    }

    /**
     * Send a message that starts with the "white_check_mark" emoji, often used to indicate that something succeeded.
     *
     * @param message Message that we send, gets appended to the emoji.
     * @param channel Channel where we send the message.
     */
    public static void sendSuccess(String message, MessageChannel channel) {
        if (!Constants.MUTED) {
            channel.createMessage(":white_check_mark: " + message.replace("@everyone", "@\u200Beveryone").replace("@here", "@\u200Bhere"));
        }
    }

    /**
     * Send a message with no formatting, other than the formatting specified in the String.
     *
     * @param msg           The message that we send.
     * @param channel       Channel where we send the message.
     * @param allowEveryone (optional, default=false)Defines if we should allow @everyone/@here. If false, @everyone and @here get a ZWC added to them so Discord doesn't trigger it.
     * @return The message that was sent.
     */
    @SuppressWarnings("all") //Just because IntelliJ decided to be a dick.
    public static Message sendPlain(String msg, MessageChannel channel, boolean allowEveryone) {
        if (!allowEveryone) {
            msg = msg.replace("@everyone", "@\u200Beveryone").replace("@here", "@\u200Bhere");
        }
        String msgFinal = msg;
        if (!Constants.MUTED) {
            Message message = channel.createMessage(msgFinal).block();
            return message;
        }
        return null;
    }

    public static Message sendPlain(String msg, MessageChannel channel){
        return sendPlain(msg, channel, false);
    }

    /**
     * Send a PM to the specified user.
     *
     * @param user The user that we want to send the message to.
     * @param message The message that we send.
     * @return The message that was sent.
     */
    public static Message sendPM(User user, String message) {
        if (!Constants.MUTED) {
            return user.getPrivateChannel().block().createMessage(message).block();
        }
        return null;
    }

    /**
     * Gets the message with the specified ID
     *
     * @param messageId The ID of the message we want to get.
     * @param channelId The ID of the channel where the message is in.
     * @return The message we want.
     */
    public static Message getMessageByID(long messageId, long channelId){
        return Main.getInstance().getSkuddbot().getMessageById(Snowflake.of(channelId), Snowflake.of(messageId)).block();
    }


}
