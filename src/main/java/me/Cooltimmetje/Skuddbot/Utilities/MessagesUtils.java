package me.Cooltimmetje.Skuddbot.Utilities;

import me.Cooltimmetje.Skuddbot.Enums.ErrorMessages;
import me.Cooltimmetje.Skuddbot.Main;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by Tim on 8/2/2016.
 */
public class MessagesUtils {


    public static void sendSuccess(String message, IChannel channel){
        if(!Constants.MUTED) {
            try {
                channel.sendMessage(":white_check_mark: " + message);
            } catch (MissingPermissionsException | RateLimitException | DiscordException e) {
                e.printStackTrace();
            }
        }
    }

    public static void sendError(String error, IChannel channel) {
        if (!Constants.MUTED) {
            ScheduledThreadPoolExecutor exec = new ScheduledThreadPoolExecutor(1);
            final IMessage message;

            try {
                message = channel.sendMessage(":x: " + ErrorMessages.random().getError() + "\n \n`" + error + "`");
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

    public static void sendSuccessTime(String messageString, IChannel channel) {
        if (!Constants.MUTED) {
            ScheduledThreadPoolExecutor exec = new ScheduledThreadPoolExecutor(1);
            final IMessage message;

            try {
                message = channel.sendMessage(":white_check_mark: " + messageString);
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

    @SuppressWarnings("all") //Just because IntelliJ decided to be a dick.
    public static IMessage sendPlain (String msg, IChannel channel){
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

    @SuppressWarnings("all") //Just because IntelliJ decided to be a dick.
    public static IMessage sendForce (String msg, IChannel channel){
        try {
            IMessage message = channel.sendMessage(msg);
            return message;
        } catch (MissingPermissionsException | RateLimitException | DiscordException e) {
            e.printStackTrace();
        }
        return null;
    }


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
