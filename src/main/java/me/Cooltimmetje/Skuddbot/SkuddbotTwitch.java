package me.Cooltimmetje.Skuddbot;

import me.Cooltimmetje.Skuddbot.Commands.TwitchLinkCommand;
import me.Cooltimmetje.Skuddbot.Profiles.*;
import me.Cooltimmetje.Skuddbot.Utilities.Constants;
import me.Cooltimmetje.Skuddbot.Utilities.Logger;
import me.Cooltimmetje.Skuddbot.Utilities.MiscUtils;
import org.jibble.pircbot.IrcException;
import org.jibble.pircbot.PircBot;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;

import static me.Cooltimmetje.Skuddbot.Profiles.ServerManager.twitchServers;

/**
 * Created by Tim on 8/17/2016.
 */
public class SkuddbotTwitch extends PircBot{

    private boolean terminated = false;
    public static ArrayList<String> bannedUsers = new ArrayList<>();
    public static HashMap<String,Long> cooldown = new HashMap<>();

    public SkuddbotTwitch() {
        this.setName(Constants.twitchBot);
        this.setLogin(Constants.twitchBot);
    }

    @Override
    protected void onConnect(){
        Logger.info("Connected to Twitch, Joining channels...");

        joinChannel("#" + Constants.twitchBot);
    }

    @Override
    protected void onDisconnect(){
        if(!terminated){
            try {
                reconnect();
            } catch (IOException | IrcException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onJoin(String channel, String sender, String login, String hostname){
        Logger.info("Joined channel: " + channel);
    }


    @Override
    protected void onPart(String channel, String sender, String login, String hostname){
        Logger.info("Left channel: " + channel);
    }

    @Override
    protected void onMessage(String channel, String sender, String login, String hostname, String message) {
        int gain = 0;
        if (twitchServers.containsKey(channel.replace("#", " ").trim())) {

            if (message.startsWith("!riot") || message.startsWith("(╯°□°）╯︵ ┻━┻")) {
                sendMessage(channel, ((ServerManager.getTwitch(channel.replace("#", " ").trim()).isVrMode() ? "! " : " ") + "(╯°□°）╯︵ ┻━┻").trim());
            } else if (message.startsWith("!xpban") && (sender.equalsIgnoreCase("cooltimmetje") || sender.equalsIgnoreCase("jaschmedia"))) {
                String[] args = message.split(" ");
                if (args.length > 1) {
                    if (!bannedUsers.contains(args[1].toLowerCase())) {
                        sendMessage(channel, ((ServerManager.getTwitch(channel.replace("#", " ").trim()).isVrMode() ? "! " : " ") + args[1] + " is now globally banned from gaining XP. #rekt").trim());
                        bannedUsers.add(args[1].toLowerCase());
                        MySqlManager.banUser(args[1].toLowerCase());
                    } else {
                        sendMessage(channel, ((ServerManager.getTwitch(channel.replace("#", " ").trim()).isVrMode() ? "! " : " ") + args[1] + " is already globally banned from gaining XP.").trim());
                    }
                }
            } else if (message.startsWith("!flip")) {
                if (cooldown.containsKey(channel)) {
                    if ((System.currentTimeMillis() - cooldown.get(channel)) > 30000) {
                        sendMessage(channel, ((ServerManager.getTwitch(channel.replace("#", " ").trim()).isVrMode() ? "! " : " ") + "(╯°□°）╯︵ " + MiscUtils.flipText(message.trim().substring(6, message.length()).trim())).trim());
                        cooldown.put(channel, System.currentTimeMillis());
                    }
                } else {
                    sendMessage(channel, ((ServerManager.getTwitch(channel.replace("#", " ").trim()).isVrMode() ? "! " : " ") + "(╯°□°）╯︵ " + MiscUtils.flipText(message.trim().substring(6, message.length()).trim())).trim());
                    cooldown.put(channel, System.currentTimeMillis());
                }
            } else if(message.startsWith("!reverse")) {
                if(cooldown.containsKey(channel)){
                    if (cooldown.containsKey(channel)) {
                        if ((System.currentTimeMillis() - cooldown.get(channel)) > 30000) {
                            sendMessage(channel, ((ServerManager.getTwitch(channel.replace("#", " ").trim()).isVrMode() ? "! " : " ") + MiscUtils.reverse(message.trim().substring(9, message.length()).trim())).trim());
                            cooldown.put(channel, System.currentTimeMillis());
                        }
                    } else {
                        sendMessage(channel, ((ServerManager.getTwitch(channel.replace("#", " ").trim()).isVrMode() ? "! " : " ") + MiscUtils.reverse(message.trim().substring(9, message.length()).trim())).trim());
                        cooldown.put(channel, System.currentTimeMillis());
                    }
                }
            } else {
                if (!bannedUsers.contains(sender)) {
                    SkuddUser user = ProfileManager.getTwitch(sender, channel, true);
                    Server server = ServerManager.getTwitch(channel.replace("#", " ").trim());
                    gain = MiscUtils.randomInt(server.getMinXpTwitch(), server.getMaxXpTwitch());
                    user.setXp(user.getXp() + gain);
                }
            }

        } else if (channel.equals("#" + Constants.twitchBot)) {
            if (message.startsWith("!verify")) {
                String[] args = message.split(" ");
                if (args.length >= 2) {
                    SkuddUser user = Constants.verifyCodes.get(args[1].toUpperCase());
                    if (user != null) {
                        Logger.info("found user");
                        Constants.verifyCodes.remove(args[1].toUpperCase());
                        user.setTwitchVerify(null);
                        TwitchLinkCommand.sendFollowUp(user, ProfileManager.getTwitchServer(sender, user.getServerID()));
                    } else {
                        sendMessage("#" + Constants.twitchBot, sender + ", This Twitch Verification Code is invalid, please double check your code.");
                    }
                } else {
                    sendMessage("#" + Constants.twitchBot, sender + ", To activate SkuddSync you'll need to enter a Twitch Verification Code, you can obtain one by typing '!twitch' in any channel on the Discord Server you want to link to!");
                }
            }
        }

        Logger.info(MessageFormat.format("Twitch Message: {0} - {1}: {2} - XP: +{3}", channel, sender, message, gain));
    }


    public void terminate(){
        terminated = true;
        disconnect();
    }

    public void join(String channel){
        Main.getSkuddbotTwitch().joinChannel("#" + channel);
    }

    public void part(String channel){
        Main.getSkuddbotTwitch().partChannel("#" + channel);
    }

    public void joinChannels(){
        for(String string : ServerManager.twitchServers.keySet()){
            Main.getSkuddbotTwitch().joinChannel("#" + string);
        }
    }

    public void send(String message, String channel){
        sendMessage("#" + channel, message);
    }


}