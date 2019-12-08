package me.Cooltimmetje.Skuddbot.Commands;

import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.MessageChannel;
import discord4j.core.object.util.Snowflake;
import me.Cooltimmetje.Skuddbot.Enums.EmojiEnum;
import me.Cooltimmetje.Skuddbot.Enums.UserStats.UserStats;
import me.Cooltimmetje.Skuddbot.Enums.UserStats.UserStatsCats;
import me.Cooltimmetje.Skuddbot.Main;
import me.Cooltimmetje.Skuddbot.Profiles.*;
import me.Cooltimmetje.Skuddbot.Utilities.MessagesUtils;
import me.Cooltimmetje.Skuddbot.Utilities.MiscUtils;
import me.Cooltimmetje.Skuddbot.Utilities.TableUtilities.TableArrayGenerator;
import me.Cooltimmetje.Skuddbot.Utilities.TableUtilities.TableDrawer;
import me.Cooltimmetje.Skuddbot.Utilities.TableUtilities.TableRow;

import java.util.*;

/**
 * This command generates a leaderboard of a given stat.
 *
 * @author Tim (Cooltimmetje)
 * @version v0.5.1-ALPHA
 * @since v0.4.6-ALPHA
 */
public class StatLeaderboardCommand {

    public static void run(Message message){
        long startTime = System.currentTimeMillis();
        String[] args = message.getContent().get().split(" ");
        MessageChannel channel = message.getChannel().block();
        Guild guild = message.getGuild().block();
        Server server = ServerManager.getServer(guild);
        server.save();
        UserStats stat = null;
        StringBuilder sb = new StringBuilder();

        if(args.length < 2){
            MessagesUtils.addReaction(message, "You must specify a stat to be shown. Type `!statlb -list` to list all the available stats.", EmojiEnum.X, false);
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
                        if(userStats.isShow() && userStats.getCategory() == category && userStats.isHasLeaderboard()){
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
            MessagesUtils.addReaction(message, "This stat does not exist. Type `!statlb -list` to list all the available stats.", EmojiEnum.X, false);
            return;
        }

        if(!stat.isHasLeaderboard()){
            MessagesUtils.addReaction(message, "This stat does not have a leaderboard.", EmojiEnum.X);
            return;
        }

        channel.type().block();
        LinkedHashMap<String,Integer> top = getTop(stat, guild);

        sb.append("**").append(stat.getCategory().getName()).append(": ").append(stat.getDescription()).append(" leaderboard** | **").append(guild.getName()).append("**\n```\n");

        int i = 0;
        int lastValue = -1;
        TableArrayGenerator tag = new TableArrayGenerator(new TableRow("Pos", "Name", MiscUtils.capitalize(stat.getStatSuffix()), "Account Type"));
        for(String string : top.keySet()){
            TableRow tr = new TableRow();
            String name = getName(string, guild);
            SkuddUser su = ProfileManager.getByString(string, guild.getId().asString(), true);
            if(lastValue == top.get(string)){
                tr.addString(" ");
            } else {
                i++;
                tr.addString(i+"");
            }
            tr.addString(name);
            tr.addString(su.getStat(stat));
            tr.addString(su.getAccountType().getFullName());
            tag.addRow(tr);

            lastValue = top.get(string);
        }

        sb.append(new TableDrawer(tag).drawTable());
        MessagesUtils.sendPlain(sb.append("```").append("\n").append("Generated in `").append(System.currentTimeMillis() - startTime).append("ms`").toString().trim(), channel, false);
    }

    private static LinkedHashMap<String,Integer> getTop(UserStats stat, Guild guild){
        HashMap<String,Integer> stats = MySqlManager.getStats(stat, guild.getId().asString());
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

    private static String getName(String str, Guild guild){
        String name = "";
        if(MiscUtils.isLong(str)){
            name = Main.getInstance().getSkuddbot().getUserById(Snowflake.of(str)).block().asMember(guild.getId()).block().getDisplayName();
        } else {
            name = str;
        }

        return name;
    }

}
