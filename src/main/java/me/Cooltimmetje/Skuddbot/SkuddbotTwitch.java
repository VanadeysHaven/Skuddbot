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
import java.util.HashMap;

import static me.Cooltimmetje.Skuddbot.Profiles.ServerManager.twitchServers;

/**
 * Everything Twitch happens here!
 *
 * @author Tim (Cooltimmetje)
 * @version v0.5-ALPHA-DEV
 * @since v0.1-ALPHA
 */
public class SkuddbotTwitch extends PircBot{

    private boolean terminated = false;
    private static HashMap<String,Long> cooldown = new HashMap<>();

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
    @SuppressWarnings("ConstantConditions")
    protected void onMessage(String channel, String sender, String login, String hostname, String message) {
        int gain = 0;
        if (twitchServers.containsKey(channel.replace("#", " ").trim())) {

            if (message.startsWith("!riot") || message.startsWith("(╯°□°）╯︵ ┻━┻")) {
                sendMessage(channel, ((ServerManager.getTwitch(channel.replace("#", " ").trim()).isVrMode() ? "! " : " ") + "(╯°□°）╯︵ ┻━┻").trim());
            } else if (message.startsWith("!xpban") && (sender.equalsIgnoreCase("cooltimmetje") || sender.equalsIgnoreCase("jaschmedia"))) {
                String[] args = message.split(" ");
                if (args.length > 1) {
                    if (!Constants.bannedUsers.contains(args[1].toLowerCase())) {
                        sendMessage(channel, ((ServerManager.getTwitch(channel.replace("#", " ").trim()).isVrMode() ? "! " : " ") + args[1] + " is now globally banned from gaining XP. #rekt").trim());
                        Constants.bannedUsers.add(args[1].toLowerCase());
                        MySqlManager.banUser(args[1].toLowerCase());
                    } else {
                        sendMessage(channel, ((ServerManager.getTwitch(channel.replace("#", " ").trim()).isVrMode() ? "! " : " ") + args[1] + " is already globally banned from gaining XP.").trim());
                    }
                }
            } else if (message.startsWith("!flip ")) {
                if (cooldown.containsKey(channel)) {
                    if ((System.currentTimeMillis() - cooldown.get(channel)) > 30000) {
                        sendMessage(channel, ((ServerManager.getTwitch(channel.replace("#", " ").trim()).isVrMode() ? "! " : " ") + "(╯°□°）╯︵ " + MiscUtils.flipText(message.trim().substring(6, message.length()).trim())).trim());
                        cooldown.put(channel, System.currentTimeMillis());
                    }
                } else {
                    sendMessage(channel, ((ServerManager.getTwitch(channel.replace("#", " ").trim()).isVrMode() ? "! " : " ") + "(╯°□°）╯︵ " + MiscUtils.flipText(message.trim().substring(6, message.length()).trim())).trim());
                    cooldown.put(channel, System.currentTimeMillis());
                }
            } else if (message.startsWith("!reverse ")) {
                reverseCommand(message, channel, sender);
            } else if (message.startsWith("!toggletracking")){
                if(cooldown.containsKey(sender)){
                    if((System.currentTimeMillis() - cooldown.get(sender)) > 30000) {
                        SkuddUser user = ProfileManager.getTwitch(sender, channel, true);
                        boolean currentlyEnabled = user.isTrackMe();
                        user.setTrackMe(!user.isTrackMe());

                        sendMessage(channel, sender + ", tracking has been " + (currentlyEnabled ? "disabled" : "enabled") + " for you." +
                                (user.isLinked() ? " | NOTE: Because your account is linked to Discord, tracking has also been " + (currentlyEnabled ? "disabled" : "enabled") + " on Discord." : ""));
                        cooldown.put(sender, System.currentTimeMillis());
                    }
                } else {
                    SkuddUser user = ProfileManager.getTwitch(sender, channel, true);
                    boolean currentlyEnabled = user.isTrackMe();
                    user.setTrackMe(!user.isTrackMe());

                    sendMessage(channel, sender + ", tracking has been " + (currentlyEnabled ? "disabled" : "enabled") + " for you." +
                            (user.isLinked() ? " | NOTE: Because your account is linked to Discord, tracking has also been " + (currentlyEnabled ? "disabled" : "enabled") + " on Discord." : ""));
                    cooldown.put(sender, System.currentTimeMillis());
                }
            } else {
                if (!Constants.bannedUsers.contains(sender)) {
                    SkuddUser user = ProfileManager.getTwitch(sender, channel, true);
                    if(user.isTrackMe()) {
                        Server server = ServerManager.getTwitch(channel.replace("#", " ").trim());
                        gain = MiscUtils.randomInt(server.getMinXpTwitch(), server.getMaxXpTwitch());
                        user.setXp(user.getXp() + gain);
                    }
                }
            }

            Server server = ServerManager.getTwitch(channel.replace("#", " ").trim());
            server.logMessage(sender, message, gain);

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

    private void reverseCommand(String message, String channel, String sender) {
        String[] args = message.split(" ");
        if(args.length > 1) {
            if(args[1].equalsIgnoreCase("-whitelist")) {
                if (sender.equalsIgnoreCase("cooltimmetje")) {
                    if (args.length > 3) {
                        if (args[2].equalsIgnoreCase("add")) {
                            if(!Constants.whitelistedCommands.contains(args[3].toLowerCase())){
                                Constants.whitelistedCommands.add(args[3].toLowerCase());
                                MySqlManager.addWhitelistCommand(args[3].toLowerCase());

                                sendMessage(channel, "\"" + args[3].toLowerCase() + "\" has been whitelisted.");
                                return;
                            }
                        } else if (args[2].equalsIgnoreCase("remove")) {
                            if(Constants.whitelistedCommands.contains(args[3].toLowerCase())){
                                Constants.whitelistedCommands.remove(args[3].toLowerCase());
                                MySqlManager.removeWhitelistedCommand(args[3].toLowerCase());

                                sendMessage(channel, "\"" + args[3].toLowerCase() + "\" has been un-whitelisted.");
                                return;
                            }
                        }
                    }
                }
            }
        }
        if (cooldown.containsKey(channel)) {
            if ((System.currentTimeMillis() - cooldown.get(channel)) > 30000) {
                sendMessage(channel, ((ServerManager.getTwitch(channel.replace("#", " ").trim()).isVrMode() ? "! " : " ") + MiscUtils.reverse(message.trim().substring(9, message.length()).trim(), true)).trim());
                cooldown.put(channel, System.currentTimeMillis());
            }
        } else {
            sendMessage(channel, ((ServerManager.getTwitch(channel.replace("#", " ").trim()).isVrMode() ? "! " : " ") + MiscUtils.reverse(message.trim().substring(9, message.length()).trim(), true)).trim());
            cooldown.put(channel, System.currentTimeMillis());
        }
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

    public void leaveChannels(){
        for(String string : ServerManager.twitchServers.keySet()){
            Main.getSkuddbotTwitch().partChannel("#" + string);
        }
    }

    public void send(String message, String channel){
        sendMessage("#" + channel, message);
    }

}