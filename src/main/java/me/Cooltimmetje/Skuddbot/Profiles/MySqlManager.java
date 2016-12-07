package me.Cooltimmetje.Skuddbot.Profiles;

import com.zaxxer.hikari.HikariDataSource;
import me.Cooltimmetje.Skuddbot.Enums.DataTypes;
import me.Cooltimmetje.Skuddbot.Listeners.CreateServerListener;
import me.Cooltimmetje.Skuddbot.Main;
import me.Cooltimmetje.Skuddbot.SkuddbotTwitch;
import me.Cooltimmetje.Skuddbot.Utilities.Constants;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

/**
 * This class handles everything to do with the database, and contains all operations we can run on the database.
 *
 * @author Tim (Cooltimmetje)
 * @version v0.3-ALPHA-DEV
 * @since v0.1-ALPHA
 */
public class MySqlManager {

    private static HikariDataSource hikari = null;

    public static void setupHikari(String user, String pass){
        hikari = new HikariDataSource();
        hikari.setMaximumPoolSize(10);

        hikari.setDataSourceClassName("com.mysql.jdbc.jdbc2.optional.MysqlDataSource");
        hikari.addDataSourceProperty("serverName", "localhost");
        hikari.addDataSourceProperty("port", 3306);
        hikari.addDataSourceProperty("databaseName", "skuddbot");
        hikari.addDataSourceProperty("user", user);
        hikari.addDataSourceProperty("password", pass);
    }

    public static void disconnect(){
        hikari.close();
    }

    public static SkuddUser getTwitch(String twitchUsername, String serverID){
        SkuddUser user = getDiscordByTwitch(twitchUsername, serverID);

        if(user == null){
            user = getTwitchProfile(twitchUsername, serverID);
        }

        return user;
    }

    public static SkuddUser getDiscordByTwitch(String twitchUsername, String serverID){
        Connection c = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        SkuddUser user = null;

        String query = "SELECT * FROM " + serverID + "_discord WHERE twitch_username = '" + twitchUsername + "';";

        try {
            c = hikari.getConnection();
            ps = c.prepareStatement(query);
            rs = ps.executeQuery();
            if(rs.next()){
                user = new SkuddUser(rs.getString("discord_id"), serverID, rs.getString("discord_username"), rs.getInt("xp"), rs.getString("twitch_username"));
            } else {
                user = null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if(c != null){
                try {
                    c.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if(ps != null){
                try {
                    ps.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if(rs != null){
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        return user;
    }

    private static SkuddUser getTwitchProfile(String twitchUsername, String serverID){
        Connection c = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        SkuddUser user = null;

        String query = "SELECT * FROM " + serverID + "_twitch WHERE twitch_user = '" + twitchUsername + "';";

        try {
            c = hikari.getConnection();
            ps = c.prepareStatement(query);
            rs = ps.executeQuery();
            if(rs.next()){
                user = new SkuddUser(null, serverID, null,rs.getInt("xp"), rs.getString("twitch_user"));
            } else {
                user = null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if(c != null){
                try {
                    c.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if(ps != null){
                try {
                    ps.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if(rs != null){
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        return user;
    }

    public static void saveTwitch(SkuddUser user){
        Connection c = null;
        PreparedStatement ps = null;
        String create = "INSERT INTO " + user.getServerID() + "_twitch VALUES(?,?)ON DUPLICATE KEY UPDATE xp=?";

        try {
            c = hikari.getConnection();
            ps = c.prepareStatement(create);

            ps.setString(1, user.getTwitchUsername());
            ps.setInt(2, user.getXp());
            ps.setInt(3, user.getXp());

            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if(c != null){
                try {
                    c.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if(ps != null){
                try {
                    ps.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static SkuddUser getProfile(String id, String serverID){
        Connection c = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        SkuddUser user = null;

        String query = "SELECT * FROM " + serverID + "_discord WHERE discord_id = '" + id + "';";

        try {
            c = hikari.getConnection();
            ps = c.prepareStatement(query);
            rs = ps.executeQuery();

            if(rs.next()){
                user = new SkuddUser(rs.getString("discord_id"),serverID, rs.getString("discord_username"), rs.getInt("xp"), rs.getString("twitch_username"));
            } else {
                user = null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if(c != null){
                try {
                    c.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if(ps != null){
                try {
                    ps.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if(rs != null){
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        return user;
    }

    public static void saveProfile(SkuddUser user) {
        Connection c = null;
        PreparedStatement ps = null;
        String create = "INSERT INTO " + user.getServerID() + "_discord VALUES(?,?,?,?) ON DUPLICATE KEY UPDATE discord_username=?,xp=?,twitch_username=?";

        try {
            c = hikari.getConnection();
            ps = c.prepareStatement(create);

            ps.setString(1, user.getId());
            ps.setString(2, (Main.getInstance().getSkuddbot().getUserByID(user.getId()) == null ? user.getName() : Main.getInstance().getSkuddbot().getUserByID(user.getId()).getName()));
            ps.setInt(3, user.getXp());
            ps.setString(4, user.getTwitchUsername());
            ps.setString(5, (Main.getInstance().getSkuddbot().getUserByID(user.getId()) == null ? user.getName() : Main.getInstance().getSkuddbot().getUserByID(user.getId()).getName()));
            ps.setInt(6, user.getXp());
            ps.setString(7, user.getTwitchUsername());

            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if(c != null){
                try {
                    c.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if(ps != null){
                try {
                    ps.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void deleteTwitch(String username, String serverID){
        Connection c = null;
        PreparedStatement ps = null;
        String create = "DELETE FROM " + serverID + "_twitch WHERE twitch_user=?";

        try {
            c = hikari.getConnection();
            ps = c.prepareStatement(create);

            ps.setString(1, username);

            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if(c != null){
                try {
                    c.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if(ps != null){
                try {
                    ps.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static HashMap<Integer,SkuddUser> getTopDiscord(String serverID){
        HashMap<Integer,SkuddUser> top = new HashMap<>();

        Connection c = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        String query = "SELECT * FROM " + serverID + "_discord ORDER BY xp DESC;";

        try {
            c = hikari.getConnection();
            ps = c.prepareStatement(query);
            rs = ps.executeQuery();

            int i = 0;
            while (rs.next() && i < 10){
                SkuddUser user = ProfileManager.getDiscord(rs.getString("discord_id"), serverID, true);
                top.put(user.getXp(),user);
                i++;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if(c != null){
                try {
                    c.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if(ps != null){
                try {
                    ps.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if(rs != null){
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        return top;
    }

    public static HashMap<Integer,SkuddUser> getTopTwitch(String serverID){
        HashMap<Integer,SkuddUser> top = new HashMap<>();

        Connection c = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        String query = "SELECT * FROM " + serverID + "_twitch ORDER BY xp DESC;";

        try {
            c = hikari.getConnection();
            ps = c.prepareStatement(query);
            rs = ps.executeQuery();

            int i = 0;
            while (rs.next() && i < 10){
                SkuddUser user = ProfileManager.getTwitchServer(rs.getString("twitch_user"), serverID);
                top.put(user.getXp(),user);
                i++;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if(c != null){
                try {
                    c.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if(ps != null){
                try {
                    ps.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if(rs != null){
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        return top;
    }

    public static Server loadServer(String id) {
        Server loaded = null;

        Connection c = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        String query = "SELECT * FROM servers WHERE server_id=?;";

        try {
            c = hikari.getConnection();
            ps = c.prepareStatement(query);

            ps.setString(1, id);

            rs = ps.executeQuery();
            if(rs.next()){
                loaded = new Server(rs.getString(1),rs.getInt(2),rs.getInt(3),rs.getInt(4),rs.getInt(5),rs.getInt(6),rs.getDouble(7),
                        rs.getString(8),rs.getString(9),rs.getString(10),rs.getString(11),rs.getString(12),rs.getString(13),rs.getString(14));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if(c != null){
                try {
                    c.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if(ps != null){
                try {
                    ps.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if(rs != null){
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        return loaded;
    }

    public static void saveServer(Server server) {
        Connection c = null;
        PreparedStatement ps = null;

        String query = "INSERT INTO servers VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?) ON DUPLICATE KEY UPDATE xp_min=?,xp_max=?,xp_min_twitch=?,xp_max_twitch=?,xp_base=?,xp_multiplier=?,cleverbot_channel=?,twitch_channel=?,welcome_message=?,goodbye_message=?,welcome_goodbye_chan=?,admin_role=?,role_on_join=?";

        try {
            c = hikari.getConnection();
            ps = c.prepareStatement(query);

            ps.setString(1, server.getServerID());
            ps.setInt(2, server.getMinXP());
            ps.setInt(3, server.getMaxXP());
            ps.setInt(4, server.getMinXpTwitch());
            ps.setInt(5, server.getMaxXpTwitch());
            ps.setInt(6, server.getXpBase());
            ps.setDouble(7, server.getXpMultiplier());
            ps.setString(8, server.getCleverbotChannel());
            ps.setString(9, server.getTwitchChannel());
            ps.setString(10, server.getWelcomeMessage());
            ps.setString(11, server.getGoodbyeMessage());
            ps.setString(12, server.getWelcomeGoodbyeChannel());
            ps.setString(13, server.getAdminRole());
            ps.setString(14, server.getRoleOnJoin());
            ps.setInt(15, server.getMinXP());
            ps.setInt(16, server.getMaxXP());
            ps.setInt(17, server.getMinXpTwitch());
            ps.setInt(18, server.getMaxXpTwitch());
            ps.setInt(19, server.getXpBase());
            ps.setDouble(20, server.getXpMultiplier());
            ps.setString(21, server.getCleverbotChannel());
            ps.setString(22, server.getTwitchChannel());
            ps.setString(23, server.getWelcomeMessage());
            ps.setString(24, server.getGoodbyeMessage());
            ps.setString(25, server.getWelcomeGoodbyeChannel());
            ps.setString(26, server.getAdminRole());
            ps.setString(27, server.getRoleOnJoin());

            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if(c != null){
                try {
                    c.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if(ps != null){
                try {
                    ps.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

        }

    }

    public static void createServerTables(String serverID){
        createDiscordTable(serverID);
        createTwitchTable(serverID);
    }

    private static void createDiscordTable(String serverID){
        Connection c = null;
        PreparedStatement ps = null;

        String query = "CREATE TABLE " + serverID + "_discord (discord_id VARCHAR(25),discord_username VARCHAR(100) NOT NULL,xp INT(11) DEFAULT 0 NOT NULL,twitch_username VARCHAR(100) NULL DEFAULT NULL, PRIMARY KEY (discord_id));";

        try {
            c = hikari.getConnection();
            ps = c.prepareStatement(query);

            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if(c != null){
                try {
                    c.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if(ps != null){
                try {
                    ps.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    private static void createTwitchTable(String serverID){
        Connection c = null;
        PreparedStatement ps = null;

        String query = "CREATE TABLE " + serverID + "_twitch (twitch_user VARCHAR(100),xp INT(11) DEFAULT 0 NOT NULL, PRIMARY KEY (twitch_user));";

        try {
            c = hikari.getConnection();
            ps = c.prepareStatement(query);

            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if(c != null){
                try {
                    c.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if(ps != null){
                try {
                    ps.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    @SuppressWarnings("unchecked")
    public static JSONArray dumpDiscord(){
        JSONArray dump = new JSONArray();

        Connection c = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        String query = "SELECT * FROM users;";

        try {
            c = hikari.getConnection();
            ps = c.prepareStatement(query);
            rs = ps.executeQuery();

            while(rs.next()){
                JSONObject obj = new JSONObject();
                obj.put("id", rs.getString("discord_id"));
                obj.put("name", rs.getString("discord_username"));
                obj.put("discrim", Main.getInstance().getSkuddbot().getUserByID(rs.getString("discord_id")).getDiscriminator());
                obj.put("avatar_url", Main.getInstance().getSkuddbot().getUserByID(rs.getString("discord_id")).getAvatarURL());
                obj.put("xp", rs.getInt("xp"));
                obj.put("twitch_username", rs.getString("twitch_username"));

                dump.add(obj);
            }
        } catch (SQLException e){
            e.printStackTrace();
        } finally {
            if(c != null){
                try {
                    c.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if(ps != null){
                try {
                    ps.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if(rs != null){
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        return dump;
    }

    @SuppressWarnings("unchecked")
    public static JSONArray dumpTwitch(){
        JSONArray dump = new JSONArray();

        Connection c = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        String query = "SELECT * FROM twitch_users;";

        try {
            c = hikari.getConnection();
            ps = c.prepareStatement(query);
            rs = ps.executeQuery();

            while(rs.next()){
                JSONObject obj = new JSONObject();
                obj.put("twitch_username", rs.getString("twitch_user"));
                obj.put("xp", rs.getInt("xp"));

                dump.add(obj);
            }
        } catch (SQLException e){
            e.printStackTrace();
        } finally {
            if(c != null){
                try {
                    c.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if(ps != null){
                try {
                    ps.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if(rs != null){
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        return dump;
    }

    @SuppressWarnings("ignored")
    public static void dumpDataToJSON(){
        JSONObject obj = new JSONObject();
        JSONArray discord = dumpDiscord();
        JSONArray twitch = dumpTwitch();

        //noinspection unchecked
        obj.put("Discord", discord);
        //noinspection unchecked
        obj.put("Twitch", twitch);


        try (FileWriter file = new FileWriter("dump.json")) {
            file.write(obj.toJSONString());
            System.out.println("Successfully Copied JSON Object to File...");
            System.out.println("\nJSON Object: " + obj);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadGlobal(){
        Connection c = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        String query = "SELECT * FROM global_config;";

        try {
            c = hikari.getConnection();
            ps = c.prepareStatement(query);
            rs = ps.executeQuery();

            while(rs.next()){
                Constants.config.put(rs.getString(1), rs.getString(2));
            }
        } catch (SQLException e){
            e.printStackTrace();
        } finally {
            if(c != null){
                try {
                    c.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if(ps != null){
                try {
                    ps.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if(rs != null){
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void loadAuth(){
        Connection c = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        String query = "SELECT * FROM authorized_servers;";

        try {
            c = hikari.getConnection();
            ps = c.prepareStatement(query);
            rs = ps.executeQuery();

            while(rs.next()){
                CreateServerListener.authorized.add(rs.getString(1));
            }
        } catch (SQLException e){
            e.printStackTrace();
        } finally {
            if(c != null){
                try {
                    c.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if(ps != null){
                try {
                    ps.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if(rs != null){
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void loadBans(){
        Connection c = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        String query = "SELECT * FROM banned_users;";

        try {
            c = hikari.getConnection();
            ps = c.prepareStatement(query);
            rs = ps.executeQuery();

            while(rs.next()){
                SkuddbotTwitch.bannedUsers.add(rs.getString(1));
            }
        } catch (SQLException e){
            e.printStackTrace();
        } finally {
            if(c != null){
                try {
                    c.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if(ps != null){
                try {
                    ps.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if(rs != null){
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void banUser(String name){
        Connection c = null;
        PreparedStatement ps = null;

        String query = "INSERT INTO banned_users VALUES(?);";

        try {
            c = hikari.getConnection();
            ps = c.prepareStatement(query);

            ps.setString(1, name);

            ps.execute();
        } catch (SQLException e){
            e.printStackTrace();
        } finally {
            if(c != null){
                try {
                    c.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if(ps != null){
                try {
                    ps.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Send a request to the database to add a awesome user to the database.
     *
     * @param id The Discord ID associated with the person we want to add.
     */
    public static void addAwesome(String id){
        Connection c = null;
        PreparedStatement ps = null;

        String query = "INSERT INTO awesome_users VALUES(?,?,null);";

        try {
            c = hikari.getConnection();
            ps = c.prepareStatement(query);

            ps.setString(1, id);
            ps.setString(2, Main.getInstance().getSkuddbot().getUserByID(id).getName());

            ps.execute();
        } catch (SQLException e){
            e.printStackTrace();
        } finally {
            if(c != null){
                try {
                    c.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if(ps != null){
                try {
                    ps.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * This will update the ping message in the database for the specified user.
     *
     * @param id The ID of the user.
     * @param ping The message.
     */
    public static void updateAwesome(String id, String ping){ //UPDATE `awesome_users` SET `ping` = 'It''s lit.' WHERE `awesome_users`.`id` = '148376320726794240';
            Connection c = null;
            PreparedStatement ps = null;

            String query = "UPDATE awesome_users SET ping=? WHERE id=?;";

            try {
                c = hikari.getConnection();
                ps = c.prepareStatement(query);

                ps.setString(1, ping);
                ps.setString(2, id);

                ps.execute();
            } catch (SQLException e){
                e.printStackTrace();
            } finally {
                if(c != null){
                    try {
                        c.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
                if(ps != null){
                    try {
                        ps.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
    }

    /**
     * Send a request to the database to remove a awesome user form the database.
     *
     * @param id The Discord ID associated with the person we want to remove.
     */
    public static void removeAwesome(String id){
        Connection c = null;
        PreparedStatement ps = null;

        String query = "DELETE FROM awesome_users WHERE id=?;";

        try {
            c = hikari.getConnection();
            ps = c.prepareStatement(query);

            ps.setString(1, id);

            ps.execute();
        } catch (SQLException e){
            e.printStackTrace();
        } finally {
            if(c != null){
                try {
                    c.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if(ps != null){
                try {
                    ps.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * This loads all awesome ID's (users) from the database and adds them to the ArrayList.
     */
    public static void loadAwesomeUsers(){
        Connection c = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        String query = "SELECT * FROM awesome_users;";

        try {
            c = hikari.getConnection();
            ps = c.prepareStatement(query);
            rs = ps.executeQuery();

            while(rs.next()){
                Constants.awesomeUser.add(rs.getString(1));
                if(rs.getString(3) != null) {
                    Constants.awesomePing.put(rs.getString(1), rs.getString(3));
                }
            }
        } catch (SQLException e){
            e.printStackTrace();
        } finally {
            if(c != null){
                try {
                    c.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if(ps != null){
                try {
                    ps.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if(rs != null){
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Loads all awesome data from the database and adds them to the HashMap.
     */
    public static void loadAwesomeData(){
        Connection c = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        String query = "SELECT * FROM awesome_data;";

        try {
            c = hikari.getConnection();
            ps = c.prepareStatement(query);
            rs = ps.executeQuery();

            while(rs.next()){
                Constants.awesomeStrings.put(rs.getString(4), DataTypes.valueOf(rs.getString(3).toUpperCase()));
            }
        } catch (SQLException e){
            e.printStackTrace();
        } finally {
            if(c != null){
                try {
                    c.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if(ps != null){
                try {
                    ps.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if(rs != null){
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Adds a awesome string to the database!
     *
     * @param dataType Message type.
     * @param message The message itself.
     * @param id The ID of the user that added it.
     */
    public static void addAwesomeString(DataTypes dataType, String message, String id){
        Connection c = null;
        PreparedStatement ps = null;

        String query = "INSERT INTO awesome_data VALUES(null,?,?,?);";

        try {
            c = hikari.getConnection();
            ps = c.prepareStatement(query);

            ps.setString(1, id);
            ps.setString(2, dataType.toString());
            ps.setString(3, message);

            ps.execute();
        } catch (SQLException e){
            e.printStackTrace();
        } finally {
            if(c != null){
                try {
                    c.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if(ps != null){
                try {
                    ps.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Send a request to the database to add a Admin user to the database.
     *
     * @param id The Discord ID associated with the person we want to add.
     */
    public static void addAdmin(String id){
        Connection c = null;
        PreparedStatement ps = null;

        String query = "INSERT INTO admin_users VALUES(?);";

        try {
            c = hikari.getConnection();
            ps = c.prepareStatement(query);

            ps.setString(1, id);

            ps.execute();
        } catch (SQLException e){
            e.printStackTrace();
        } finally {
            if(c != null){
                try {
                    c.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if(ps != null){
                try {
                    ps.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Send a request to the database to remove a admin user form the database.
     *
     * @param id The Discord ID associated with the person we want to remove.
     */
    public static void removeAdmin(String id){
        Connection c = null;
        PreparedStatement ps = null;

        String query = "DELETE FROM admin_users WHERE id=?;;";

        try {
            c = hikari.getConnection();
            ps = c.prepareStatement(query);

            ps.setString(1, id);

            ps.execute();
        } catch (SQLException e){
            e.printStackTrace();
        } finally {
            if(c != null){
                try {
                    c.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if(ps != null){
                try {
                    ps.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * This loads all admin ID's (users) from the database and adds them to the ArrayList.
     */
    public static void loadAdmin(){
        Connection c = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        String query = "SELECT * FROM admin_users;";

        try {
            c = hikari.getConnection();
            ps = c.prepareStatement(query);
            rs = ps.executeQuery();

            while(rs.next()){
                Constants.adminUser.add(rs.getString(1));
            }
        } catch (SQLException e){
            e.printStackTrace();
        } finally {
            if(c != null){
                try {
                    c.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if(ps != null){
                try {
                    ps.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if(rs != null){
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }


}
