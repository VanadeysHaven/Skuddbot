package me.Cooltimmetje.Skuddbot.Profiles;

import lombok.Getter;
import lombok.Setter;
import me.Cooltimmetje.Skuddbot.Main;
import me.Cooltimmetje.Skuddbot.Utilities.Constants;
import me.Cooltimmetje.Skuddbot.Utilities.Logger;
import me.Cooltimmetje.Skuddbot.Utilities.MessagesUtils;
import org.apache.commons.lang3.StringUtils;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;

import java.text.MessageFormat;
import java.util.List;

/**
 * Created by Tim on 8/4/2016.
 */
@Getter
@Setter
public class SkuddUser {

    private String id;
    private String serverID;
    private String name;
    private int xp;
    private int level;
    private String twitchUsername;
    private String twitchVerify;
    private IMessage verifyMessage;
    private boolean inactive;

    public SkuddUser(String id, String serverID, String twitchUsername){
        this.id = id;
        this.name = (id != null ? Main.getInstance().getSkuddbot().getUserByID(id).getName() : null);
        this.serverID = serverID;
        this.xp = 0;
        this.level = 1;
        this.twitchUsername = twitchUsername;
        this.inactive = false;

        Constants.PROFILES_IN_MEMORY++;
        IGuild guild = Main.getInstance().getSkuddbot().getGuildByID(serverID);
        boolean isTwitch = id == null;
        Logger.info(MessageFormat.format("[ProfileCreate][{0}] User: {1} | Server: {2} (ID: {3}) - Profiles in memory: {4}", isTwitch ? "Twitch" : "Discord", isTwitch ? twitchUsername : guild.getUserByID(id).getName() + " (ID: " + id + ")", guild.getName(), guild.getID(), Constants.PROFILES_IN_MEMORY));

        try{
            setRoles();
        } catch (IndexOutOfBoundsException e){
            Logger.info("Something happened... Something bad...");
        }
    }

    public SkuddUser(String id, String serverID, String name, int xp, String twitchUsername) {
        this.id = id;
        this.name = name;
        this.serverID = serverID;
        this.xp = xp;
        this.twitchUsername = twitchUsername;
        this.inactive = false;

        if(id != null) {
            calcXP(true, null);
        }

        Constants.PROFILES_IN_MEMORY++;
        IGuild guild = Main.getInstance().getSkuddbot().getGuildByID(serverID);
        boolean isTwitch = id == null;
        Logger.info(MessageFormat.format("[ProfileLoad][{0}] User: {1} | Server: {2} (ID: {3}) - Profiles in memory: {4}", isTwitch ? "Twitch" : "Discord", isTwitch ? twitchUsername : (guild.getUserByID(id) == null ? name : guild.getUserByID(id).getName()) + " (ID: " + id + ")", guild.getName(), guild.getID(), Constants.PROFILES_IN_MEMORY));

        try{
            setRoles();
        } catch (IndexOutOfBoundsException e){
            Logger.info("Something happened... Something bad...");
        }
    }

    public int[] calcXP(boolean load, IMessage message){
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

        if(load){
            setLevel(level);
        } else {
            if(level > getLevel()){
                int levels = level - getLevel();
                setLevel(level);

                MessagesUtils.sendSuccess(MessageFormat.format("{0}, you leveled up! You are now **level {1}**! {2}", message.getAuthor().mention(),getLevel(), levels > 1 ? "(You leveled up **" + levels + " times**)" : " "), message.getChannel());
            }
        }

        return new int[]{exp, getXp(), needed, level};
    }


    public String calcXpLB(int length){
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

        String nameUser = (id == null ? getTwitchUsername() : (Main.getInstance().getSkuddbot().getUserByID(getId()) == null ? name : (Main.getInstance().getSkuddbot().getUserByID(getId()).getNicknameForGuild(Main.getInstance().getSkuddbot().getGuildByID(getServerID())).isPresent() ?
                Main.getInstance().getSkuddbot().getUserByID(getId()).getNicknameForGuild(Main.getInstance().getSkuddbot().getGuildByID(getServerID())).get() : Main.getInstance().getSkuddbot().getUserByID(getId()).getName())));

        int progress = (int) (((double)exp / (double)needed)*100); //We don't really care about rounding here. Ain't nobody noticing that shit... Unless your name is Jasch.

        return nameUser + StringUtils.repeat(" ", length - nameUser.length()) + " | Level " + (level < 10 ? " " + level : level) + " (" + (progress < 10 ? " " : "") + progress+ ("%) " +
                (getId() == null ? "- Twitch (not linked)" : (getTwitchUsername() == null ? "- Discord (not linked)" : " ")));
    }

    public void save(){
        boolean isTwitch = id == null;
        IGuild guild = Main.getInstance().getSkuddbot().getGuildByID(serverID);
        Logger.info(MessageFormat.format("[ProfileSave][" + (isTwitch ? "Twitch" : "Discord") + "] User: {0} | Server: {1} (ID: {2})", isTwitch ? twitchUsername : (guild.getUserByID(id) == null ? name : guild.getUserByID(id).getName()) + " (ID: " + id + ")", guild.getName(), guild.getID()));

        if(isTwitch){
            MySqlManager.saveTwitch(this);
        } else {
            MySqlManager.saveProfile(this);
        }
    }

    public void setRoles(){
        if(id != null && twitchUsername != null && Main.getInstance().getSkuddbot().getUserByID(id) != null){
            IUser user = Main.getInstance().getSkuddbot().getUserByID(id);
            IGuild guild = Main.getInstance().getSkuddbot().getGuildByID(serverID);
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
        IGuild guild = Main.getInstance().getSkuddbot().getGuildByID(serverID);
        boolean isTwitch = id == null;
        Logger.info(MessageFormat.format("[ProfileUnload][{0}] User: {1} | Server: {2} (ID: {3}) - Profiles in memory: {4}", isTwitch ? "Twitch" : "Discord", isTwitch ? twitchUsername : (guild.getUserByID(id) == null ? name : guild.getUserByID(id).getName()) + " (ID: " + id + ")", guild.getName(), guild.getID(), Constants.PROFILES_IN_MEMORY));
    }
}
