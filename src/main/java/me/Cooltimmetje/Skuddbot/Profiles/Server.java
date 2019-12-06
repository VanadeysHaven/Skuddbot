package me.Cooltimmetje.Skuddbot.Profiles;

import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Message;
import discord4j.core.object.util.Snowflake;
import lombok.Getter;
import lombok.Setter;
import me.Cooltimmetje.Skuddbot.Commands.Custom.Command;
import me.Cooltimmetje.Skuddbot.Enums.EmojiEnum;
import me.Cooltimmetje.Skuddbot.Enums.ServerSettings.ServerSettings;
import me.Cooltimmetje.Skuddbot.Main;
import me.Cooltimmetje.Skuddbot.Minigames.Blackjack.BlackjackHandler;
import me.Cooltimmetje.Skuddbot.Minigames.Challenge.ChallengeHandler;
import me.Cooltimmetje.Skuddbot.Minigames.FreeForAll.FFAHandler;
import me.Cooltimmetje.Skuddbot.Utilities.Logger;
import me.Cooltimmetje.Skuddbot.Utilities.MessagesUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;

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

        Guild guild = Main.getInstance().getSkuddbot().getGuildById(Snowflake.of(serverID)).block();

        Message message = MessagesUtils.sendPlain("**Welcome to the fun, welcome to the revolution, welcome to Skuddbot.** :eyes:\n\n" +
                        "I see that this server is not yet in my database, but I am taking care of that as we speak. I will add the :white_check_mark: reaction to this message when I'm ready.\n" +
                        "You can start configuring me by typing `!serversettings`.",
                guild.getSystemChannel().block(), false);
        Logger.info("[CreateServer] " + guild.getName() + " (ID: " + serverID + ")");



        Logger.info(MessageFormat.format("Initializing {0} (ID: {1})", guild.getName(), guild.getId().asString()));
        this.clearProfiles();
        this.serverInitialized = true;
        Logger.info(MessageFormat.format("Initialized {0} (ID: {1})", guild.getName(), guild.getId().asString()));

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

        Logger.info("[LoadServer] " + getGuild().getName() + " (ID: " + serverID + ")");

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

            Logger.info("[SaveServer] " + getGuild().getName() + " (ID: " + serverID + ")");
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

    public Command getCommand(String invoker){
        for(Command command : commands)
            if(command.getInvoker().equalsIgnoreCase(invoker))
                return command;
        return null;
    }

    public void runCommand(String invoker, Message message){
        Command command = getCommand(invoker);
        if(command != null)
            command.run(message);
    }

    public boolean doesCommandExist(String invoker){
        return getCommand(invoker) != null;
    }

    public void addCommand(Message message) {
        String[] args = message.getContent().get().split(" ");
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

        Command command = new Command(serverID, invoker, output, message.getAuthor().get().getId().asLong());
        commands.add(command);

        MessagesUtils.addReaction(message, "Added command `" + invoker + "` with output `" + output + "`.", EmojiEnum.WHITE_CHECK_MARK);
    }

    public void loadCommand(String invoker, String output, String metaData, String properties){
        Command command = new Command(serverID, invoker, output, metaData, properties);
        commands.add(command);
    }

    public void editCommand(Message message) {
        String[] args = message.getContent().get().split(" ");
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

        command.update(message.getAuthor().get().getId().asLong());
        command.save();


        MessagesUtils.addReaction(message, "Edited command `" + args[2] + "` to output `" + newOutput + "`.", EmojiEnum.WHITE_CHECK_MARK);
    }

    private void editInvoker(Message message){
        String[] args = message.getContent().get().split(" ");
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

        command.update(message.getAuthor().get().getId().asLong());
        command.setInvoker(newInvoker);
        MessagesUtils.addReaction(message, "Updated invoker `" + oldInvoker + "` to `" + newInvoker + "`.", EmojiEnum.WHITE_CHECK_MARK);
    }

    public void removeCommand(Message message){
        String[] args = message.getContent().get().split(" ");
        if(args.length < 3){
            MessagesUtils.addReaction(message, "Invalid usage! Usage: `!command remove <invoker>`", EmojiEnum.X);
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

    private Guild getGuild(){
        return Main.getInstance().getSkuddbot().getGuildById(Snowflake.of(serverID)).block();
    }
}
