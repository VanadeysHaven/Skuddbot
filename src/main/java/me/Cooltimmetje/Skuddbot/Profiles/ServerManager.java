package me.Cooltimmetje.Skuddbot.Profiles;

import java.util.HashMap;

/**
 * Created by Tim on 8/22/2016.
 */
public class ServerManager {

    public static HashMap<String,Server> servers = new HashMap<>();
    public static HashMap<String,Server> twitchServers = new HashMap<>();

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

    public static Server getTwitch(String twitchChannel){
        return twitchServers.get(twitchChannel);
    }


    public static void saveAll(){
        servers.values().forEach(Server::save);
    }

}
