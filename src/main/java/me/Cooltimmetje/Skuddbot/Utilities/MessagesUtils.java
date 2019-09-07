package me.Cooltimmetje.Skuddbot.Utilities;

import com.vdurmont.emoji.EmojiManager;
import me.Cooltimmetje.Skuddbot.Enums.EmojiEnum;
import me.Cooltimmetje.Skuddbot.Main;
import org.json.simple.JSONObject;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.reaction.ReactionAddEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;
import sx.blah.discord.util.RequestBuffer;

import java.util.HashMap;

/**
 * This class is used for message sending, there is a separate class for this so that I don't have try-catches all over my code. Keep it nice and tidy. SeemsGood
 *
 * @author Tim (Cooltimmetje)
 * @version v0.4.61-ALPHA
 * @since v0.1-ALPHA
 */
public class MessagesUtils {

    /**
     * This HashMap is to save messages that have been reacted to, to recall their original message so we can print it out when people react to it.
     */
    public static HashMap<IMessage, JSONObject> reactions = new HashMap<>();

    /**
     * This adds a reaction to the specified message with the specified emoji. The debug string get's saved to recall later and will be posted upon reaction from the original author with the same emoji.
     * @param message The message that we want to add the reaction to.
     * @param debug   The message that will be saved. (Debug String, not mandatory)
     * @param emoji   The emoji that we want to add.
     * @param ignoreUser (optional, default=false) If this is true, any user can click the emoji to display the debug.
     * @param expireTime (optional, default=1800000) This sets the the time for how long the debug string will be stored for in the HashMap.
     */
    @SuppressWarnings("unchecked")
    public static void addReaction(IMessage message, String debug, EmojiEnum emoji, boolean ignoreUser, long expireTime) {
        try {
            RequestBuffer.request(() -> message.addReaction(EmojiManager.getForAlias(emoji.getAlias())));
        } catch (MissingPermissionsException | RateLimitException | DiscordException e) {
            e.printStackTrace();
        }

        JSONObject obj = new JSONObject();

        obj.put("time", System.currentTimeMillis());
        obj.put("debug", debug);
        obj.put("emoji", emoji.getEmoji());
        obj.put("ignoreUser", ignoreUser);
        obj.put("expireTime", expireTime);

        reactions.put(message, obj);
    }


    public static void addReaction(IMessage message, String debug, EmojiEnum emoji, boolean ignoreUser){
        addReaction(message, debug, emoji, ignoreUser, 30*60*1000);
    }

    public static void addReaction(IMessage message, String debug, EmojiEnum emoji){
        addReaction(message, debug, emoji, false, 30*60*1000);
    }

    /**
     * EVENT: This will fire when a reaction was added to a message, then we will check if it meets the requirements to post the debug string.
     *
     * @param event The event that fires this.
     */
    @EventSubscriber
    public void onReaction(ReactionAddEvent event) {
        if(event.getUser().isBot()) return;
        if(reactions.containsKey(event.getMessage())) { //Check if the message is actually eligible for a "debug" string.
            JSONObject obj = reactions.get(event.getMessage()); //Save it for sake of code tidyness.
            if (event.getReaction().getUserReacted(Main.getInstance().getSkuddbot().getOurUser())) { //Check if the bot reacted the same.
                if (event.getReaction().getUserReacted(event.getMessage().getAuthor()) || Boolean.parseBoolean(String.valueOf(obj.get("ignoreUser")))) { //Check if the original author reacted or if we should ignore users.
                    if (obj.get("debug") != null) { //Check if there's a debug string.
                        sendPlain(obj.get("emoji") + " " + obj.get("debug"), event.getMessage().getChannel(), false); //Post the message.
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
    public static void sendSuccess(String message, IChannel channel) {
        if (!Constants.MUTED) {
            try {
                RequestBuffer.request(() -> channel.sendMessage(":white_check_mark: " + message.replace("@everyone", "@\u200Beveryone").replace("@here", "@\u200Bhere")));
            } catch (MissingPermissionsException | RateLimitException | DiscordException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Send a message with no formatting, other than the formatting specified in the String.
     *
     * @param msg           The message that we send.
     * @param channel       Channel where we send the message.
     * @param allowEveryone Defines if we should allow @everyone/@here. If false, @everyone and @here get a ZWC added to them so Discord doesn't trigger it.
     * @return The message that was sent.
     */
    @SuppressWarnings("all") //Just because IntelliJ decided to be a dick.
    public static IMessage sendPlain(String msg, IChannel channel, boolean allowEveryone) {
        if (!allowEveryone) {
            msg = msg.replace("@everyone", "@\u200Beveryone").replace("@here", "@\u200Bhere");
        }
        String msgFinal = msg;
        if (!Constants.MUTED) {
            try {
                IMessage message = RequestBuffer.request(() -> {
                    return channel.sendMessage(msgFinal);
                }).get();
                return message;
            } catch (MissingPermissionsException | RateLimitException | DiscordException e) {
                e.printStackTrace();
            }
            return null;
        }
        return null;
    }

    /**
     * Send a PM to the specified user.
     *
     * @param user The user that we want to send the message to.
     * @param message The message that we send.
     * @return The message that was sent.
     */
    public static IMessage sendPM(IUser user, String message) {
        if (!Constants.MUTED) {
            try {
                return RequestBuffer.request(() -> Main.getInstance().getSkuddbot().getOrCreatePMChannel(user).sendMessage(message)).get();
            } catch (DiscordException | RateLimitException | MissingPermissionsException e) {
                e.printStackTrace();
            }
            return null;
        }
        return null;
    }

    /**
     * Gets the message with the specified ID
     *
     * @param id The ID of the message we want to get.
     * @return The message we want.
     */
    public static IMessage getMessageByID(long id){
        return Main.getInstance().getSkuddbot().getMessageByID(id);
    }


}
