package me.Cooltimmetje.Skuddbot.Profiles;

import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.User;
import me.Cooltimmetje.Skuddbot.Utilities.MiscUtils;

/**
 * This class handles all profiles, getting them from either memory or the database, or creating new ones.
 *
 * @author Tim (Cooltimmetje)
 * @version v0.5.1-ALPHA
 * @since v0.1-ALPHA
 */

public class ProfileManager {

    /**
     * This method is used to get the SkuddUser instance for a user on a server by Discord ID.
     * First we check if the user exists in memory for the specified server, if it does: return it.
     * Then we check if there is a entry in the database, if it does: load it into memory and return it.
     * Finally we just create a new one, given createNew is true.
     *
     * @param id         specifies the user ID
     * @param serverID   specifies the server ID
     * @param createNew  specifies if we should create a new user when there isn't a existing one for the specified ID on the specified server, when false and there isn't a user, we return null.
     * @return           Returns the SkuddUser instance.
     */
    public static SkuddUser getDiscord(String id, String serverID, boolean createNew){
        Server server = ServerManager.getServer(serverID); //Get the server instance.
        SkuddUser user = server.getDiscord(id); //Get the user instance.

        if(user != null){ //If there is a user in memory, make it active, and return it.
            user.setInactive(false);
            return user;
        } else {
            user = MySqlManager.getProfile(id, serverID); //Get profile from the database.
            if(user == null && createNew) { //Check if it doesn't exist and if we should create a new one.
                user = new SkuddUser(id, serverID, null); //Create new instance
            }

            if(user == null){ //To return null when there isn't a existing user and we should NOT create a new one.
                return null;
            }

            server.addDiscord(user); //Add the loaded profile to memory.
            if(user.getTwitchUsername() != null){ //If there is a Twitch Username connected, make sure we can get this same profile by discord.
                server.addTwitch(user);
            }
            user.setInactive(false); //Set active.
            server.lastSeen.put(Long.parseLong(id), System.currentTimeMillis()); //Report last seen.
            return user; //Return it
        }

    }

    public static SkuddUser getDiscord(Member member, boolean createNew){
        return getDiscord(member.getId().asString(), member.getGuildId().asString(), createNew);
    }

    public static SkuddUser getDiscord(User user, Guild guild, boolean createNew) {
        return getDiscord(user.asMember(guild.getId()).block(), createNew);
    }

    public static SkuddUser getTwitch(String twitchUsername, String twitchChannel, boolean createNew){
        twitchChannel = twitchChannel.replace("#", " ").trim();
        Server server = ServerManager.getTwitch(twitchChannel);
        String serverID = server.getServerID();
        SkuddUser user = server.getTwitch(twitchUsername);

        if(user != null){
            user.setInactive(false);
            return user;
        } else {
            user = MySqlManager.getTwitch(twitchUsername, serverID);
            if(user == null && createNew){
                user = new SkuddUser(null, serverID, twitchUsername);
            }

            if(user == null){
                return null;
            }

            server.addTwitch(user);
            if(user.getId() != null){
                server.addDiscord(user);
            }
            user.setInactive(false);
            return user;
        }
    }

    public static SkuddUser getTwitchServer(String twitchUsername, String serverID){
        Server server = ServerManager.getServer(serverID);
        SkuddUser user = server.getTwitch(twitchUsername);

        if(user != null){
            user.setInactive(false);
            return user;
        } else {
            user = MySqlManager.getTwitch(twitchUsername, serverID);
            if(user == null){
                user = new SkuddUser(null, serverID, twitchUsername);
            }

            server.addTwitch(user);
            if(user.getId() != null){
                server.addDiscord(user);
            }
            user.setInactive(false);
            return user;
        }
    }

    public static void swapTwitch(SkuddUser user, String serverID){
        Server server = ServerManager.getServer(serverID);
        server.removeTwitch(user.getTwitchUsername());
        server.addTwitch(user);
    }

    public static SkuddUser getByString(String str, String serverID, boolean createNew){
        SkuddUser su = null;
        if(MiscUtils.isLong(str)) {
            su = getDiscord(str, serverID, createNew);
        } else {
            su = getTwitchServer(str, serverID);
        }

        return su;
    }

}
