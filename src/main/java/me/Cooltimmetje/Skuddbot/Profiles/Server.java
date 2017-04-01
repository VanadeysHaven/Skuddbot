package me.Cooltimmetje.Skuddbot.Profiles;

import com.google.code.chatterbotapi.ChatterBotSession;
import lombok.Getter;
import lombok.Setter;
import me.Cooltimmetje.Skuddbot.Enums.ServerSettings;
import me.Cooltimmetje.Skuddbot.Main;
import me.Cooltimmetje.Skuddbot.Utilities.Constants;
import me.Cooltimmetje.Skuddbot.Utilities.Logger;
import me.Cooltimmetje.Skuddbot.Utilities.MessagesUtils;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * This class holds settings and profiles for servers, and manages them too.
 *
 * @author Tim (Cooltimmetje)
 * @version v0.4-ALPHA-DEV
 * @since v0.2-ALPHA
 */

@Getter
@Setter
public class Server {

    private String serverID;
    private int minXP;
    private int maxXP;
    private int minXpTwitch;
    private int maxXpTwitch;
    private int xpBase;
    private double xpMultiplier;
    private String twitchChannel;
    private String welcomeMessage;
    private String goodbyeMessage;
    private String welcomeGoodbyeChannel;
    private String adminRole;
    private String roleOnJoin;
    private boolean vrMode;
    private boolean serverInitialized;
    private ChatterBotSession session;

    public HashMap<String,SkuddUser> discordProfiles = new HashMap<>();
    public HashMap<String,SkuddUser> twitchProfiles = new HashMap<>();

    /**
     * Constructor for a new server, it puts all the settings to default and asks to initialize the server.
     *
     * @param serverID The ID of the server.
     */
    public Server(String serverID){
        this.serverID = serverID;
        this.minXP = Constants.MIN_GAIN;
        this.maxXP = Constants.MAX_GAIN;
        this.minXpTwitch = Constants.MIN_GAIN_TWITCH;
        this.maxXpTwitch = Constants.MAX_GAIN_TWITCH;
        this.xpBase = Constants.BASE_LEVEL;
        this.xpMultiplier = Constants.LEVEL_MULTIPLIER;
        this.vrMode = Constants.VR_MODE;
        this.serverInitialized = false;

        ServerManager.servers.put(serverID, this);

        MessagesUtils.sendPlain("**Welcome to the fun, welcome to the revolution, welcome to Skuddbot.** :eyes:\n\nI see that this server is not yet in my database, therefore I'll need to initialize this server before I can be used on this server!\n" +
                "In order to do so, please make sure I have a role that has the `ADMINISTRATOR` permission, then run `!initialize`. **NOTE:** This command requires you to also have the `ADMINISTRATOR` permission.",
                Main.getInstance().getSkuddbot().getGuildByID(serverID).getChannelByID(serverID), false);
        Logger.info("[CreateServer] " + Main.getInstance().getSkuddbot().getGuildByID(serverID).getName() + " (ID: " + serverID + ")");

    }

    /**
     * This is the constructor for existing servers loaded from the database.
     *
     * @param serverID The ID of the server.
     * @param minXP The MIN_XP setting.
     * @param maxXP The MAX_XP setting.
     * @param minXpTwitch The XP_TWITCH_MIN setting.
     * @param maxXpTwitch The XP_TWITCH_MAX setting.
     * @param xpBase The XP_BASE setting.
     * @param xpMultiplier The XP_MULTIPLIER setting.
     * @param twitchChannel The TWITCH_CHANNEL setting.
     * @param welcomeMessage The WELCOME_MESSAGE setting.
     * @param goodbyeMessage The GOODBYE_MESSAGE setting.
     * @param welcomeGoodbyeChannel The WELCOME_GOODBYE_CHAN setting.
     * @param adminRole The ADMIN_ROLE setting.
     * @param roleOnJoin The ROLE_ON_JOIN setting.
     * @param vrMode The VR_MODE setting.
     */
    public Server(String serverID, int minXP, int maxXP, int minXpTwitch, int maxXpTwitch, int xpBase, double xpMultiplier, String twitchChannel, String welcomeMessage, String goodbyeMessage, String welcomeGoodbyeChannel, String adminRole, String roleOnJoin, boolean vrMode) {
        this.serverID = serverID;
        this.minXP = minXP;
        this.maxXP = maxXP;
        this.minXpTwitch = minXpTwitch;
        this.maxXpTwitch = maxXpTwitch;
        this.xpBase = xpBase;
        this.xpMultiplier = xpMultiplier;
        this.twitchChannel = twitchChannel;
        this.welcomeMessage = welcomeMessage;
        this.goodbyeMessage = goodbyeMessage;
        this.adminRole = adminRole;
        this.roleOnJoin = roleOnJoin;
        this.welcomeGoodbyeChannel = welcomeGoodbyeChannel;
        this.vrMode = vrMode;
        this.serverInitialized = true;

        ServerManager.servers.put(serverID, this);
        if(twitchChannel != null){
            ServerManager.twitchServers.put(twitchChannel, this);
        }

        Logger.info("[LoadServer] " + Main.getInstance().getSkuddbot().getGuildByID(serverID).getName() + " (ID: " + serverID + ")");
    }

    /**
     * Saves the server settings, and all profiles to the database.
     */
    public void save(){
        if(serverInitialized){
            MySqlManager.saveServer(this);

            ArrayList<SkuddUser> saved = new ArrayList<>();
            for (String s : discordProfiles.keySet()) {
                discordProfiles.get(s).save();
                if(discordProfiles.get(s).getTwitchUsername() != null){
                    saved.add(discordProfiles.get(s));
                }
            }

            twitchProfiles.keySet().stream().filter(s -> !saved.contains(twitchProfiles.get(s))).forEach(s -> twitchProfiles.get(s).save());

            Logger.info("[SaveServer] " + Main.getInstance().getSkuddbot().getGuildByID(serverID).getName() + " (ID: " + serverID + ")");
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
            case XP_TWITCH_MIN:
                return getMinXpTwitch() + "";
            case XP_TWITCH_MAX:
                return getMaxXpTwitch() + "";
            case XP_BASE:
                return getXpBase() + "";
            case XP_MULTIPLIER:
                return getXpMultiplier() + "";
            case TWITCH_CHANNEL:
                return getTwitchChannel();
            case WELCOME_MESSAGE:
                return getWelcomeMessage();
            case GOODBYE_MESSAGE:
                return getGoodbyeMessage();
            case ADMIN_ROLE:
                return getAdminRole();
            case ROLE_ON_JOIN:
                return getRoleOnJoin();
            case WELCOME_GOODBYE_CHAN:
                return getWelcomeGoodbyeChannel();
            case VR_MODE:
                return isVrMode() + "";
        }
    }

    /**
     * Changes the setting to the specifed value.
     *
     * @param si The setting that should be changed.
     * @param value The value that should be set.
     * @return When the value was changed succesfully it returns 'null'. When a error occured it returns what went wrong.
     */
    @SuppressWarnings("all") //Fuck you IntelliJ
    public String setSetting(ServerSettings si, String value){
        double doubleValue = 0;
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
                if(!(intValue <= getMaxXP())){
                    return intValue + " isn't smaller than or equal to your XP_MAX value (Which is " + getMaxXP() + ").";
                }
                setMinXP(intValue);
                return null;
            case XP_MAX:
                if(!(intValue >= getMinXP())){
                    return intValue + " isn't greater than or equal to your XP_MIN value (Which is " + getMinXP() + ").";
                }
                setMaxXP(intValue);
                return null;
            case XP_TWITCH_MIN:
                if(!(intValue <= getMaxXpTwitch())){
                    return intValue + " isn't smaller than or equal to your XP_TWITCH_MAX value (Which is " + getMaxXpTwitch() + ").";
                }
                setMinXpTwitch(intValue);
                return null;
            case XP_TWITCH_MAX:
                if(!(intValue >= getMinXpTwitch())){
                    return intValue + " isn't greater than or equal to your XP_TWITCH_MIN value (Which is " + getMinXpTwitch() + ").";
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
            case GOODBYE_MESSAGE:
                setGoodbyeMessage(value);
                return null;
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
}
