package me.Cooltimmetje.Skuddbot.Minigames.Challenge;

import com.vdurmont.emoji.EmojiManager;
import me.Cooltimmetje.Skuddbot.Enums.EmojiEnum;
import me.Cooltimmetje.Skuddbot.Enums.Platforms;
import me.Cooltimmetje.Skuddbot.Main;
import me.Cooltimmetje.Skuddbot.Profiles.ProfileManager;
import me.Cooltimmetje.Skuddbot.Profiles.Server;
import me.Cooltimmetje.Skuddbot.Profiles.ServerManager;
import me.Cooltimmetje.Skuddbot.Profiles.SkuddUser;
import me.Cooltimmetje.Skuddbot.Utilities.*;
import sx.blah.discord.handle.impl.events.guild.channel.message.reaction.ReactionAddEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.RequestBuffer;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * This allows people to challenge each other. Winner is picked at random.
 *
 * @author Tim (Cooltimmetje)
 * @version v0.4.42-ALPHA
 * @since v0.4.3-ALPHA
 */

public class ChallengeHandler {

    private String serverId;

    public ChallengeHandler(String serverId){
        this.serverId = serverId;
    }

    private int cooldown = 300;
    private int xpReward = 100;
    private int streakReward = 50;

    public HashMap<String,Long> cooldowns = new HashMap<>();

    private String updateStats(SkuddUser winner, SkuddUser loser, Platforms platform){
        boolean newHighestStreak = false;
        StringBuilder rewardString = new StringBuilder();
        String winnerName = "hi";
        IGuild guild;
        switch (platform){
            case DISCORD:
                guild = Main.getInstance().getSkuddbot().getGuildByID(Long.parseLong(winner.getServerID()));
                winnerName = Main.getInstance().getSkuddbot().getUserByID(Long.parseLong(winner.getId())).getDisplayName(guild);
                break;
            case TWITCH:
                winnerName = winner.getTwitchUsername();
                break;
        }

        winner.setChallengeWins(winner.getChallengeWins() + 1);
        winner.setChallengeStreak(winner.getChallengeStreak() + 1);
        if(winner.getChallengeStreak() > winner.getChallengeLongestStreak()){
            newHighestStreak = true;
            winner.setChallengeLongestStreak(winner.getChallengeStreak());
        }
        loser.setChallengeLosses(loser.getChallengeLosses() + 1);
        loser.setChallengeStreak(0);

        switch (platform){
            case DISCORD:
                rewardString.append(winnerName).append(":").append(" *+").append(xpReward).append(" ").append(EmojiHelper.getEmoji("xp_icon")).append("*");
                break;
            case TWITCH:
                rewardString.append(winnerName).append(": +").append(xpReward).append(" XP");
                break;
        }

        if(winner.getChallengeStreak() > 1){
            switch (platform){
                case DISCORD:
                    rewardString.append(" | **Win streak ");
                    if(winner.getChallengeStreak() > 2){
                        rewardString.append("continued");
                    } else {
                        rewardString.append("started");
                    }
                    rewardString.append(":** *").append(winner.getChallengeStreak()).append(" wins*").append(" (+").append(streakReward * (winner.getChallengeStreak() - 1)).append(" bonus ").append(EmojiHelper.getEmoji("xp_icon")).append(")");
                    break;
                case TWITCH:
                    rewardString.append(" | Win streak ");
                    if(winner.getChallengeStreak() > 2){
                        rewardString.append("continued");
                    } else {
                        rewardString.append("started");
                    }
                    rewardString.append(": ").append(winner.getChallengeStreak()).append(" wins").append(" (+").append(streakReward * (winner.getChallengeStreak() - 1)).append(" bonus XP)");
                    break;
            }
        }

        if(newHighestStreak && winner.getChallengeStreak() > 1){
            switch (platform){
                case DISCORD:
                    rewardString.append(" | **New longest winstreak:** *").append(winner.getChallengeLongestStreak()).append(" wins*");
                    break;
                case TWITCH:
                    rewardString.append(" | New longest winstreak: ").append(winner.getChallengeLongestStreak()).append(" wins");
                    break;
            }
        }

        return rewardString.toString().trim();
    }

    //DISCORD
    private HashMap<String,String> senderMessage = new HashMap<>();
    private HashMap<String,String> botMessage = new HashMap<>();
    private String openInvoker = "-open";

    private HashMap<String,String> outstandingChallenges = new HashMap<>();
    private ArrayList<String> openChallenges = new ArrayList<>();
    public HashMap<IUser,IUser> targetPunch = new HashMap<>();

    public void run(IMessage message){
        if(cooldowns.containsKey(message.getAuthor().getStringID())){
            if((System.currentTimeMillis() - cooldowns.get(message.getAuthor().getStringID())) < (cooldown * 1000)){
                MessagesUtils.addReaction(message, "Hold on there, **" + message.getAuthor().mention() + "**, you're still wounded from the last fight.", EmojiEnum.HOURGLASS_FLOWING_SAND);
                return;
            }
        }

        String[] args = message.getContent().split(" ");
        if(args.length == 1){
            MessagesUtils.addReaction(message, "You need to specify if you want a open challenge or which user you want to fight.", EmojiEnum.X);
            return;
        }

        if(!args[1].equalsIgnoreCase(openInvoker) && message.getMentions().isEmpty()){
            MessagesUtils.addReaction(message, "Allowed arguments are `"  + openInvoker + "` or a user mention (which is not Skuddbot or yourself).", EmojiEnum.X);
            return;
        }

        if(!message.getMentions().isEmpty()){
            if(message.getMentions().get(0) == message.getAuthor()){
                MessagesUtils.addReaction(message, "You can't challenge yourself.", EmojiEnum.X);
                return;
            }

            if(message.getMentions().get(0) == Main.getInstance().getSkuddbot().getOurUser()){
                MessagesUtils.addReaction(message, "You can't challenge me!", EmojiEnum.X);
                return;
            }
        }

        deletePreviousChallenge(message);

        if(args[1].equalsIgnoreCase(openInvoker)){
            startOpenChallenge(message);
            return;
        }

        startInviteChallenge(message);
    }

    private void startOpenChallenge(IMessage message){
        openChallenges.add(message.getAuthor().getStringID());

        IMessage messageSent = MessagesUtils.sendPlain(MessageFormat.format(EmojiEnum.CROSSED_SWORDS.getEmoji() + "**{0}** has put down an open fight, anyone can accept it! Click the {1} to accept.",
                message.getAuthor().getDisplayName(message.getGuild()), EmojiEnum.CROSSED_SWORDS.getEmoji()), message.getChannel(), false);
        RequestBuffer.request(() -> messageSent.addReaction(EmojiManager.getForAlias(EmojiEnum.CROSSED_SWORDS.getAlias())));

        senderMessage.put(message.getAuthor().getStringID(), message.getStringID());
        botMessage.put(message.getAuthor().getStringID(), messageSent.getStringID());
    }

    private void startInviteChallenge(IMessage message){
        if(outstandingChallenges.get(message.getMentions().get(0).getStringID()) == message.getAuthor().getStringID()){ //Challenge was accepted
            fight(message.getMentions().get(0),message.getAuthor(), message, message.getChannel());
        } else { //Challenge was not accepted.
            outstandingChallenges.put(message.getAuthor().getStringID(), message.getMentions().get(0).getStringID());
            senderMessage.put(message.getAuthor().getStringID(), message.getStringID());

            IMessage messageBot = MessagesUtils.sendPlain(EmojiEnum.CROSSED_SWORDS.getEmoji() + " **" + message.getAuthor().getDisplayName(message.getGuild()) + "** has challenged **" + message.getMentions().get(0).getDisplayName(message.getGuild()) + "** to a fight! " +
                    "To accept click the reaction below!", message.getChannel(), false);
            messageBot.addReaction(EmojiManager.getForAlias(EmojiEnum.CROSSED_SWORDS.getAlias()));

            botMessage.put(message.getAuthor().getStringID(), messageBot.getStringID());
            targetPunch.put(message.getAuthor(), message.getMentions().get(0));
        }
    }

    private IUser getChallenger(IMessage message){
        for (String s : botMessage.keySet()){
            if (botMessage.get(s).equals(message.getStringID())) {
                return Main.getInstance().getSkuddbot().getUserByID(Long.parseLong(s));
            }
        }

        return null;
    }

    public void reactionAccept(ReactionAddEvent event){
        if(event.getUser().isBot()){
            return;
        }
        if(!botMessage.containsValue(event.getReaction().getMessage().getStringID())){
            return;
        }
        IUser challengerOne = getChallenger(event.getMessage());
        if(challengerOne == null){
            return;
        }
        if(!event.getReaction().getEmoji().getName().equals(EmojiEnum.CROSSED_SWORDS.getEmoji())) {
            return;
        }

        if(openChallenges.contains(challengerOne.getStringID())) {
            openChallenges.remove(challengerOne.getStringID());
            IUser challengerTwo = event.getUser();
            if(challengerOne != challengerTwo) {
                Logger.info("An open challenge was accepted.");

                fight(challengerOne, challengerTwo, null, event.getChannel());
            }
        } else if (outstandingChallenges.containsKey(challengerOne.getStringID())){
            if(botMessage.containsValue(event.getReaction().getMessage().getStringID())){
                if(EmojiEnum.getByUnicode(event.getReaction().getEmoji().getName()) == EmojiEnum.CROSSED_SWORDS){
                    if(event.getReaction().getUserReacted(Main.getInstance().getSkuddbot().getUserByID(Long.parseLong(outstandingChallenges.get(challengerOne.getStringID()))))){ //Accepted
                        IUser challengerTwo = Main.getInstance().getSkuddbot().getUserByID(Long.parseLong(outstandingChallenges.get(challengerOne.getStringID())));
                        fight(challengerOne, challengerTwo, null, event.getReaction().getMessage().getChannel());
                    }
                } else if(EmojiEnum.getByUnicode(event.getReaction().getEmoji().getName()) == EmojiEnum.EYES){
                    if(Constants.adminUser.contains(event.getUser().getStringID())){
                        IUser challengerTwo = Main.getInstance().getSkuddbot().getUserByID(Long.parseLong(outstandingChallenges.get(challengerOne)));
                        fight(challengerOne, challengerTwo, null, event.getReaction().getMessage().getChannel());
                    }
                }
            }
        }
    }

    private void fight(IUser challengerOne, IUser challengerTwo, IMessage message, IChannel channel){
        targetPunch.put(challengerOne, challengerTwo);
        targetPunch.put(challengerTwo, challengerOne);
        Server server = ServerManager.getServer(channel.getGuild().getStringID());
        ScheduledThreadPoolExecutor exec = new ScheduledThreadPoolExecutor(2);

        exec.schedule(() -> {
            ArrayList<IMessage> toDelete = new ArrayList<>(Arrays.asList(
                    Main.getInstance().getSkuddbot().getMessageByID(Long.parseLong(senderMessage.get(challengerOne.getStringID()))),
                    Main.getInstance().getSkuddbot().getMessageByID(Long.parseLong(botMessage.get(challengerOne.getStringID())))));
            if(message != null){
                toDelete.add(message);
            }
            channel.bulkDelete(toDelete);

            senderMessage.remove(challengerOne.getStringID());
            botMessage.remove(challengerOne.getStringID());
            outstandingChallenges.remove(challengerOne);
        },10, TimeUnit.MILLISECONDS);

        cooldowns.put(challengerOne.getStringID(), System.currentTimeMillis());
        cooldowns.put(challengerTwo.getStringID(), System.currentTimeMillis());

        IUser preWinner = (MiscUtils.randomInt(1,2) == 1) ? challengerOne : challengerTwo;
        if(Constants.rigged.containsKey(challengerOne.getStringID())){
            if(Constants.rigged.get(challengerOne.getStringID())){
                preWinner = challengerOne;
            } else {
                preWinner = challengerTwo;
            }
            Constants.rigged.remove(challengerOne.getStringID());
        }
        if(Constants.rigged.containsKey(challengerTwo.getStringID())){
            if(Constants.rigged.get(challengerTwo.getStringID())) {
                preWinner = challengerTwo;
            } else {
                preWinner = challengerOne;
            }
            Constants.rigged.remove(challengerTwo.getStringID());
        }
        final IUser winner = preWinner;
        IUser loser = preWinner == challengerOne ? challengerTwo : challengerOne;

        SkuddUser suWinner = ProfileManager.getDiscord(winner, channel.getGuild(), true);
        SkuddUser suLoser = ProfileManager.getDiscord(loser, channel.getGuild(), true);

        String fightAnnounceFormat = "{0} **{1}** and **{2}** go head to head in {3}, who will win? *3*... *2*... *1*... **FIGHT!**";
        MessagesUtils.sendPlain(MessageFormat.format(fightAnnounceFormat, EmojiEnum.CROSSED_SWORDS.getEmoji(), challengerOne.getDisplayName(channel.getGuild()), challengerTwo.getDisplayName(channel.getGuild()), server.getArenaName()), channel, false);
        channel.setTypingStatus(true);

        exec.schedule(() -> {
            String rewards = updateStats(suWinner, suLoser, Platforms.DISCORD);
            String messageToSend = MessageFormat.format("{0} The crowd goes wild, but suddenly a scream of victory sounds! **{1}** has won the fight! \n\n{2}", EmojiEnum.CROSSED_SWORDS.getEmoji(), winner.getDisplayName(channel.getGuild()), rewards);
            MessagesUtils.sendPlain(messageToSend, channel, false);

            targetPunch.remove(challengerOne);
            targetPunch.remove(challengerTwo);
        }, 5, TimeUnit.SECONDS);
    }

    private void deletePreviousChallenge(IMessage message){
        if(senderMessage.containsKey(message.getAuthor().getStringID())) {
            message.getChannel().bulkDelete(new ArrayList<>(Arrays.asList(
                    Main.getInstance().getSkuddbot().getMessageByID(Long.parseLong(senderMessage.get(message.getAuthor().getStringID()))),
                    Main.getInstance().getSkuddbot().getMessageByID(Long.parseLong(botMessage.get(message.getAuthor().getStringID())))
            )));

            senderMessage.remove(message.getAuthor().getStringID());
            botMessage.remove(message.getAuthor().getStringID());
        }

        openChallenges.remove(message.getAuthor().getStringID());
        outstandingChallenges.remove(message.getAuthor().getStringID());
    }

    // ---Twitch Stuff---
    private HashMap<String, String> outstandingChallengesTwitch = new HashMap<>();

    public void run(String sender, String message, String twitchChannel){
        String[] args = message.toLowerCase().split(" ");

        if(cooldowns.containsKey(sender)){
            if((System.currentTimeMillis() - cooldowns.get(sender)) < (cooldown*1000)){
                return;
            }
        }

        if(args.length < 2){
            Main.getSkuddbotTwitch().send(sender + ", you did not specify anyone to challenge!", twitchChannel);
            return;
        }

        if(args[1].startsWith("@")){
            args[1] = args[1].substring(1);
        }

        if(args[1].equalsIgnoreCase(sender)){
            Main.getSkuddbotTwitch().send(sender + ", you can't challenge yourself!", twitchChannel);
            return;
        }

        if(args[1].equalsIgnoreCase(Main.getSkuddbotTwitch().getNick())){
            Main.getSkuddbotTwitch().send(sender + ", you can't challenge me!", twitchChannel);
            return;
        }

        if(outstandingChallengesTwitch.containsKey(args[1]) && outstandingChallengesTwitch.get(args[1]).equalsIgnoreCase(sender)){ //Challenge was accepted
            fight(args[1], sender, twitchChannel);
        } else { //Challenge was not accepted
            outstandingChallengesTwitch.put(sender, args[1]);

            Main.getSkuddbotTwitch().send(MessageFormat.format("{0} has challenged {1} to a fight!  Type s!challenge {0} to accept.", sender, args[1]), twitchChannel);
        }
    }

    private void fight(String challengerOne, String challengerTwo, String twitchChannel){
        Server server = ServerManager.getTwitch(twitchChannel.substring(1));
        ScheduledThreadPoolExecutor exec = new ScheduledThreadPoolExecutor(1);

        cooldowns.put(challengerOne, System.currentTimeMillis());
        cooldowns.put(challengerTwo, System.currentTimeMillis());
        outstandingChallengesTwitch.remove(challengerOne);

        int winInt = MiscUtils.randomInt(1,2);

        String winner = winInt == 1 ? challengerOne : challengerTwo;
        String loser = winInt != 1 ? challengerOne : challengerTwo;

        SkuddUser suWinner = ProfileManager.getTwitch(winner, twitchChannel.substring(1), true);
        SkuddUser suLoser = ProfileManager.getTwitch(loser, twitchChannel.substring(1), true);

        Main.getSkuddbotTwitch().send(MessageFormat.format("{0} and {1} go head to head in {2}, who will win? 3... 2... 1... FIGHT!", challengerOne, challengerTwo, server.getArenaName()), twitchChannel);

        exec.schedule(() -> {
            String rewards = updateStats(suWinner, suLoser, Platforms.TWITCH);

            Main.getSkuddbotTwitch().send(MessageFormat.format("The crowd goes wild but suddenly a scream of victory sounds! {0} has won the fight! | {1}", winner, rewards), twitchChannel);
        }, 5, TimeUnit.SECONDS);
    }


}
