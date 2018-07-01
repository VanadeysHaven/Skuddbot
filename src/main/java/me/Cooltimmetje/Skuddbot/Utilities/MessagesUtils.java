package me.Cooltimmetje.Skuddbot.Utilities;

import com.vdurmont.emoji.EmojiManager;
import me.Cooltimmetje.Skuddbot.Enums.DataTypes;
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

import java.util.HashMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * This class is used for message sending, there is a separate class for this so that I don't have try-catches all over my code. Keep it nice and tidy. SeemsGood
 *
 * @author Tim (Cooltimmetje)
 * @version v0.4.31-ALPHA
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
     * @param debug The message that will be saved. (Debug String, not mandatory)
     * @param emoji The emoji that we want to add.
     */
    @SuppressWarnings("unchecked")
    public static void addReaction(IMessage message, String debug, EmojiEnum emoji){
        try {
            message.addReaction(EmojiManager.getForAlias(emoji.getAlias()));
        } catch (MissingPermissionsException | RateLimitException | DiscordException e) {
            e.printStackTrace();
        }

        JSONObject obj = new JSONObject();

        obj.put("time", System.currentTimeMillis());
        obj.put("debug", debug);
        obj.put("emoji", emoji.getEmoji());

        reactions.put(message, obj);
    }

    /**
     * EVENT: This will fire when a reaction was added to a message, then we will check if it meets the requirements to post the debug string.
     *
     * @param event The event that fires this.
     */
    @EventSubscriber
    public void onReaction(ReactionAddEvent event){
        if(reactions.containsKey(event.getMessage())){ //Check if the message is actually eligible for a "debug" string.
            if(event.getReaction().getUserReacted(Main.getInstance().getSkuddbot().getOurUser())){ //Check if the bot reacted the same.
                if(event.getReaction().getUserReacted(event.getMessage().getAuthor())){ //Check if the original author reacted.
                    JSONObject obj = reactions.get(event.getMessage()); //Save it for sake of code tidyness.
                    if(obj.get("debug") != null){ //Check if there's a debug string.
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
    public static void sendSuccess(String message, IChannel channel){
        if(!Constants.MUTED) {
            try {
                channel.sendMessage(":white_check_mark: " + message.replace("@everyone", "@\u200Beveryone").replace("@here", "@\u200Bhere"));
            } catch (MissingPermissionsException | RateLimitException | DiscordException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Send a error message. Consisting out of a emoji (x), and a random message. Gets deleted after 10 seconds.
     *
     * @param error The error that occurred.
     * @param channel Channel where we send the message.
     */
    public static void sendError(String error, IChannel channel) {
        if (!Constants.MUTED) {
            ScheduledThreadPoolExecutor exec = new ScheduledThreadPoolExecutor(1);
            final IMessage message;

            try {
                message = channel.sendMessage((":x: " + MiscUtils.getRandomMessage(DataTypes.ERROR) + "\n \n`" + error + "`").replace("@everyone", "@\u200Beveryone").replace("@here", "@\u200Bhere"));
                exec.schedule(() -> {
                    assert message != null;
                    try {
                        message.delete();
                    } catch (MissingPermissionsException | RateLimitException | DiscordException e) {
                        e.printStackTrace();
                    }
                }, 10, TimeUnit.SECONDS);
            } catch (MissingPermissionsException | RateLimitException | DiscordException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Send a message that starts with the "white_check_mark" emoji, often used to indicate that something succeeded. Gets deleted after 10 seconds.
     *
     * @param messageString Message that we send, gets appended to the emoji.
     * @param channel Channel where we send the message.
     */
    public static void sendSuccessTime(String messageString, IChannel channel) {
        if (!Constants.MUTED) {
            ScheduledThreadPoolExecutor exec = new ScheduledThreadPoolExecutor(1);
            final IMessage message;

            try {
                message = channel.sendMessage(":white_check_mark: " + messageString.replace("@everyone", "@\u200Beveryone").replace("@here", "@\u200Bhere"));
                exec.schedule(() -> {
                    assert message != null;
                    try {
                        message.delete();
                    } catch (MissingPermissionsException | RateLimitException | DiscordException e) {
                        e.printStackTrace();
                    }
                }, 10, TimeUnit.SECONDS);
            } catch (MissingPermissionsException | RateLimitException | DiscordException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Send a message with no formatting, other than the formatting specified in the String.
     *
     * @param msg The message that we send.
     * @param channel Channel where we send the message.
     * @param allowEveryone Defines if we should allow @everyone/@here. If false, @everyone and @here get a ZWC added to them so Discord doesn't trigger it.
     * @return The message that was sent.
     */
    @SuppressWarnings("all") //Just because IntelliJ decided to be a dick.
    public static IMessage sendPlain(String msg, IChannel channel, boolean allowEveryone){
        if(!allowEveryone){
            msg = msg.replace("@everyone", "@\u200Beveryone").replace("@here", "@\u200Bhere");
        }
        if (!Constants.MUTED) {
            try {
                IMessage message = channel.sendMessage(msg);
                return message;
            } catch (MissingPermissionsException | RateLimitException | DiscordException e) {
                e.printStackTrace();
            }
            return null;
        }
        return null;
    }

    /**
     * Send a message regardless if Skuddbot is muted.
     *
     * @param msg The message that we send.
     * @param channel Channel where we send the message.
     * @return The message that was sent.
     */
    @SuppressWarnings("all") //Just because IntelliJ decided to be a dick.
    public static IMessage sendForce (String msg, IChannel channel){
        try {
            IMessage message = channel.sendMessage(msg.replace("@everyone", "@\u200Beveryone").replace("@here", "@\u200Bhere"));
            return message;
        } catch (MissingPermissionsException | RateLimitException | DiscordException e) {
            e.printStackTrace();
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
                return Main.getInstance().getSkuddbot().getOrCreatePMChannel(user).sendMessage(message);
            } catch (DiscordException | RateLimitException | MissingPermissionsException e) {
                e.printStackTrace();
            }
            return null;
        }
        return null;
    }

}
