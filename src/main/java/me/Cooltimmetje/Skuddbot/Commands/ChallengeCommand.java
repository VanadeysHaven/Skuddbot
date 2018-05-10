package me.Cooltimmetje.Skuddbot.Commands;

import me.Cooltimmetje.Skuddbot.Enums.EmojiEnum;
import me.Cooltimmetje.Skuddbot.Main;
import me.Cooltimmetje.Skuddbot.Profiles.ProfileManager;
import me.Cooltimmetje.Skuddbot.Profiles.SkuddUser;
import me.Cooltimmetje.Skuddbot.Utilities.MessagesUtils;
import me.Cooltimmetje.Skuddbot.Utilities.MiscUtils;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * This allows people to challenge each other. Winner is picked at random.
 *
 * @author Tim (Cooltimmetje)
 * @version v0.4.3-ALPHA
 * @since v0.4.3-ALPHA
 */

public class ChallengeCommand {

    public static int cooldown = 300; //cooldown in seconds
    public static int xpReward = 50;

    public static HashMap<String, Long> cooldowns = new HashMap<>();

    public static HashMap<String, String> senderMessage = new HashMap<>();
    public static HashMap<String, String> botMessage = new HashMap<>();

    public static HashMap<IUser, IUser> outstandingChallenges = new HashMap<>();

    public static void run(IMessage message) {
        if(cooldowns.containsKey(message.getAuthor().getStringID())){
            if((System.currentTimeMillis() - cooldowns.get(message.getAuthor().getStringID())) < (cooldown * 1000)){
                MessagesUtils.addReaction(message, "Hold on there, " + message.getAuthor().mention() + ", you're still wounded from the last fight.", EmojiEnum.HOURGLASS_FLOWING_SAND);
                return;
            }
        }

        if(message.getMentions().isEmpty()){
            MessagesUtils.addReaction(message, "You didn't specify who you want to challenge.", EmojiEnum.X);
            return;
        }

        if(message.getMentions().get(0) == message.getAuthor()){
            MessagesUtils.addReaction(message, "You can't challenge yourself.", EmojiEnum.X);
            return;
        }

        if(message.getMentions().get(0) == Main.getInstance().getSkuddbot().getOurUser()){
            MessagesUtils.addReaction(message, "You can't challenge me!", EmojiEnum.X);
            return;
        }

        if(outstandingChallenges.get(message.getMentions().get(0)) == message.getAuthor()){ //Challenge was accepted
            ScheduledThreadPoolExecutor exec = new ScheduledThreadPoolExecutor(2);
            IUser challengerOne = message.getMentions().get(0);
            IUser challengerTwo = message.getAuthor();
            IChannel channel = message.getChannel();

//            Main.getInstance().getSkuddbot().getMessageByID(Long.parseLong(senderMessage.get(challengerOne.getStringID()))).delete();
//            Main.getInstance().getSkuddbot().getMessageByID(Long.parseLong(botMessage.get(challengerOne.getStringID()))).delete();
//            message.delete();
//
//            ArrayList<IMessage> deleteMessages = new ArrayList<>(Arrays.asList(
//                    Main.getInstance().getSkuddbot().getMessageByID(Long.parseLong(senderMessage.get(challengerOne.getStringID()))),
//                    Main.getInstance().getSkuddbot().getMessageByID(Long.parseLong(botMessage.get(challengerOne.getStringID()))),
//                    message));

            exec.schedule(() -> {
                channel.bulkDelete(new ArrayList<>(Arrays.asList(
                        Main.getInstance().getSkuddbot().getMessageByID(Long.parseLong(senderMessage.get(challengerOne.getStringID()))),
                        Main.getInstance().getSkuddbot().getMessageByID(Long.parseLong(botMessage.get(challengerOne.getStringID()))),
                        message)));

                senderMessage.remove(challengerOne.getStringID());
                botMessage.remove(challengerOne.getStringID());
                outstandingChallenges.remove(challengerOne);
            },10, TimeUnit.MILLISECONDS);

            cooldowns.put(challengerOne.getStringID(), System.currentTimeMillis());
            cooldowns.put(challengerTwo.getStringID(), System.currentTimeMillis());

            IUser winner/*winner chicken dinner*/ = (MiscUtils.randomInt(1,2) == 1) ? challengerOne : challengerTwo;

            IMessage messageBot = MessagesUtils.sendPlain(EmojiEnum.CROSSED_SWORDS.getEmoji() + " **" + challengerOne.getDisplayName(channel.getGuild()) + "** and **" +
                    challengerTwo.getDisplayName(channel.getGuild()) + "** go head to head in the Rayscooter arena, who will win? 3... 2... 1... **FIGHT!**", channel, false);

            exec.schedule(() -> {
                try {
                    IMessage messageResult = MessagesUtils.sendPlain(EmojiEnum.CROSSED_SWORDS.getEmoji() + " The crowd goes wild but suddenly a scream of victory sounds! **" + winner.getDisplayName(channel.getGuild()) + "** has won the fight!\n\n" +
                            winner.getDisplayName(channel.getGuild()) + ": *+" + xpReward + " XP*", channel, false);

                    SkuddUser user = ProfileManager.getDiscord(winner.getStringID(), message.getGuild().getStringID(), true);

                    user.setXp(user.getXp() + xpReward);
                    user.calcXP(false, messageResult);
                } catch (MissingPermissionsException | RateLimitException | DiscordException e) {
                    e.printStackTrace();
                }
            }, 5, TimeUnit.SECONDS);
            assert messageBot != null;
//            exec.schedule(messageBot::delete, 10, TimeUnit.SECONDS);
        } else { //Challenge was not accepted.
            if(senderMessage.containsKey(message.getAuthor().getStringID())) {
                message.getChannel().bulkDelete(new ArrayList<>(Arrays.asList(
                        Main.getInstance().getSkuddbot().getMessageByID(Long.parseLong(senderMessage.get(message.getAuthor().getStringID()))),
                        Main.getInstance().getSkuddbot().getMessageByID(Long.parseLong(botMessage.get(message.getAuthor().getStringID())))
                )));
            }
            outstandingChallenges.put(message.getAuthor(), message.getMentions().get(0));

            senderMessage.put(message.getAuthor().getStringID(), message.getStringID());

            IMessage messageBot = MessagesUtils.sendPlain(EmojiEnum.CROSSED_SWORDS.getEmoji() + " **" + message.getAuthor().getDisplayName(message.getGuild()) + "** has challenged **" + message.getMentions().get(0).getDisplayName(message.getGuild()) + "** to a fight! " +
                    "To accept type `!challenge @" + message.getAuthor().getName()  + "#" + message.getAuthor().getDiscriminator() + "`!", message.getChannel(), false);

            botMessage.put(message.getAuthor().getStringID(), messageBot.getStringID());
        }
    }

}
