package me.Cooltimmetje.Skuddbot.Profiles;

import sx.blah.discord.handle.obj.IGuild;

import java.util.HashMap;

/**
 * Used for handling servers.
 *
 * @author Tim (Cooltimmetje)
 * @version v0.4.61-ALPHA
 * @since v0.2-ALPHA
 */
public class ServerManager {

    public static HashMap<String,Server> servers = new HashMap<>();
    public static HashMap<String,Server> twitchServers = new HashMap<>();

    /**
     * Get the server by their Guild Instance.
     *
     * @param guild The Guild Instance of the server we want.
     * @return The server instance.
     */
    public static Server getServer(IGuild guild){
        return getServer(guild.getStringID());
    }

    /**
     * Get the server by their Guild ID.
     *
     * @param id The Guild ID of the server we want.
     * @return The server instance.
     */
    public static Server getServer(String id){
        if(servers.containsKey(id)) {
            return servers.get(id);
        } else {
            Server server = MySqlManager.loadServer(id);
            if(server != null){
                return server;
            } else {
                return new Server(id);
            }
        }
    }

    /**
     * Get the server by their Twitch Channel
     *
     * @param twitchChannel The Twitch Channel of the server we want.
     * @return The server instance.
     */
    public static Server getTwitch(String twitchChannel){
        if(twitchChannel.startsWith("#")) twitchChannel = twitchChannel.substring(1);
        return twitchServers.get(twitchChannel);
    }


    /**
     * Save all servers to the database.
     * @param onlySettings If true, profiles will not be saved.
     */
    public static void saveAll(boolean onlySettings){
        servers.values().forEach(server -> server.save(onlySettings));
    }

}
