package me.Cooltimmetje.Skuddbot.Commands;

import me.Cooltimmetje.Skuddbot.Enums.EmojiEnum;
import me.Cooltimmetje.Skuddbot.Enums.UserStats.UserStats;
import me.Cooltimmetje.Skuddbot.Enums.UserStats.UserStatsCats;
import me.Cooltimmetje.Skuddbot.Profiles.MySqlManager;
import me.Cooltimmetje.Skuddbot.Utilities.MessagesUtils;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;

import java.util.HashMap;

/**
 * This command generates a leaderboard of a given stat.
 *
 * @author Tim (Cooltimmetje)
 * @version v0.4.6-ALPHA
 * @since v0.4.6-ALPHA
 */
public class StatLeaderboardCommand {

    public static void run(IMessage message){
        String[] args = message.getContent().split(" ");
        IChannel channel = message.getChannel();
        IGuild guild = message.getGuild();
        UserStats stat = null;
        StringBuilder sb = new StringBuilder();

        if(args.length < 2){
            MessagesUtils.addReaction(message, "You must specify a stat to be shown. Type `!statlb -list` to list all the available stats.", EmojiEnum.X);
            return;
        }
        if(args[1].equalsIgnoreCase("-list")){
            StringBuilder stringBuilder = new StringBuilder();

            stringBuilder.append("Avaliable stats: \n\n");

            for(UserStatsCats category : UserStatsCats.values()){
                if(category.isShow()){
                    stringBuilder.append("**").append(category.getName().toUpperCase()).append(":** ");

                    StringBuilder sb1 = new StringBuilder();
                    for(UserStats userStats : UserStats.values()){
                        if(userStats.isShow() && userStats.getCategory() == category){
                            sb1.append("`").append(userStats.toString()).append("` | ");

                        }
                    }

                    stringBuilder.append(sb1.toString(), 0, sb1.toString().length() - 3).append("\n");
                }
            }

            MessagesUtils.sendPlain(stringBuilder.toString().trim(), channel, false);
        }
        try {
            stat = UserStats.valueOf(args[1].toUpperCase().replace("-", "_"));
        } catch (IllegalArgumentException e) {
            MessagesUtils.addReaction(message, "This stat does not exist. Type `!statlb -list` to list all the available stats.", EmojiEnum.X);
            return;
        }

        channel.setTypingStatus(true);
        HashMap<String,Integer> stats = MySqlManager.getStats(stat, guild.getStringID());

        sb.append("**").append(stat.getDescription()).append(" leaderboard ** | **").append(guild.getName()).append("**\n\n```");


        MessagesUtils.sendPlain(sb.toString().trim(), channel, false);
    }

}
