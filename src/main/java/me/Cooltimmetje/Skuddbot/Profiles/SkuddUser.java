package me.Cooltimmetje.Skuddbot.Profiles;

import discord4j.core.object.entity.Message;
import lombok.Getter;
import lombok.Setter;
import me.Cooltimmetje.Skuddbot.Enums.AccountType;
import me.Cooltimmetje.Skuddbot.Enums.EmojiEnum;
import me.Cooltimmetje.Skuddbot.Enums.UserSettings;
import me.Cooltimmetje.Skuddbot.Enums.UserStats.UserStats;
import me.Cooltimmetje.Skuddbot.Main;
import me.Cooltimmetje.Skuddbot.Minigames.TeamDeathmatch.Members.TeamMember;
import me.Cooltimmetje.Skuddbot.Minigames.TeamDeathmatch.Team;
import me.Cooltimmetje.Skuddbot.Utilities.Constants;
import me.Cooltimmetje.Skuddbot.Utilities.Logger;
import me.Cooltimmetje.Skuddbot.Utilities.MessagesUtils;
import me.Cooltimmetje.Skuddbot.Utilities.MiscUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Holds user data. Doesn't need much explanation imo...
 *
 * @author Tim (Cooltimmetje)
 * @version v0.5.1-ALPHA
 * @since v0.1-ALPHA
 */
@Getter
@Setter
public class SkuddUser {

    JSONParser parser = new JSONParser();

    private String id;
    private String serverID;
    private String name;
    private int xp;
    private int level;
    private String twitchUsername;
    private String twitchVerify;
    private IMessage verifyMessage;
    private boolean inactive;

    //---- USER SETTINGS ----
    private int levelUpNotify;
    private boolean trackMe;
    private boolean analyticsMention;
    private boolean statsPrivate;
    private boolean mentionMe;
    private boolean minigameReminders;

    //---- USER STATS ----
    private int xpStreak;
    private int msgStreak;
    private int wallStreak;
    private int challengeWins;
    private int challengeLosses;
    private int challengeStreak;
    private int challengeLongestStreak;
    private int ffaWins;
    private int ffaLosses;
    private int ffaMostWin;
    private int ffaKills;
    private int blackjackWins;
    private int blackjackPushes;
    private int blackjackTwentyOnes;
    private int blackjackLosses;
    private int teamDeathmatchWins;
    private int teamDeathmatchLosses;
    private int teamDeathmatchSaves;
    private int teamDeathmatchMostWin;
    private int teamDeathmatchAllSurvived;
    private int teamDeathmatchKills;
    private HashMap<String,Integer> teamDeathmatchFavTeammate;

    public SkuddUser(String id, String serverID, String twitchUsername){
        this.id = id;
        this.name = (id != null ? Main.getInstance().getSkuddbot().getUserByID(Long.parseLong(id)).getName() : null);
        this.serverID = serverID;
        this.xp = 0;
        this.level = 1;
        this.twitchUsername = twitchUsername;
        this.inactive = false;

        Constants.PROFILES_IN_MEMORY++;
        IGuild guild = Main.getInstance().getSkuddbot().getGuildByID(Long.parseLong(serverID));
        boolean isTwitch = id == null;
        Logger.info(MessageFormat.format("[ProfileCreate][{0}] User: {1} | Server: {2} (ID: {3}) - Profiles in memory: {4}", isTwitch ? "Twitch" : "Discord", isTwitch ? twitchUsername : guild.getUserByID(Long.parseLong(id)).getName() + " (ID: " + id + ")", guild.getName(), guild.getStringID(), Constants.PROFILES_IN_MEMORY));

        try {
            setSettings("{}");
            setStats("{}");
        } catch (ParseException e) {
            e.printStackTrace();
        } finally {
            try{
                setRoles();
            } catch (IndexOutOfBoundsException e){
                Logger.info("Something happened... Something bad...");
            }
        }
    }

    public SkuddUser(String id, String serverID, String name, int xp, String twitchUsername, String settings, String stats) {
        this.id = id;
        this.name = name;
        this.serverID = serverID;
        this.xp = xp;
        this.twitchUsername = twitchUsername;
        this.inactive = false;

        try {
            setSettings(settings);
            setStats(stats);
        } catch (ParseException e) {
            e.printStackTrace();
        } finally {
            if(id != null) {
                calcXP(false, null);
            }

            Constants.PROFILES_IN_MEMORY++;
            IGuild guild = Main.getInstance().getSkuddbot().getGuildByID(Long.parseLong(serverID));
            boolean isTwitch = id == null;
            Logger.info(MessageFormat.format("[ProfileLoad][{0}] User: {1} | Server: {2} (ID: {3}) - Profiles in memory: {4}", isTwitch ? "Twitch" : "Discord", isTwitch ? twitchUsername : (guild.getUserByID(Long.parseLong(id)) == null ? name : guild.getUserByID(Long.parseLong(id)).getName()) + " (ID: " + id + ")", guild.getName(), guild.getStringID(), Constants.PROFILES_IN_MEMORY));

            try{
                setRoles();
            } catch (IndexOutOfBoundsException e){
                Logger.info("Something happened... Something bad...");
            }
        }
    }

    public int[] calcXP(boolean showLevelUp, Message message){
        if(showLevelUp && message == null) throw new IllegalArgumentException("We need a message to find the channel where we should post the level up message.");
        Server server = ServerManager.getServer(getServerID());
        int exp,level,needed;
        exp = getXp();
        level = 1;
        needed = server.getXpBase();

        while (exp >= needed) {
            exp = exp - needed;
            level++;
            needed = (int) (server.getXpBase() * Math.pow(server.getXpMultiplier(), level - 1));
        }

        if(!showLevelUp){
            setLevel(level);
        } else {
            if(level > getLevel()){
                int levels = level - getLevel();
                setLevel(level);

                if(getLevelUpNotify() == 0){
                    MessagesUtils.addReaction(message, MessageFormat.format("{0}, you leveled up! You are now **level {1}**! {2}", message.getAuthor().mention(),getLevel(), levels > 1 ? "(You leveled up **" + levels + " times**)" : " "), EmojiEnum.ARROW_UP, false);
                } else if (getLevelUpNotify() == 1){
                    MessagesUtils.sendPM(message.getAuthor(), MessageFormat.format(EmojiEnum.ARROW_UP + " You leveled up in **{0}**! You are now **level {1}**! {2}", message.getGuild().getName(),getLevel(), levels > 1 ? "(You leveled up **" + levels + " times**)" : " "));
                }
            }
        }

        return new int[]{exp, getXp(), needed, level};
    }

    public void save(){
        boolean isTwitch = id == null;
        IGuild guild = Main.getInstance().getSkuddbot().getGuildByID(Long.parseLong(serverID));
        Logger.info(MessageFormat.format("[ProfileSave][" + (isTwitch ? "Twitch" : "Discord") + "] User: {0} | Server: {1} (ID: {2})", isTwitch ? twitchUsername : (guild.getUserByID(Long.parseLong(id)) == null ? name : guild.getUserByID(Long.parseLong(id)).getName()) + " (ID: " + id + ")", guild.getName(), guild.getStringID()));

        if(isTwitch){
            MySqlManager.saveTwitch(this);
        } else {
            MySqlManager.saveProfile(this);
        }
    }

    public void setRoles(){
        if(id != null && twitchUsername != null && Main.getInstance().getSkuddbot().getUserByID(Long.parseLong(serverID)) != null){
            IUser user = Main.getInstance().getSkuddbot().getUserByID(Long.parseLong(serverID));
            IGuild guild = Main.getInstance().getSkuddbot().getGuildByID(Long.parseLong(serverID));
            List<IRole> roleList = user.getRolesForGuild(guild);

            roleList.add(guild.getRolesByName("Linked").get(0));
            IRole[] roles = roleList.toArray(new IRole[roleList.size()]);

            try {
                guild.editUserRoles(user, roles);
            } catch (MissingPermissionsException | RateLimitException | DiscordException e) {
//                Logger.warn("Couldn't add role. See Stacktrace", e);
            }
        }
    }

    public void unload() {
        save();

        if(getId() != null) {
            ServerManager.getServer(getServerID()).discordProfiles.remove(getId());
        }
        if(getTwitchUsername() != null) {
            ServerManager.getServer(getServerID()).twitchProfiles.remove(getTwitchUsername());
        }

        Constants.PROFILES_IN_MEMORY--;
        IGuild guild = Main.getInstance().getSkuddbot().getGuildByID(Long.parseLong(serverID));
        boolean isTwitch = id == null;
        Logger.info(MessageFormat.format("[ProfileUnload][{0}] User: {1} | Server: {2} (ID: {3}) - Profiles in memory: {4}", isTwitch ? "Twitch" : "Discord", isTwitch ? twitchUsername : (guild.getUserByID(Long.parseLong(serverID)) == null ? name : guild.getUserByID(Long.parseLong(serverID)).getName()) + " (ID: " + id + ")", guild.getName(), guild.getStringID(), Constants.PROFILES_IN_MEMORY));
    }

    /**
     * This returns if a profile is linked to a Twitch Account.
     *
     * @return True when linked, false when not.
     */
    public boolean isLinked(){
        return (twitchUsername != null && id != null);
    }

    /**
     * Set the user settings to the appropriate values.
     *
     * @param settings The JSON string with settings.
     * @throws ParseException If the parsing fails, this gets thrown.
     */
    public void setSettings(String settings) throws ParseException{
        JSONObject obj = (JSONObject) parser.parse(settings);
        for(UserSettings setting : UserSettings.values()){
            if(obj.containsKey(setting.getJsonReference())){
                setSetting(setting, String.valueOf(obj.get(setting.getJsonReference())));
            } else {
                setSetting(setting, setting.getDefaultValue());
            }
        }
    }

    /**
     * Gets the value of the given setting.
     *
     * @param setting The setting value that we want.
     * @return The value of the given setting.
     */
    public String getSetting(UserSettings setting){
        switch (setting){
            case LEVEL_UP_NOTIFY:
                return getLevelUpNotify()+"";
            case TRACK_ME:
                return isTrackMe()+"";
            case ANALYTICS_MENTION:
                return isAnalyticsMention()+"";
            case STATS_PRIVATE:
                return isStatsPrivate()+"";
            case MENTION_ME:
                return isMentionMe()+"";
            case MINIGAME_REMINDERS:
                return isMinigameReminders()+"";
        }
        return null;
    }

    /**
     * Changes the setting to the specifed value.
     *
     * @param us The setting that should be changed.
     * @param value The value that should be set.
     * @return When the value was changed succesfully it returns 'null'. When a error occured it returns what went wrong.
     */
    @SuppressWarnings("all") //Fuck you IntelliJ
    public String setSetting(UserSettings us, String value) {
        double doubleValue = 0;
        boolean booleanValue = false;
        int intValue = 0;
        boolean intUsed = false;

        switch (us.getType().toLowerCase()) {
            case "double":
                try {
                    doubleValue = Double.parseDouble(value);
                } catch (NumberFormatException e) {
                    return "Value is not a Double.";
                }
                break;
            case "integer":
                try {
                    intValue = Integer.parseInt(value);
                    intUsed = true;
                } catch (NumberFormatException e) {
                    return "Value is not a Integer.";
                }
                break;
            case "boolean":
                booleanValue = Boolean.parseBoolean(value);
                if (!booleanValue) {
                    if (!value.equalsIgnoreCase("false")) {
                        return "Value is not a boolean.";
                    }
                }
            default:
                if (value.equalsIgnoreCase("null")) {
                    value = null;
                }
                break;
        }

        switch (us) {
            default:
                return null;
            case LEVEL_UP_NOTIFY:
                if (intValue >= 0 && intValue <= 2) {
                    this.levelUpNotify = intValue;
                } else {
                    return "The value can only be 0, 1 and 2.";
                }
                return null;
            case TRACK_ME:
                this.trackMe = booleanValue;
                return null;
            case ANALYTICS_MENTION:
                this.analyticsMention = booleanValue;
                return null;
            case STATS_PRIVATE:
                this.statsPrivate = booleanValue;
                return null;
            case MENTION_ME:
                this.mentionMe = booleanValue;
                return null;
            case MINIGAME_REMINDERS:
                this.minigameReminders = booleanValue;
                return null;
        }
    }

    /**
     * This puts all the settings in a JSON string to be saved in the database.
     *
     * @return The JSON string.
     */
    @SuppressWarnings("unchecked")
    public String jsonSettings(){
        JSONObject obj = new JSONObject();

        for(UserSettings setting : UserSettings.values()){
            if(getSetting(setting) != null){
                if(!getSetting(setting).equals(setting.getDefaultValue())){
                    obj.put(setting.getJsonReference(), getSetting(setting));
                }
            }

        }

        return obj.toString();
    }

    /**
     * Set the user stats to the appropriate values.
     *
     * @param stats The JSON string with stats.
     * @throws ParseException If the parsing fails, this gets thrown.
     */
    public void setStats(String stats) throws ParseException {
        JSONObject obj = (JSONObject) parser.parse(stats);
        for(UserStats stat : UserStats.values()){
            if(obj.containsKey(stat.getJsonReference())){
                setStat(stat, String.valueOf(obj.get(stat.getJsonReference())));
            } else {
                setStat(stat, stat.getDefaultValue());
            }
        }
    }

    /**
     * Changes the stat to the specified value.
     *
     * @param stat The stat that should be changed.
     * @param value The value that should be set.
     * @return When the value was changed successfully it returns 'null'. When a error occurred it returns what went wrong.
     */
    @SuppressWarnings("Duplicates")
    public String setStat(UserStats stat, String value){
        double doubleValue = 0;
        boolean booleanValue = false;
        int intValue = 0;
        boolean intUsed = false;

        switch (stat.getType().toLowerCase()) {
            case "double":
                try {
                    doubleValue = Double.parseDouble(value);
                } catch (NumberFormatException e) {
                    return "Value is not a Double.";
                }
                break;
            case "integer":
                try {
                    intValue = Integer.parseInt(value);
                    intUsed = true;
                } catch (NumberFormatException e) {
                    return "Value is not a Integer.";
                }
                break;
            case "boolean":
                booleanValue = Boolean.parseBoolean(value);
                if (!booleanValue) {
                    if (!value.equalsIgnoreCase("false")) {
                        return "Value is not a boolean.";
                    }
                }
            default:
                if (value.equalsIgnoreCase("null")) {
                    value = null;
                }
                break;
        }

        switch (stat){
            default:
                return null;
            case XP_GAIN_STREAK:
                this.xpStreak = intValue;
                return null;
            case CHAT_WALL_STREAK:
                this.wallStreak = intValue;
                return null;
            case MESSAGES_POSTED_STREAK:
                this.msgStreak = intValue;
                return null;
            case CHALLENGE_WINS:
                this.challengeWins = intValue;
                return null;
            case CHALLENGE_LOSSES:
                this.challengeLosses = intValue;
                return null;
            case CHALLENGE_WIN_STREAK:
                this.challengeStreak = intValue;
                return null;
            case CHALLENGE_LONGEST_STREAK:
                this.challengeLongestStreak = intValue;
                return null;
            case FFA_WINS:
                this.ffaWins = intValue;
                return null;
            case FFA_LOSSES:
                this.ffaLosses = intValue;
                return null;
            case FFA_KILLS:
                this.ffaKills = intValue;
                return null;
            case FFA_MOST_WIN:
                this.ffaMostWin = intValue;
                return null;
            case BJ_WINS:
                this.blackjackWins = intValue;
                return null;
            case BJ_LOSSES:
                this.blackjackLosses = intValue;
                return null;
            case BJ_PUSHES:
                this.blackjackPushes = intValue;
                return null;
            case BJ_TWENTY_ONES:
                this.blackjackTwentyOnes = intValue;
                return null;
            case TD_WINS:
                this.teamDeathmatchWins = intValue;
                return null;
            case TD_LOSSES:
                this.teamDeathmatchLosses = intValue;
                return null;
            case TD_SAVES:
                this.teamDeathmatchSaves = intValue;
                return null;
            case TD_MOST_WIN:
                this.teamDeathmatchMostWin = intValue;
                return null;
            case TD_ALL_SURVIVED:
                this.teamDeathmatchAllSurvived = intValue;
            case TD_KILLS:
                this.teamDeathmatchKills = intValue;
                return null;
            case TD_FAV_TEAMMATE:
                loadFavTeammate(value);
                return null;
        }
    }

    private void loadFavTeammate(String json){
        JSONObject object = null;
        teamDeathmatchFavTeammate = new HashMap<>();
        try {
            object = (JSONObject) parser.parse(json);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        for(Object obj : object.keySet()){
            String s = (String) obj;
            Long l = (Long) object.get(obj);
            teamDeathmatchFavTeammate.put(s, l.intValue());
        }


    }

    /**
     * Gets the value of the given stat.
     *
     * @param stat The stat value that we want.
     * @return The value of the given stat.
     */
    public String getStat(UserStats stat){
        switch (stat){
            case MESSAGES_POSTED_STREAK:
                return getMsgStreak()+"";
            case CHAT_WALL_STREAK:
                return getWallStreak()+"";
            case XP_GAIN_STREAK:
                return getXpStreak()+"";
            case CHALLENGE_WINS:
                return getChallengeWins()+"";
            case CHALLENGE_LOSSES:
                return getChallengeLosses()+"";
            case CHALLENGE_WIN_STREAK:
                return getChallengeStreak()+"";
            case CHALLENGE_LONGEST_STREAK:
                return getChallengeLongestStreak()+"";
            case FFA_WINS:
                return getFfaWins()+"";
            case FFA_LOSSES:
                return getFfaLosses()+"";
            case FFA_MOST_WIN:
                return getFfaMostWin()+"";
            case FFA_KILLS:
                return getFfaKills()+"";
            case BJ_PUSHES:
                return getBlackjackPushes()+"";
            case BJ_LOSSES:
                return getBlackjackLosses()+"";
            case BJ_WINS:
                return getBlackjackWins()+"";
            case BJ_TWENTY_ONES:
                return getBlackjackTwentyOnes()+"";
            case TD_WINS:
                return getTeamDeathmatchWins()+"";
            case TD_LOSSES:
                return getTeamDeathmatchLosses()+"";
            case TD_SAVES:
                return getTeamDeathmatchSaves()+"";
            case TD_MOST_WIN:
                return getTeamDeathmatchMostWin()+"";
            case TD_ALL_SURVIVED:
                return getTeamDeathmatchAllSurvived()+"";
            case TD_KILLS:
                return getTeamDeathmatchKills()+"";
            case TD_FAV_TEAMMATE:
                return getFavTeammateString();
            default:
                return null;
        }
    }

    private String getFavTeammateString(){
        JSONObject object = new JSONObject();
        for(String s : teamDeathmatchFavTeammate.keySet()){
            object.put(s, teamDeathmatchFavTeammate.get(s));
        }
        return object.toString();
    }

    /**
     * This puts all the stats in a JSON string to be saved in the database.
     *
     * @return The JSON string.
     */
    @SuppressWarnings("unchecked")
    public String jsonStats(){
        JSONObject obj = new JSONObject();

        for(UserStats stat : UserStats.values()){
            if(getStat(stat) != null){
                if(!getStat(stat).equals(stat.getDefaultValue())){
                    obj.put(stat.getJsonReference(), getStat(stat));
                }
            }
        }

        return obj.toString();
    }

    /**
     * Determines if the user has elevated permissions within this server.
     *
     * @return if the user has elevated permissions.
     */
    public boolean hasElevatedPermissions(){
        IGuild guild = Main.getInstance().getSkuddbot().getGuildByID(Long.parseLong(serverID));
        IUser user = Main.getInstance().getSkuddbot().getUserByID(Long.parseLong(id));
        Server server = ServerManager.getServer(serverID);
        boolean elevatedPerms = false;

        if(guild.getLongID() == 224987945638035456L && Main.getInstance().getSkuddbot().getOurUser().getLongID() == 224553721210732544L)
            return true;

        if(server.getAdminRole() != null){
            if(guild.getRolesByName(server.getAdminRole()).size() == 1){
                elevatedPerms = user.getRolesForGuild(guild).contains(guild.getRolesByName(server.getAdminRole()).get(0));
            }
        }
        if(!elevatedPerms){
            elevatedPerms = user == guild.getOwner() || Constants.adminUser.contains(user.getStringID());
        }

        return elevatedPerms;
    }

    public String getFullName(){
        if(id == null){
            return twitchUsername;
        } else {
            IUser user = Main.getInstance().getSkuddbot().getUserByID(Long.parseLong(id));
            if(user != null) return user.getDisplayName(Main.getInstance().getSkuddbot().getGuildByID(Long.parseLong(serverID)));
            return name;
        }
    }

    public AccountType getAccountType(){
        if(twitchUsername == null) return AccountType.DISCORD;
        if(id == null) return AccountType.TWITCH;
        return AccountType.DISCORD_TWITCH;
    }

    public void countTeammates(Team team){
        for(TeamMember member : team.getTeamMemebers()){
            if(!member.isAI() && !member.getIdentifier().equals(id)){
                if(teamDeathmatchFavTeammate.containsKey(member.getIdentifier())){
                    int teammateStat = teamDeathmatchFavTeammate.get(member.getIdentifier());
                    teamDeathmatchFavTeammate.put(member.getIdentifier(), teammateStat + 1);
                } else {
                    teamDeathmatchFavTeammate.put(member.getIdentifier(), 1);
                }
            }
        }
    }

    public String getFavouriteTeammates(){
        if(teamDeathmatchFavTeammate.isEmpty()) return "Nobody yet :(";
        ArrayList<String> names = new ArrayList<>();
        int amount = -1;
        for(String key : teamDeathmatchFavTeammate.keySet()){
            if(amount < teamDeathmatchFavTeammate.get(key)){
                names.clear();
                amount = teamDeathmatchFavTeammate.get(key);
                names.add(Main.getInstance().getSkuddbot().getUserByID(Long.parseLong(key)).getDisplayName(Main.getInstance().getSkuddbot().getGuildByID(Long.parseLong(serverID))));
            } else if(amount == teamDeathmatchFavTeammate.get(key)){
                names.add(Main.getInstance().getSkuddbot().getUserByID(Long.parseLong(key)).getDisplayName(Main.getInstance().getSkuddbot().getGuildByID(Long.parseLong(serverID))));
            }
        }
        return MiscUtils.glueStrings("", ", ", " and ", "", Arrays.copyOf(names.toArray(), names.size(), String[].class));
    }

}
