package me.Cooltimmetje.Skuddbot.Profiles;

import lombok.Getter;
import lombok.Setter;
import me.Cooltimmetje.Skuddbot.Commands.Custom.Command;
import me.Cooltimmetje.Skuddbot.Enums.EmojiEnum;
import me.Cooltimmetje.Skuddbot.Enums.ServerSettings.ServerSettings;
import me.Cooltimmetje.Skuddbot.Main;
import me.Cooltimmetje.Skuddbot.Minigames.Blackjack.BlackjackHandler;
import me.Cooltimmetje.Skuddbot.Minigames.Challenge.ChallengeHandler;
import me.Cooltimmetje.Skuddbot.Minigames.FreeForAll.FFAHandler;
import me.Cooltimmetje.Skuddbot.Utilities.Constants;
import me.Cooltimmetje.Skuddbot.Utilities.Logger;
import me.Cooltimmetje.Skuddbot.Utilities.MessagesUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;

import java.io.*;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

/**
 * This class holds settings and profiles for servers, and manages them too.
 *
 * @author Tim (Cooltimmetje)
 * @version v0.5-ALPHA
 * @since v0.2-ALPHA
 */

@Getter
@Setter
public class Server {

    JSONParser parser = new JSONParser();

    private String serverID;
    private int minXP;
    private int maxXP;
    private int minXpTwitch;
    private int maxXpTwitch;
    private int xpBase;
    private double xpMultiplier;
    private String twitchChannel;
    private String welcomeMessage;
    private String welcomeMsgAttach;
    private String goodbyeMessage;
    private String goodbyeMsgAttach;
    private String welcomeGoodbyeChannel;
    private String adminRole;
    private String roleOnJoin;
    private boolean vrMode;
    private boolean serverInitialized;
    private boolean streamLive;
    private boolean allowAnalytics;
    private boolean allowRewards;
    private String arenaName;

    private ChallengeHandler challengeHandler;
    private FFAHandler ffaHandler;
    private BlackjackHandler blackjackHandler;

    public HashMap<String,SkuddUser> discordProfiles = new HashMap<>();
    public HashMap<String,SkuddUser> twitchProfiles = new HashMap<>();
    public HashMap<Long,Long> lastSeen = new HashMap<>();
    private ArrayList<Command> commands = new ArrayList<>();

    /**
     * Constructor for a new server, it puts all the settings to default and asks to initialize the server.
     *
     * @param serverID The ID of the server.
     */
    public Server(String serverID){
        this.serverID = serverID;
        this.serverInitialized = false;

        ServerManager.servers.put(serverID, this);

        try {
            setSettings("{}");
        } catch (ParseException e) {
            e.printStackTrace();
        }

        IGuild guild = Main.getInstance().getSkuddbot().getGuildByID(Long.parseLong(serverID));

        IMessage message = MessagesUtils.sendPlain("**Welcome to the fun, welcome to the revolution, welcome to Skuddbot.** :eyes:\n\n" +
                        "I see that this server is not yet in my database, but I am taking care of that as we speak. I will add the :white_check_mark: reaction to this message when I'm ready.\n" +
                        "You can start configuring me by typing `!serversettings`.",
                Main.getInstance().getSkuddbot().getGuildByID(Long.parseLong(serverID)).getDefaultChannel(), false);
        Logger.info("[CreateServer] " + Main.getInstance().getSkuddbot().getGuildByID(Long.parseLong(serverID)).getName() + " (ID: " + serverID + ")");



        Logger.info(MessageFormat.format("Initializing {0} (ID: {1})",guild.getName(),guild.getStringID()));
        this.clearProfiles();
        this.serverInitialized = true;
        Logger.info(MessageFormat.format("Initialized {0} (ID: {1})",guild.getName(),guild.getStringID()));

        MessagesUtils.addReaction(message, null, EmojiEnum.WHITE_CHECK_MARK, false);

        this.challengeHandler = new ChallengeHandler(serverID);
        this.ffaHandler = new FFAHandler(serverID);
        this.blackjackHandler = new BlackjackHandler(serverID);
    }

    /**
     * This is the constructor for existing servers loaded from the database.
     *
     * @param serverID The ID of the server.
     * @param settings The JSON settings string.
     */
    public Server(String serverID, String settings) {
        this.serverID = serverID;
        try {
            setSettings(settings);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        this.serverInitialized = true;

        ServerManager.servers.put(serverID, this);
        if(twitchChannel != null){
            ServerManager.twitchServers.put(twitchChannel, this);
        }

        Logger.info("[LoadServer] " + Main.getInstance().getSkuddbot().getGuildByID(Long.parseLong(serverID)).getName() + " (ID: " + serverID + ")");

        this.challengeHandler = new ChallengeHandler(serverID);
        this.ffaHandler = new FFAHandler(serverID);
        this.blackjackHandler = new BlackjackHandler(serverID);

        MySqlManager.loadCommands(serverID);
    }

    /**
     * Saves the server settings, and all profiles to the database.
     */
    public void save(){
        if(serverInitialized) {
            MySqlManager.saveServer(this);

            ArrayList<SkuddUser> saved = new ArrayList<>();
            for (String s : discordProfiles.keySet()) {
                discordProfiles.get(s).save();
                if (discordProfiles.get(s).getTwitchUsername() != null) {
                    saved.add(discordProfiles.get(s));
                }
            }

            twitchProfiles.keySet().stream().filter(s -> !saved.contains(twitchProfiles.get(s))).forEach(s -> twitchProfiles.get(s).save());

            for(Command command : commands)
                command.save();

            Logger.info("[SaveServer] " + Main.getInstance().getSkuddbot().getGuildByID(Long.parseLong(serverID)).getName() + " (ID: " + serverID + ")");
        }
    }

    /**
     * Get the value for the specifed setting.
     *
     * @param si The setting that should be returned.
     * @return The value of the setting in String form.
     */
    public String getSetting(ServerSettings si){
        switch (si){
            default:
                return null;
            case XP_MIN:
                return getMinXP() + "";
            case XP_MAX:
                return getMaxXP() + "";
            case XP_MIN_TWITCH:
                return getMinXpTwitch() + "";
            case XP_MAX_TWITCH:
                return getMaxXpTwitch() + "";
            case XP_BASE:
                return getXpBase() + "";
            case XP_MULTIPLIER:
                return getXpMultiplier() + "";
            case TWITCH_CHANNEL:
                return getTwitchChannel();
            case WELCOME_MESSAGE:
                return getWelcomeMessage();
            case WELCOME_MSG_ATTACH:
                return getWelcomeMsgAttach();
            case GOODBYE_MESSAGE:
                return getGoodbyeMessage();
            case GOODBYE_MSG_ATTACH:
                return getGoodbyeMsgAttach();
            case ADMIN_ROLE:
                return getAdminRole();
            case ROLE_ON_JOIN:
                return getRoleOnJoin();
            case WELCOME_GOODBYE_CHAN:
                return getWelcomeGoodbyeChannel();
            case VR_MODE:
                return isVrMode() + "";
            case STREAM_LIVE:
                return isStreamLive() + "";
            case ALLOW_ANALYTICS:
                return isAllowAnalytics() + "";
            case ALLOW_REWARDS:
                return isAllowRewards() + "";
            case ARENA_NAME:
                return getArenaName();
        }
    }

    /**
     * Used to load all the settings into memory.
     *
     * @param settings The JSON setting string.
     * @throws ParseException Get's thrown when the JSON string can't be parsed.
     */
    public void setSettings(String settings) throws ParseException{
        JSONObject obj = (JSONObject) parser.parse(settings);
        for(ServerSettings setting : ServerSettings.values()){
            if(obj.containsKey(setting.getJsonReference())){
                String output = setSetting(setting, String.valueOf(obj.get(setting.getJsonReference())), true);
            } else {
                setSetting(setting, setting.getDefaultValue(), true);
            }
        }
    }

    /**
     * Changes the setting to the specifed value.
     *
     * @param si The setting that should be changed.
     * @param value The value that should be set.
     * @param ignoreMinMax This will be true upon loading, because otherwise we'll be running into issues and errors.
     * @return When the value was changed succesfully it returns 'null'. When a error occured it returns what went wrong.
     */
    @SuppressWarnings("all") //Fuck you IntelliJ
    public String setSetting(ServerSettings si, String value, boolean ignoreMinMax){
        double doubleValue = 0;
        long longValue = 0;
        boolean booleanValue = false;
        int intValue = 0;
        boolean intUsed = false;

        switch (si.getType().toLowerCase()){
            case "double":
                try {
                    doubleValue = Double.parseDouble(value);
                } catch (NumberFormatException e){
                    return "Value is not a Double.";
                }
                break;
            case "integer":
                try {
                    intValue = Integer.parseInt(value);
                    intUsed = true;
                } catch (NumberFormatException e){
                    return "Value is not a Integer.";
                }
                break;
            case "boolean":
                booleanValue = Boolean.parseBoolean(value);
                if (!booleanValue) {
                    if(!value.equalsIgnoreCase("false")){
                        return "Value is not a boolean.";
                    }
                }
                break;
            case "long":
                try {
                    longValue = Long.parseLong(value);
                } catch (NumberFormatException e){
                    return "Value is not a Long.";
                }
            default:
                if(value.equalsIgnoreCase("null")){
                    value = null;
                }
                break;
        }

        if(intUsed && (!(intValue > 0))){
            return si.toString() + " should always be greater than 0!";
        }

        switch (si){
            default:
                return null;
            case XP_MIN:
                if((!(intValue <= getMaxXP())) && !ignoreMinMax){
                    return intValue + " isn't smaller than or equal to your XP_MAX value (Which is " + getMaxXP() + ").";
                }
                setMinXP(intValue);
                return null;
            case XP_MAX:
                if((!(intValue >= getMinXP())) && !ignoreMinMax){
                    return intValue + " isn't greater than or equal to your XP_MIN value (Which is " + getMinXP() + ").";
                }
                setMaxXP(intValue);
                return null;
            case XP_MIN_TWITCH:
                if((!(intValue <= getMaxXpTwitch())) && !ignoreMinMax){
                    return intValue + " isn't smaller than or equal to your XP_MAX_TWITCH value (Which is " + getMaxXpTwitch() + ").";
                }
                setMinXpTwitch(intValue);
                return null;
            case XP_MAX_TWITCH:
                if((!(intValue >= getMinXpTwitch())) && !ignoreMinMax){
                    return intValue + " isn't greater than or equal to your XP_MIN_TWITCH value (Which is " + getMinXpTwitch() + ").";
                }
                setMaxXpTwitch(intValue);
                return null;
            case XP_BASE:
                setXpBase(intValue);
                return null;
            case XP_MULTIPLIER:
                if(!(doubleValue > 1.0)){
                    return "XP_MULTIPLIER should always be greater than 1!";
                }
                setXpMultiplier(doubleValue);
                return null;
            case TWITCH_CHANNEL:
                if(ServerManager.twitchServers.containsKey(value)){
                    return "This channel is already in use on a different server, if you think this is an error, please contact a Skuddbot admin.";
                }
                setTwitchChannel(value);
                return null;
            case WELCOME_MESSAGE:
                setWelcomeMessage(value);
                return null;
            case WELCOME_MSG_ATTACH:
                setWelcomeMsgAttach(value);
                return null;
            case GOODBYE_MESSAGE:
                setGoodbyeMessage(value);
                return null;
            case GOODBYE_MSG_ATTACH:
                setGoodbyeMsgAttach(value);
            case ADMIN_ROLE:
                setAdminRole(value);
                return null;
            case ROLE_ON_JOIN:
                setRoleOnJoin(value);
                return null;
            case WELCOME_GOODBYE_CHAN:
                setWelcomeGoodbyeChannel(value);
                return null;
            case VR_MODE:
                setVrMode(booleanValue);
                return null;
            case STREAM_LIVE:
                setStreamLive(false);
                return null;
            case ALLOW_ANALYTICS:
                setAllowAnalytics(booleanValue);
                return null;
            case ALLOW_REWARDS:
                setAllowRewards(booleanValue);
                return null;
            case ARENA_NAME:
                setArenaName(value);
                return null;
        }
    }

    /**
     * Get a profile by Discord ID.
     *
     * @param id The ID of the user that we want.
     * @return The profile of the user with the specified ID.
     */
    public SkuddUser getDiscord(String id){
        return discordProfiles.get(id);
    }

    /**
     * Adds a profile to the HashMap with the Discord ID as the key.
     *
     * @param user The user that should be added.
     */
    public void addDiscord(SkuddUser user){
        discordProfiles.put(user.getId(), user);
    }

    /**
     * Clear the profiles when we initialize the server.
     */
    public void clearProfiles() {
        if(!serverInitialized){
            discordProfiles.clear();
            twitchProfiles.clear();
        }
    }

    /**
     * Adds a profile to the HashMap with the Twitch username as the key.
     *
     * @param user The user that should be added.
     */
    public void addTwitch(SkuddUser user) {
        twitchProfiles.put(user.getTwitchUsername(), user);
    }

    /**
     * Get a profile by Twitch username.
     *
     * @param username The username of the profile we want.
     * @return The profile of the specified username.
     */
    public SkuddUser getTwitch(String username){
        return twitchProfiles.get(username);
    }

    /**
     * Remove a profile from the HashMap by Twitch username.
     *
     * @param twitchUsername The username of the profile that should be removed.
     */
    public void removeTwitch(String twitchUsername){
        twitchProfiles.remove(twitchUsername);
    }

    /**
     * Join the correct Twitch channels for the server (and leave if there are any).
     *
     * @param twitchChannel The channel that should be joined.
     */
    public void setTwitch(String twitchChannel){
        if(twitchChannel.equalsIgnoreCase("null")){
            twitchChannel = null;
        }

        if(this.twitchChannel != null) {
            ServerManager.twitchServers.remove(this.twitchChannel);
            Main.getSkuddbotTwitch().part(this.twitchChannel);
        }

        this.twitchChannel = twitchChannel;

        if(this.twitchChannel != null) {
            ServerManager.twitchServers.put(this.twitchChannel, this);
            Main.getSkuddbotTwitch().join(this.twitchChannel);
        }
    }

    /**
     * This puts all the settings in a JSON string to be saved in the database.
     *
     * @return The JSON string.
     */
    @SuppressWarnings("unchecked")
    public String jsonSettings() {
        JSONObject obj = new JSONObject();

        for(ServerSettings setting : ServerSettings.values()){
            if(getSetting(setting) != null) {
                if (!getSetting(setting).equals(setting.getDefaultValue())) {
                    obj.put(setting.getJsonReference(), getSetting(setting));
                }
            }
        }

        return obj.toString();
    }

    /**
     * Run analytics on the current chat log.
     *
     * @param channel The channel where the stream end command originated from, and where the analytics should be posted.
     */
    @SuppressWarnings("unchecked")
    public void runAnalytics(IChannel channel){
        setStreamLive(false);
        if(!allowAnalytics){
            return;
        }

        IMessage message = MessagesUtils.sendPlain(":hourglass_flowing_sand: Stream finished... *Compiling analytics...*", channel, false);
        String line;
        int quotesAdded = 0,riots = 0,totalXp = 0,messages = 0;
        ArrayList<String> users = new ArrayList<>();
        HashMap<String,Integer> xpGain = new HashMap<>();
        HashMap<String,Integer> messagesSent = new HashMap<>();
        HashMap<String,Integer> riotsStarted = new HashMap<>();
        HashMap<String,Integer> rewards = new HashMap<>();

        int currentWallLength = 0;
        String currentWallUser = "yermom";

        int longestWallLength = 0;
        String longestWallUser = "yermom";


        try (BufferedReader br = new BufferedReader(new FileReader("chat-logs/" + serverID + ".json"))){
            while ((line = br.readLine()) != null){
                JSONObject obj = (JSONObject) parser.parse(line);
                String sender = (String) obj.get("sender");
                String msg = (String) obj.get("message");
                int xp = Integer.parseInt(String.valueOf(obj.get("xp")));

                messages++;
                totalXp += xp;
                if(xpGain.containsKey(sender)){
                    xpGain.put(sender, xpGain.get(sender) + xp);
                } else {
                    xpGain.put(sender, xp);
                }
                if(messagesSent.containsKey(sender)){
                    messagesSent.put(sender, messagesSent.get(sender) + 1);
                } else {
                    messagesSent.put(sender, 1);
                }
                if (!users.contains(sender)) {
                    users.add(sender);
                }
                if(currentWallUser.equals(sender)){
                    currentWallLength++;
                } else {
                    if(longestWallLength < currentWallLength){
                        longestWallLength = currentWallLength;
                        longestWallUser = currentWallUser;
                    }

                    currentWallUser = sender;
                    currentWallLength = 1;
                }

                if(msg.split(" ")[0].equalsIgnoreCase("!riot")){
                    riots++;
                    if(riotsStarted.containsKey(sender)){
                        riotsStarted.put(sender, riotsStarted.get(sender) + 1);
                    } else {
                        riotsStarted.put(sender, 1);
                    }
                } else if (msg.toLowerCase().contains("--> succesfully added quote #")){
                    quotesAdded++;
                }
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        StringBuilder topXp = new StringBuilder();
        StringBuilder topMsg = new StringBuilder();
        StringBuilder topRiots = new StringBuilder();

        Logger.info("Compiling top 5's...");
        TreeMap<Integer,List<String>> messagesSentTop = new TreeMap<>();
        for(String s : messagesSent.keySet()){
            if(messagesSentTop.containsKey(messagesSent.get(s))){
                List<String> list = messagesSentTop.get(messagesSent.get(s));
                list.add(s);
                messagesSentTop.put(messagesSent.get(s), list);
            } else {
                List<String> list = new ArrayList<>();
                list.add(s);
                messagesSentTop.put(messagesSent.get(s), list);
            }
        }
        int msgAmount = 1;
        for (int i : messagesSentTop.descendingKeySet()){
            if(msgAmount == 6){
                break;
            }
            int current = msgAmount;
            for(String s : messagesSentTop.get(i)) {
                if(msgAmount == 6){
                    break;
                }
                if (!Constants.bannedUsers.contains(s)) {
                    int reward = ((6-current) * 200);
                    if(rewards.containsKey(s)){
                        rewards.put(s, rewards.get(s) + reward);
                    } else {
                        rewards.put(s, reward);
                    }
                    if(allowRewards) {
                        topMsg.append(current).append(". ").append(formatName(s)).append(" - ").append(i).append(" Messages *(+").append(reward).append(" XP)*\n");
                    } else {
                        topMsg.append(current).append(". ").append(formatName(s)).append(" - ").append(i).append(" Messages\n");
                    }
                    msgAmount++;
                }
            }
        }

        TreeMap<Integer,List<String>> xpGainTop = new TreeMap<>();
        for(String s : xpGain.keySet()){
            if(xpGainTop.containsKey(xpGain.get(s))){
                List<String> list = xpGainTop.get(xpGain.get(s));
                list.add(s);
                xpGainTop.put(xpGain.get(s), list);
            } else {
                List<String> list = new ArrayList<>();
                list.add(s);
                xpGainTop.put(xpGain.get(s), list);
            }
        }
        int xpAmount = 1;
        for (int i : xpGainTop.descendingKeySet()){
            if(xpAmount == 6){
                break;
            }
            int current = xpAmount;
            for(String s : xpGainTop.get(i)) {
                if(xpAmount == 6){
                    break;
                }
                if (!Constants.bannedUsers.contains(s)) {
                    int reward = ((6-current) * 200);
                    if(rewards.containsKey(s)){
                        rewards.put(s, rewards.get(s) + reward);
                    } else {
                        rewards.put(s, reward);
                    }
                    if(allowRewards){
                        topXp.append(current).append(". ").append(formatName(s)).append(" - ").append(i).append(" XP *(+").append(reward).append(" XP)*\n");
                    } else {
                        topXp.append(current).append(". ").append(formatName(s)).append(" - ").append(i).append(" XP\n");
                    }

                    xpAmount++;
                }
            }
        }

        TreeMap<Integer,List<String>> riotStartedTop = new TreeMap<>();
        for(String s : riotsStarted.keySet()){
            if(riotStartedTop.containsKey(riotsStarted.get(s))){
                List<String> list = riotStartedTop.get(riotsStarted.get(s));
                list.add(s);
                riotStartedTop.put(riotsStarted.get(s), list);
            } else {
                List<String> list = new ArrayList<>();
                list.add(s);
                riotStartedTop.put(riotsStarted.get(s), list);
            }
        }
        int riotAmount = 1;
        for (int i : riotStartedTop.descendingKeySet()){
            if(riotAmount == 6){
                break;
            }
            int current = riotAmount;
            for(String s : riotStartedTop.get(i)) {
                if(riotAmount == 6){
                    break;
                }
                if (!Constants.bannedUsers.contains(s)) {
                    int reward = ((6-current) * 200);
                    if(rewards.containsKey(s)){
                        rewards.put(s, rewards.get(s) + reward);
                    } else {
                        rewards.put(s, reward);
                    }
                    if(allowRewards){
                        topRiots.append(current).append(". ").append(formatName(s)).append(" - ").append(i).append(" riots *(+").append(reward).append(" XP)*\n");
                    } else {
                        topRiots.append(current).append(". ").append(formatName(s)).append(" - ").append(i).append(" riots\n");
                    }
                    riotAmount++;
                }
            }
        }

        double averageXP = (double)Math.round(((double)totalXp / (double)users.size()) * 100d) / 100d;

        if(rewards.containsKey(longestWallUser)){
            rewards.put(longestWallUser, rewards.get(longestWallUser) + 1000);
        } else {
            rewards.put(longestWallUser, 1000);
        }

        if(allowRewards) {
            for (String s : rewards.keySet()) {
                ProfileManager.getTwitch(s, getTwitchChannel(), true).setXp(ProfileManager.getTwitch(s, getTwitchChannel(), true).getXp() + rewards.get(s));
            }
        }

        try {
            assert message != null;
            message.delete();
        } catch (MissingPermissionsException | RateLimitException | DiscordException e) {
            e.printStackTrace();
        }
        MessagesUtils.sendPlain("**SKUDDBOT ANALYTICS** | This is what the chat did this stream:\n\n" +
                "The chat has started **" + riots + " riots!**\n" +
                "Things were said and **" + quotesAdded + " quotes were born**.\n" +
                "**" + users.size() + " users** have posted **" + messages + " messages** which gave them a total of **" + totalXp + " XP**. With a average of **" + averageXP + " XP per user**!\n" +
                "**" + formatName(longestWallUser) + "** has made the longest chat wall this stream with **" + longestWallLength + " messages**" + (allowRewards ? " *(+1000 xp)*" : "") + "\n\n" +
                "**TOP " + (msgAmount - 1) + " MESSAGES POSTED:**\n" + topMsg.toString() + "\n" +
                "**TOP " + (xpAmount - 1) + " XP GAIN:**\n" + topXp.toString() + "\n" +
                "**TOP " + (riotAmount - 1) + " RIOTS STARTED:**\n" + topRiots.toString(), channel, false);

        JSONObject obj = new JSONObject();
        long timestamp = System.currentTimeMillis();
        obj.put("timestamp", timestamp);
        obj.put("riots", riots);
        obj.put("quotes_added", quotesAdded);
        obj.put("unique_users", users.size());
        obj.put("messages", messages);
        obj.put("total_xp", totalXp);
        obj.put("average_xp", averageXP);
        obj.put("longest_wall_user", longestWallUser);
        obj.put("longest_wall_length", longestWallLength);
        obj.put("rewards_given", allowRewards);

        JSONArray msg = new JSONArray();
        for(String s : messagesSent.keySet()){
            JSONObject obj1 = new JSONObject();
            obj1.put("user", s);
            obj1.put("message_amount", messagesSent.get(s));
            msg.add(obj1);
        }
        JSONArray xp = new JSONArray();
        for(String s : xpGain.keySet()){
            JSONObject obj1 = new JSONObject();
            obj1.put("user", s);
            obj1.put("xp_amount", xpGain.get(s));
            xp.add(obj1);
        }
        JSONArray riot = new JSONArray();
        for(String s : riotsStarted.keySet()){
            JSONObject obj1 = new JSONObject();
            obj1.put("user", s);
            obj1.put("riots", riotsStarted.get(s));
            riot.add(obj1);
        }
        JSONArray rewardJSON = new JSONArray();
        for(String s : rewards.keySet()){
            JSONObject obj1 = new JSONObject();
            obj1.put("user", s);
            obj1.put("reward", riotsStarted.get(s));
            rewardJSON.add(obj1);
        }
        obj.put("message_user", msg);
        obj.put("xp_user", xp);
        obj.put("riot_user", riot);
        obj.put("rewards", rewardJSON);

        File directory = new File("analytic-log/" + serverID);
        if(!directory.exists()){
            directory.mkdirs();
        }
        try(FileWriter file = new FileWriter("analytic-log/" + serverID  + "/" + timestamp + ".json",true)){
            file.write(obj.toJSONString());
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

        File file = new File("chat-logs/" + serverID + ".json");
        file.delete();
    }

    /**
     * Format a Twitch username to a mention if applicable.
     *
     * @param name The Twitch username we want formatted.
     * @return The username or mention.
     */
    public String formatName(String name){
        String formattedName = (ProfileManager.getTwitch(name, getTwitchChannel(), true).isLinked() ? Main.getInstance().getSkuddbot().getUserByID(Long.parseLong(ProfileManager.getTwitch(name, getTwitchChannel(), false).getId())).mention() : name);
        return ProfileManager.getTwitch(name, getTwitchChannel(), true).isAnalyticsMention() ? formattedName : name;
    }

    /**
     * Logs a message to the log for analytics.
     *
     * @param sender The user that sent the message.
     * @param message The message that the user sent.
     * @param xpGain The amount of XP that the user gained.
     */
    public void logMessage(String sender, String message, int xpGain){
        if(allowAnalytics && streamLive && ProfileManager.getTwitchServer(sender, serverID).isTrackMe()){
            JSONObject obj = new JSONObject();
            obj.put("sender", sender);
            obj.put("message", message);
            obj.put("xp", xpGain);

            try(FileWriter file = new FileWriter("chat-logs/" + serverID  + ".json",true)){
                file.write(obj.toJSONString() + "\n");
                file.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Command getCommand(String invoker){
        for(Command command : commands)
            if(command.getInvoker().equalsIgnoreCase(invoker))
                return command;
        return null;
    }

    public void runCommand(String invoker, IMessage message){
        Command command = getCommand(invoker);
        if(command != null)
            command.run(message);
    }

    public boolean doesCommandExist(String invoker){
        return getCommand(invoker) != null;
    }

    public void addCommand(IMessage message) {
        String[] args = message.getContent().split(" ");
        String invoker = args[2].toLowerCase();
        StringBuilder sb = new StringBuilder();
        for(int i=3; i < args.length; i++) sb.append(args[i]).append(" ");
        String output = sb.toString().trim();

        if(args.length < 4) {
            MessagesUtils.addReaction(message, "Invalid usage. Usage: `!command add <invoker> <output...>`", EmojiEnum.X);
            return;
        }
        if(doesCommandExist(invoker)){
            MessagesUtils.addReaction(message, "This command already exists!", EmojiEnum.X);
            return;
        }

        Command command = new Command(serverID, invoker, output);
        commands.add(command);

        MessagesUtils.addReaction(message, "Added command `" + invoker + "` with output `" + output + "`.", EmojiEnum.WHITE_CHECK_MARK);
    }

    public void loadCommand(String invoker, String output, String metaData, String properties){
        Command command = new Command(serverID, invoker, output, metaData, properties);
        commands.add(command);
    }

    public void editCommand(IMessage message) {
        String[] args = message.getContent().split(" ");
        if(args.length < 4){
            MessagesUtils.addReaction(message, "Invalid usage! Usage: `!command edit <invoker> <newOutput...>`.", EmojiEnum.X);
            return;
        }
        if(args[2].equalsIgnoreCase("-invoker")){
            editInvoker(message);
            return;
        }

        Command command = getCommand(args[2]);
        if(command == null){
            MessagesUtils.addReaction(message, "This command does not exist!", EmojiEnum.X);
            return;
        }

        StringBuilder sb = new StringBuilder();
        for(int i=3; i < args.length; i++) sb.append(args[i]).append(" ");
        String newOutput = sb.toString().trim();
        command.setOutput(newOutput);

        command.update();
        command.save();


        MessagesUtils.addReaction(message, "Edited command `" + args[2] + "` to output `" + newOutput + "`.", EmojiEnum.WHITE_CHECK_MARK);
    }

    private void editInvoker(IMessage message){
        String[] args = message.getContent().split(" ");
        if(args.length < 5){
            MessagesUtils.addReaction(message, "Invalid usage! Usage: `!command edit -invoker <oldInvoker> <newInvoker>", EmojiEnum.X);
            return;
        }
        String oldInvoker = args[3];
        String newInvoker = args[4];
        if(oldInvoker.equalsIgnoreCase(newInvoker)){
            MessagesUtils.addReaction(message, "The old and the new invoker are the same!", EmojiEnum.X);
            return;
        }
        Command command = getCommand(oldInvoker);
        if(command == null){
            MessagesUtils.addReaction(message, "This command doesn't exist!", EmojiEnum.X);
            return;
        }

        command.update();
        command.setInvoker(newInvoker);
        MessagesUtils.addReaction(message, "Updated invoker `" + oldInvoker + "` to `" + newInvoker + "`.", EmojiEnum.WHITE_CHECK_MARK);
    }

    public void removeCommand(IMessage message){
        String[] args = message.getContent().split(" ");
        if(args.length < 3){
            MessagesUtils.addReaction(message, "Invalid usage! Usage: `!comm" +
                    "and remove <invoker>`", EmojiEnum.X);
            return;
        }
        String invoker = args[2];
        Command command = getCommand(invoker);
        if(command == null){
            MessagesUtils.addReaction(message, "This command does not exist!", EmojiEnum.X);
            return;
        }

        commands.remove(command);
        MySqlManager.removeCommand(serverID, invoker);

        MessagesUtils.addReaction(message, "Removed command with invoker `" + invoker + "`.", EmojiEnum.WHITE_CHECK_MARK);
        Logger.info("Removed command with invoker " + invoker);
    }
}
