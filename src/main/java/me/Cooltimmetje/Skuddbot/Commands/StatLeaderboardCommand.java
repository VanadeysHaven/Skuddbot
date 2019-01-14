package me.Cooltimmetje.Skuddbot.Commands;

import me.Cooltimmetje.Skuddbot.Enums.EmojiEnum;
import me.Cooltimmetje.Skuddbot.Enums.UserStats.UserStats;
import me.Cooltimmetje.Skuddbot.Enums.UserStats.UserStatsCats;
import me.Cooltimmetje.Skuddbot.Main;
import me.Cooltimmetje.Skuddbot.Profiles.*;
import me.Cooltimmetje.Skuddbot.Utilities.MessagesUtils;
import me.Cooltimmetje.Skuddbot.Utilities.MiscUtils;
import org.apache.commons.lang3.StringUtils;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;

import java.util.*;

/**
 * This command generates a leaderboard of a given stat.
 *
 * @author Tim (Cooltimmetje)
 * @version v0.4.6-ALPHA
 * @since v0.4.6-ALPHA
 */
public class StatLeaderboardCommand {

    public static void run(IMessage message){
        long startTime = System.currentTimeMillis();
        String[] args = message.getContent().split(" ");
        IChannel channel = message.getChannel();
        IGuild guild = message.getGuild();
        Server server = ServerManager.getServer(guild);
        server.save(false);
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
                            sb1.append("`").append(userStats.toString().toLowerCase().replace("_", "-")).append("` | ");

                        }
                    }

                    stringBuilder.append(sb1.toString(), 0, sb1.toString().length() - 3).append("\n");
                }
            }

            MessagesUtils.sendPlain(stringBuilder.toString().trim(), channel, false);
            return;
        }
        try {
            stat = UserStats.valueOf(args[1].toUpperCase().replace("-", "_"));
        } catch (IllegalArgumentException e) {
            MessagesUtils.addReaction(message, "This stat does not exist. Type `!statlb -list` to list all the available stats.", EmojiEnum.X);
            return;
        }

        channel.setTypingStatus(true);
        LinkedHashMap<String,Integer> top = getTop(stat, guild);

        sb.append("**").append(stat.getDescription()).append(" leaderboard** | **").append(guild.getName()).append("**\n```\n");
        int longestName = 0;

        for (String string : top.keySet()) {
            String name = getName(string, guild);
            if(name.length() > longestName){
                longestName = name.length();
            }
        }

        int i = 0;
        int lastValue = -1;
        for(String string : top.keySet()){
            if(i<10){
                sb.append(" ");
            }
            String name = getName(string, guild);
            SkuddUser su = ProfileManager.getByString(string, guild.getStringID(), true);
            if(lastValue == top.get(string)){
                sb.append("   ");
            } else {
                i++;
                sb.append(i).append(". ");
            }
            sb.append(name).append(StringUtils.repeat(" ", longestName - name.length())).append(" | ").append(top.get(string)).append(" ").append(stat.getStatSuffix());
            if(!su.isLinked()){
                sb.append(" - ");
                if(su.getTwitchUsername() != null) {
                    sb.append("Twitch");
                } else {
                    sb.append("Discord");
                }
                sb.append(" (not linked)");
            }
            sb.append("\n");
            lastValue = top.get(string);
        }

        MessagesUtils.sendPlain(sb.append("```").append("\n").append("Generated in `").append(System.currentTimeMillis() - startTime).append("ms`").toString().trim(), channel, false);
    }

    private static LinkedHashMap<String,Integer> getTop(UserStats stat, IGuild guild){
        HashMap<String,Integer> stats = MySqlManager.getStats(stat, guild.getStringID());
        List<String> mapKeys = new ArrayList<>(stats.keySet());
        List<Integer> mapValues = new ArrayList<>(stats.values());
        mapKeys.sort(Collections.reverseOrder());
        mapValues.sort(Collections.reverseOrder());

        LinkedHashMap<String,Integer> sortedMap = new LinkedHashMap<>();

        for (int mapValue : mapValues) {
            int val = Integer.parseInt(String.valueOf(mapValue));
            Iterator<String> keyIt = mapKeys.iterator();

            while (keyIt.hasNext()) {
                String key = keyIt.next();
                int comp1 = stats.get(key);

                if (comp1 == val) {
                    keyIt.remove();
                    sortedMap.put(key, val);
                    break;
                }
            }

            if(sortedMap.size() == 10){
                return sortedMap;
            }
        }

        return sortedMap;
    }

    private static String getName(String str, IGuild guild){
        String name = "";
        if(MiscUtils.isLong(str)){
            name = Main.getInstance().getSkuddbot().getUserByID(Long.parseLong(str)).getDisplayName(guild);
        } else {
            name = str;
        }

        return name;
    }

}
