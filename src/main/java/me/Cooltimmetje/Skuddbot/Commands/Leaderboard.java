package me.Cooltimmetje.Skuddbot.Commands;

import me.Cooltimmetje.Skuddbot.Main;
import me.Cooltimmetje.Skuddbot.Profiles.MySqlManager;
import me.Cooltimmetje.Skuddbot.Profiles.ServerManager;
import me.Cooltimmetje.Skuddbot.Profiles.SkuddUser;
import me.Cooltimmetje.Skuddbot.Utilities.MessagesUtils;
import sx.blah.discord.handle.obj.IMessage;

import java.util.HashMap;
import java.util.TreeMap;

/**
 * Created by Tim on 8/21/2016.
 */
public class Leaderboard {

    public static void run(IMessage message){
        message.getChannel().toggleTypingStatus();
        long startTime = System.currentTimeMillis();
        ServerManager.getServer(message.getGuild().getID()).save();
        HashMap<Integer,SkuddUser> discord = MySqlManager.getTopDiscord(message.getGuild().getID());
        HashMap<Integer,SkuddUser> twitch = MySqlManager.getTopTwitch(message.getGuild().getID());

        TreeMap<Integer,SkuddUser> top = new TreeMap<>(); //topkek
        for(int i : discord.keySet()){
            top.put(i,discord.get(i));
        }
        for(int i : twitch.keySet()){
            top.put(i,twitch.get(i));
        }

        StringBuilder sb = new StringBuilder();
        int i = 0;

        int lengthName = 0;

        for(int i2 : top.descendingKeySet()){
            SkuddUser user = top.get(i2);
            String name = user.getId() == null ? user.getTwitchUsername() : (Main.getInstance().getSkuddbot().getUserByID(user.getId()) == null ? user.getName() :
                    (Main.getInstance().getSkuddbot().getUserByID(user.getId()).getNicknameForGuild(message.getGuild()).isPresent() ?
                            Main.getInstance().getSkuddbot().getUserByID(user.getId()).getNicknameForGuild(message.getGuild()).get() : Main.getInstance().getSkuddbot().getUserByID(user.getId()).getName()));

            if(user.getTwitchUsername() != null){
                if(user.getTwitchUsername().equals("jaschmedia")){
                    name = "JuiceMedia";
                }
            }

            if(name.length() > lengthName){
                lengthName = name.length();
            }

            if(i<9){
                i++;
            } else {
                break;
            }
        }

        int i3 = 0;
        for(int i2 : top.descendingKeySet()){
            if(i3<9){
                sb.append(" ");
            }
            sb.append(i3+1).append(". ").append(top.get(i2).calcXpLB(lengthName)).append("\n");
            if(i3<9){
                i3++;
            } else {
                break;
            }
        }

        MessagesUtils.sendPlain("**XP Leaderboard** | **" + message.getGuild().getName() + "**\n\n```\n" + sb.toString() + "```\n**PRO-TIP:** You might have more XP if you are marked as \"not linked\", type `!twitch` to get started with linking your accounts! It's really easy to do, promise, and you'll get a nice tasty 1000xp free! Woo!\nGenerated in `" + (System.currentTimeMillis() - startTime) + " ms`", message.getChannel());
}

}
