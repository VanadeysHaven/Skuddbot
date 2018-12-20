package me.Cooltimmetje.Skuddbot.Minigames.Challenge;

import me.Cooltimmetje.Skuddbot.Enums.EmojiEnum;
import me.Cooltimmetje.Skuddbot.Enums.Platforms;
import me.Cooltimmetje.Skuddbot.Main;
import me.Cooltimmetje.Skuddbot.Profiles.SkuddUser;
import me.Cooltimmetje.Skuddbot.Utilities.EmojiHelper;
import me.Cooltimmetje.Skuddbot.Utilities.MessagesUtils;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * This allows people to challenge each other. Winner is picked at random.
 *
 * @author Tim (Cooltimmetje)
 * @version v0.4.41-ALPHA
 * @since v0.4.3-ALPHA
 */

public class ChallengeHandler {

    private String serverId;

    public ChallengeHandler(String serverId){
        this.serverId = serverId;
    }

    private int cooldown = 300;
    private int xpReward = 50;
    private int streakReward = 25;

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

        deletePreviousMessages(message);



    }

    private void deletePreviousMessages(IMessage message){
        if(senderMessage.containsKey(message.getAuthor().getStringID())) {
            message.getChannel().bulkDelete(new ArrayList<>(Arrays.asList(
                    Main.getInstance().getSkuddbot().getMessageByID(Long.parseLong(senderMessage.get(message.getAuthor().getStringID()))),
                    Main.getInstance().getSkuddbot().getMessageByID(Long.parseLong(botMessage.get(message.getAuthor().getStringID())))
            )));

            senderMessage.remove(message.getAuthor().getStringID());
            botMessage.remove(message.getAuthor().getStringID());
        }
    }




}
