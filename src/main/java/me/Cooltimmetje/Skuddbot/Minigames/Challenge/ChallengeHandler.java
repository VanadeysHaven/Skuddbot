package me.Cooltimmetje.Skuddbot.Minigames.Challenge;

import discord4j.core.event.domain.message.ReactionAddEvent;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.TextChannel;
import discord4j.core.object.reaction.ReactionEmoji;
import discord4j.core.object.util.Snowflake;
import me.Cooltimmetje.Skuddbot.Enums.EmojiEnum;
import me.Cooltimmetje.Skuddbot.Enums.Platforms;
import me.Cooltimmetje.Skuddbot.Main;
import me.Cooltimmetje.Skuddbot.Profiles.ProfileManager;
import me.Cooltimmetje.Skuddbot.Profiles.Server;
import me.Cooltimmetje.Skuddbot.Profiles.ServerManager;
import me.Cooltimmetje.Skuddbot.Profiles.SkuddUser;
import me.Cooltimmetje.Skuddbot.Utilities.*;
import reactor.core.publisher.Flux;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * This allows people to challenge each other. Winner is picked at random.
 *
 * @author Tim (Cooltimmetje)
 * @version v0.5.1-ALPHA
 * @since v0.4.3-ALPHA
 */

public class ChallengeHandler {

    private String serverId;

    public ChallengeHandler(String serverId){
        this.serverId = serverId;
        this.cooldownManager = new CooldownManager(COOLDOWN);
    }

    private static final int COOLDOWN = 300;
    private static final int XP_REWARD = 100;
    private static final int STREAK_REWARD = 50;

    private CooldownManager cooldownManager;

    public void clearCooldowns(){
        cooldownManager.clearAll();
    }

    private String updateStats(SkuddUser winner, SkuddUser loser, Platforms platform){
        boolean newHighestStreak = false;
        StringBuilder rewardString = new StringBuilder();
        String winnerName = "hi";
        Guild guild;
        switch (platform){
            case DISCORD:
                guild = Main.getInstance().getSkuddbot().getGuildById(Snowflake.of(winner.getServerID())).block();
                winnerName = guild.getMemberById(Snowflake.of(winner.getId())).block().getDisplayName();
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
                rewardString.append(winnerName).append(":").append(" *+").append(XP_REWARD).append(" ").append(EmojiHelper.getEmoji("xp_icon")).append("*");
                break;
            case TWITCH:
                rewardString.append(winnerName).append(": +").append(XP_REWARD).append(" XP");
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
                    rewardString.append(":** *").append(winner.getChallengeStreak()).append(" wins*").append(" (+").append(STREAK_REWARD * (winner.getChallengeStreak() - 1)).append(" bonus ").append(EmojiHelper.getEmoji("xp_icon")).append(")");
                    break;
                case TWITCH:
                    rewardString.append(" | Win streak ");
                    if(winner.getChallengeStreak() > 2){
                        rewardString.append("continued");
                    } else {
                        rewardString.append("started");
                    }
                    rewardString.append(": ").append(winner.getChallengeStreak()).append(" wins").append(" (+").append(STREAK_REWARD * (winner.getChallengeStreak() - 1)).append(" bonus XP)");
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
    public HashMap<Member,Member> targetPunch = new HashMap<>();

    public void run(Message message){
        if(cooldownManager.isOnCooldown(message.getId().asString())){
            MessagesUtils.addReaction(message, "Hold on there, **" + message.getAuthorAsMember().block().getMention() + "**, you're still wounded from the last fight.", EmojiEnum.HOURGLASS_FLOWING_SAND, false);
            return;
        }


        String[] args = message.getContent().get().split(" ");
        if(args.length == 1){
            MessagesUtils.addReaction(message, "You need to specify if you want a open challenge or which user you want to fight.", EmojiEnum.X, false);
            return;
        }

        if(!args[1].equalsIgnoreCase(openInvoker) && message.getUserMentions().collectList().block().isEmpty()){
            MessagesUtils.addReaction(message, "Allowed arguments are `"  + openInvoker + "` or a user mention (which is not Skuddbot or yourself).", EmojiEnum.X, false);
            return;
        }

        if(!message.getUserMentions().collectList().block().isEmpty()){
            if(message.getUserMentions().collectList().block().get(0) == message.getAuthor().get()){
                MessagesUtils.addReaction(message, "You can't challenge yourself.", EmojiEnum.X, false);
                return;
            }

            if(message.getUserMentions().collectList().block().get(0) == Main.getInstance().getSkuddbot().getSelf().block()){
                MessagesUtils.addReaction(message, "You can't challenge me!", EmojiEnum.X, false);
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

    private void startOpenChallenge(Message message){
        openChallenges.add(message.getAuthor().get().getId().asString());

        Message messageSent = MessagesUtils.sendPlain(MessageFormat.format(EmojiEnum.CROSSED_SWORDS.getUnicode() + "**{0}** has put down an open fight, anyone can accept it! Click the {1} to accept.",
                message.getAuthorAsMember().block().getDisplayName(), EmojiEnum.CROSSED_SWORDS.getUnicode()), message.getChannel().block(), false);
        messageSent.addReaction(ReactionEmoji.unicode(EmojiEnum.CROSSED_SWORDS.getUnicode())).block();

        senderMessage.put(message.getAuthorAsMember().block().getId().asString(), message.getId().asString());
        botMessage.put(message.getAuthorAsMember().block().getId().asString(), messageSent.getId().asString());
    }

    private void startInviteChallenge(Message message){
        if(outstandingChallenges.get(message.getUserMentions().collectList().block().get(0).getId().asString()).equals(message.getAuthor().get().getId().asString())){ //Challenge was accepted
            fight(message.getUserMentions().collectList().block().get(0).asMember(Snowflake.of(serverId)).block(), message.getAuthorAsMember().block(), message, (TextChannel) message.getChannel().block());
        } else { //Challenge was not accepted.
            outstandingChallenges.put(message.getAuthor().get().getId().asString(), message.getUserMentions().collectList().block().get(0).getId().asString());
            senderMessage.put(message.getAuthor().get().getId().asString(), message.getId().asString());

            Message messageBot = MessagesUtils.sendPlain(EmojiEnum.CROSSED_SWORDS.getUnicode() + " **" + message.getAuthorAsMember().block().getDisplayName() + "** has challenged **" + message.getUserMentions().collectList().block().get(0).asMember(Snowflake.of(serverId)).block().getDisplayName() + "** to a fight! " +
                    "To accept click the reaction below!", message.getChannel().block(), false);
            messageBot.addReaction(ReactionEmoji.unicode(EmojiEnum.CROSSED_SWORDS.getUnicode())).block();

            botMessage.put(message.getAuthor().get().getId().asString(), messageBot.getId().asString());
            targetPunch.put(message.getAuthorAsMember().block(), message.getUserMentions().collectList().block().get(0).asMember(Snowflake.of(serverId)).block());
        }
    }

    private Member getChallenger(Message message){
        for (String s : botMessage.keySet()){
            if (botMessage.get(s).equals(message.getId().asString())) {
                return Main.getInstance().getSkuddbot().getUserById(Snowflake.of(s)).block().asMember(Snowflake.of(serverId)).block();
            }
        }

        return null;
    }

    public void reactionAccept(ReactionAddEvent event){
        if(event.getUser().block().isBot()){
            return;
        }
        if(!botMessage.containsValue(event.getMessage().block().getId().asString())){
            return;
        }
        Member challengerOne = getChallenger(event.getMessage().block());
        if(challengerOne == null){
            return;
        }
        if(!event.getEmoji().asUnicodeEmoji().get().getRaw().equals(EmojiEnum.CROSSED_SWORDS.getUnicode())) {
            return;
        }

        if(openChallenges.contains(challengerOne.getId().asString())) {
            openChallenges.remove(challengerOne.getId().asString());
            Member challengerTwo = event.getUser().block().asMember(Snowflake.of(serverId)).block();
            if(challengerOne != challengerTwo) {
                Logger.info("An open challenge was accepted.");

                fight(challengerOne, challengerTwo, null, (TextChannel) event.getChannel().block());
            }
        } else if (outstandingChallenges.containsKey(challengerOne.getId().asString())){
            if(botMessage.containsValue(event.getMessage().block().getId().asString())){
                if(EmojiEnum.getByUnicode(event.getEmoji().asUnicodeEmoji().get().getRaw()) == EmojiEnum.CROSSED_SWORDS){
                    if(event.getMessage().block().getReactors(event.getEmoji()).collectList().block().contains(Main.client().getUserById(Snowflake.of(outstandingChallenges.get(challengerOne.getId().asString()))).block())){
                        Member challengerTwo = Main.getInstance().getSkuddbot().getUserById(Snowflake.of(outstandingChallenges.get(challengerOne.getId().asString()))).block().asMember(Snowflake.of(serverId)).block();
                        fight(challengerOne, challengerTwo, null, (TextChannel) event.getMessage().block().getChannel().block());
                    }
                } else if(EmojiEnum.getByUnicode(event.getEmoji().asUnicodeEmoji().get().getRaw()) == EmojiEnum.EYES){
                    if(Constants.adminUser.contains(event.getUser().block().getId().asString())){
                        Member challengerTwo = Main.client().getUserById(Snowflake.of(outstandingChallenges.get(challengerOne))).block().asMember(Snowflake.of(serverId)).block();
                        fight(challengerOne, challengerTwo, null, (TextChannel) event.getMessage().block().getChannel().block());
                    }
                }
            }
        }
    }

    private void fight(Member challengerOne, Member challengerTwo, Message message, TextChannel channel){
        targetPunch.put(challengerOne, challengerTwo);
        targetPunch.put(challengerTwo, challengerOne);
        Server server = ServerManager.getServer(message.getGuild().block().getId().asString());
        ScheduledThreadPoolExecutor exec = new ScheduledThreadPoolExecutor(2);

        exec.schedule(() -> {
            channel.bulkDelete(Flux.just(Snowflake.of(senderMessage.get(challengerOne.getId().asString())), Snowflake.of(botMessage.get(challengerOne.getId().asString()))));

            senderMessage.remove(challengerOne.getId().asString());
            botMessage.remove(challengerOne.getId().asString());
            outstandingChallenges.remove(challengerOne);
        },10, TimeUnit.MILLISECONDS);

        cooldownManager.applyCooldown(challengerOne.getId().asString());
        cooldownManager.applyCooldown(challengerTwo.getId().asString());

        Member preWinner = (MiscUtils.randomInt(1,2) == 1) ? challengerOne : challengerTwo;
        if(Constants.rigged.containsKey(challengerOne.getId().asString())){
            if(Constants.rigged.get(challengerOne.getId().asString())){
                preWinner = challengerOne;
            } else {
                preWinner = challengerTwo;
            }
            Constants.rigged.remove(challengerOne.getId().asString());
        }
        if(Constants.rigged.containsKey(challengerTwo.getId().asString())){
            if(Constants.rigged.get(challengerTwo.getId().asString())) {
                preWinner = challengerTwo;
            } else {
                preWinner = challengerOne;
            }
            Constants.rigged.remove(challengerTwo.getId().asString());
        }
        final Member winner = preWinner;
        Member loser = preWinner == challengerOne ? challengerTwo : challengerOne;

        SkuddUser suWinner = ProfileManager.getDiscord(winner, true);
        SkuddUser suLoser = ProfileManager.getDiscord(loser, true);

        String fightAnnounceFormat = "{0} **{1}** and **{2}** go head to head in {3}, who will win? *3*... *2*... *1*... **FIGHT!**";
        MessagesUtils.sendPlain(MessageFormat.format(fightAnnounceFormat, EmojiEnum.CROSSED_SWORDS.getUnicode(), challengerOne.getDisplayName(), challengerTwo.getDisplayName(), server.getArenaName()), channel, false);
        channel.type().block();

        exec.schedule(() -> {
            String rewards = updateStats(suWinner, suLoser, Platforms.DISCORD);
            String messageToSend = MessageFormat.format("{0} The crowd goes wild, but suddenly a scream of victory sounds! **{1}** has won the fight! \n\n{2}", EmojiEnum.CROSSED_SWORDS.getUnicode(), winner.getDisplayName(), rewards);
            MessagesUtils.sendPlain(messageToSend, channel, false);

            targetPunch.remove(challengerOne);
            targetPunch.remove(challengerTwo);
        }, 5, TimeUnit.SECONDS);
    }

    private void deletePreviousChallenge(Message message){
        if(senderMessage.containsKey(message.getAuthor().get().getId().asString())) {
            Flux<Snowflake> messages = Flux.just(Snowflake.of(senderMessage.get(message.getAuthor().get().getId().asString())), Snowflake.of(botMessage.get(message.getAuthor().get().getId().asString())));
            TextChannel tc = (TextChannel) message.getChannel().block();
            tc.bulkDelete(messages).subscribe();


            senderMessage.remove(message.getAuthor().get().getId().asString());
            botMessage.remove(message.getAuthor().get().getId().asString());
        }


        openChallenges.remove(message.getAuthor().get().getId().asString());
        outstandingChallenges.remove(message.getAuthor().get().getId().asString());
    }

    // ---Twitch Stuff---
    private HashMap<String, String> outstandingChallengesTwitch = new HashMap<>();

    public void run(String sender, String message, String twitchChannel){
        String[] args = message.toLowerCase().split(" ");

        if(cooldownManager.isOnCooldown(sender)) return;

        if(args.length < 2){
            Main.getSkuddbotTwitch().send(sender + ", you did not specify anyone to challenge!", twitchChannel);
            return;
        }

        if(args[1].startsWith("@"))
            args[1] = args[1].substring(1);

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

            Main.getSkuddbotTwitch().send(MessageFormat.format("{0} has challenged {1} to a fight!  Type \"!challenge {0}\" to accept.", sender, args[1]), twitchChannel);
        }
    }

    private void fight(String challengerOne, String challengerTwo, String twitchChannel){
        Server server = ServerManager.getTwitch(twitchChannel.substring(1));
        ScheduledThreadPoolExecutor exec = new ScheduledThreadPoolExecutor(1);

        cooldownManager.applyCooldown(challengerOne);
        cooldownManager.applyCooldown(challengerTwo);
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
