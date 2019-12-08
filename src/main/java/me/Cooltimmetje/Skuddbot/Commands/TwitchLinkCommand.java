package me.Cooltimmetje.Skuddbot.Commands;

import discord4j.core.object.entity.Message;
import discord4j.core.object.util.Snowflake;
import me.Cooltimmetje.Skuddbot.Enums.EmojiEnum;
import me.Cooltimmetje.Skuddbot.Main;
import me.Cooltimmetje.Skuddbot.Profiles.MySqlManager;
import me.Cooltimmetje.Skuddbot.Profiles.ProfileManager;
import me.Cooltimmetje.Skuddbot.Profiles.SkuddUser;
import me.Cooltimmetje.Skuddbot.Utilities.*;

import java.util.HashMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * This class allows users to link up their Twitch accounts for a real-time combined XP amount for both platforms.
 *
 * @author Tim (Cooltimmetje)
 * @version v0.5.1-ALPHA
 * @since v0.1-ALPHA
 */
public class TwitchLinkCommand {

    /**
     * Used for storing profiles for which the verify code has already been entered.
     */
    private static HashMap<SkuddUser, SkuddUser> linkPending = new HashMap<>();
    /**
     * Used to track back where the link command originated from.
     */
    public static HashMap<String, String> serverID = new HashMap<>();

    /**
     * This gets ran when users type !twitch in any Skuddbot server, this initiates the linking process and shows some information.
     *
     * @param message The message that triggered this command..
     */
    @SuppressWarnings("ConstantConditions")
    public static void run(Message message) {
        SkuddUser su = ProfileManager.getDiscord(message.getAuthor().get().getId().asString(), message.getGuild().block().getId().asString(), true);
        String code;
        su.save();

        if (linkPending.containsKey(su)) {
            sendFollowUp(su, linkPending.get(su));
            return;
        }
        if(serverID.containsKey(message.getAuthor().get().getId().asString()) && !serverID.get(message.getAuthor().get().getId().asString()).equalsIgnoreCase(message.getGuild().block().getId().asString())) {
            MessagesUtils.addReaction(message,"You currently have a link pending on " + Main.getInstance().getSkuddbot().getGuildById(Snowflake.of(serverID.get(message.getAuthor().get().getId().asString()))).block().getName() + "! Please complete that one first!", EmojiEnum.X, false);
            return;
        }

        serverID.put(message.getAuthor().get().getId().asString(), message.getGuild().block().getId().asString());

        if (su.getTwitchUsername() == null) {
            MessagesUtils.addReaction(message,message.getAuthor().get().getMention() + ", please check your PM's for further instructions!", EmojiEnum.MAILBOX_WITH_MAIL, false);
            if (su.getTwitchVerify() == null) {
                code = MiscUtils.randomString(6);
                while (Constants.verifyCodes.containsKey(code)) {
                    code = MiscUtils.randomString(6);
                }
                Constants.verifyCodes.put(code, su);
                su.setTwitchVerify(code);
            } else {
                code = su.getTwitchVerify();
            }
            su.setVerifyMessage(MessagesUtils.sendPM(message.getAuthor().get(), "[" + message.getGuild().block().getName() + "] Linking your Twitch account to Discord gives you many advantages, and it's easy to do! " +
                    "Please be aware: This connection is different from Discord's Twitch connection, this does not involve any OAuth2, but uses our custom created system called 'SkuddSync' (sounds fancy, right?)" +
                    " the only thing we need to verify is what username belongs to you (and this Discord account)! Therefore, it's just as safe as a OAuth2 connection, " +
                    "because no data get's transferred between the bot and Twitch, other than publicly available data (like chat messages).\n\n" +
                    "What this **will** allow Skuddbot to do: \n" +
                    "- Track your activity in chat and grant XP based on that. (Level up's do not get announced on Twitch, but happen in the background)\n\n" +
                    "Skuddbot **will not** be able to: \n" +
                    "- View your password (and all that other jazz)\n\n" +
                    "To verify what your Twitch username is, you'll need your Twitch Vertification Code which in your case is: `" + code + "`! Got that?\n" +
                    "To verify: You will need to head over to <https://www.twitch.tv/" + Constants.twitchBot + "> and type the following in the chat: `!verify " + code + "`\n" +
                    "Once you've done that, you will receive another PM from Skuddbot, with further instructions.\nOh and did I mention a nice tasty 1000xp for free? That's right! **1000xp**"));
        } else {
            MessagesUtils.addReaction(message,"I already know your Twitch username. Your Twitch username is: '" + su.getTwitchUsername() + "'. Is this incorrect, or you want to change it? Please message Tim.", EmojiEnum.X, false);
        }

    }

    /**
     * When the user entered their verify code on Twitch we send a follow up message to complete the linking process.
     *
     * @param user The Discord instance of the user.
     * @param twitch The Twitch instance of the user.
     */
    public static void sendFollowUp(SkuddUser user, SkuddUser twitch) {
        user.getVerifyMessage().delete().block();
        user.setVerifyMessage(null);

        user.setVerifyMessage(MessagesUtils.sendPM(Main.getInstance().getSkuddbot().getUserById(Snowflake.of(user.getId())).block(), "[" + Main.getInstance().getSkuddbot().getGuildById(Snowflake.of(serverID.get(user.getId()))).block().getName() + "] **Code recieved on Twitch.** *Processing... Please wait.*"));

        user.save();
        twitch.save();
        linkPending.put(user, twitch);

        ScheduledThreadPoolExecutor exec = new ScheduledThreadPoolExecutor(1);

        exec.schedule(() -> {
            user.getVerifyMessage().edit(spec -> {
                spec.setContent("[" + Main.getInstance().getSkuddbot().getGuildById(Snowflake.of(serverID.get(user.getId()))).block().getName() + "]  You did it! **GG!** \nRight, for the next bit you'll only need to verify the information below and confirm it's correct, then SkuddSync will be active on your account.\n\n" +
                        "Are you sure you want to merge the stats of this Discord together with the stats of the Twitch account **" + twitch.getTwitchUsername() + "**? (See overview below)\n" +
                        EmojiHelper.getEmoji("xp_icon") + "**:** Your **Discord account** has `" + user.getXp() + "xp` and your **Twitch Account** has `" + twitch.getXp() + "xp` which will put your total XP on `" + (user.getXp() + twitch.getXp()) + "xp`\n\n" +
                        "Type `!confirm` to confirm or `!cancel` to cancel.\n**WARNING: Once you link your accounts, you can't unlink them!**");
            });
        }, 3, TimeUnit.SECONDS);

    }

    /**
     * If the user confirms, we merge the accounts together and delete the Twitch one.
     *
     * @param message The message that triggered this command.
     */
    @SuppressWarnings("ConstantConditions")
    public static void confirm(Message message) {
        SkuddUser user = ProfileManager.getDiscord(message.getAuthor().get().getId().asString(), serverID.get(message.getAuthor().get().getId().asString()), true);

        if (!linkPending.containsKey(user)) {
            return;
        }

        user.setVerifyMessage(MessagesUtils.sendPM(message.getAuthor().get(),"[" + Main.getInstance().getSkuddbot().getGuildById(Snowflake.of(serverID.get(message.getAuthor().get().getId().asString()))).block().getName() + "] *Activating SkuddSync...*"));

        SkuddUser twitch = linkPending.get(ProfileManager.getDiscord(message.getAuthor().get().getId().asString(), serverID.get(message.getAuthor().get().getId().asString()), true));

        user.setTwitchUsername(twitch.getTwitchUsername());
        user.setXp(user.getXp() + twitch.getXp());
        user.setXp(user.getXp() + 1000);

        linkPending.remove(user);
        MySqlManager.deleteTwitch(user.getTwitchUsername(), serverID.get(message.getAuthor().get().getId().asString()));
        ProfileManager.swapTwitch(user, serverID.get(message.getAuthor().get().getId().asString()));

        ScheduledThreadPoolExecutor exec = new ScheduledThreadPoolExecutor(1);

        exec.schedule(() -> {
            user.getVerifyMessage().edit(spec -> {
                spec.setContent("[" + Main.getInstance().getSkuddbot().getGuildById(Snowflake.of(serverID.get(message.getAuthor().get().getId().asString()))).block().getName() + "] **SkuddSync is now active!**\n*+1000* " + EmojiHelper.getEmoji("xp_icon") + " - Account linked to Twitch.");
            });
            serverID.remove(user.getId());
        }, 3, TimeUnit.SECONDS);

        user.save();
        try{
            user.setRoles();
        } catch (IndexOutOfBoundsException e){
            Logger.info("Something happened... Something bad...");
        }

    }

    /**
     * When the user cancels we undo this process.
     *
     * @param message The message that triggered this command.
     */
    @SuppressWarnings("ConstantConditions")
    public static void cancel(Message message){
        SkuddUser user = ProfileManager.getDiscord(message.getAuthor().get().getId().asString(),serverID.get(message.getAuthor().get().getId().asString()), true);

        if(!linkPending.containsKey(user)){
            return;
        }

        linkPending.remove(user);
        MessagesUtils.sendPM(message.getAuthor().get() ,"[" + Main.getInstance().getSkuddbot().getGuildById(Snowflake.of(serverID.get(message.getAuthor().get().getId().asString()))).block().getName() + "] **Linking cancelled.** If you change your mind: You can always re-initiate this process by typing `!twitch` on the server.");
        serverID.remove(user.getId());

    }
}
