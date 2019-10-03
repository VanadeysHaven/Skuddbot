package me.Cooltimmetje.Skuddbot.Profiles;

import com.zaxxer.hikari.HikariDataSource;
import me.Cooltimmetje.Skuddbot.Enums.DataTypes;
import me.Cooltimmetje.Skuddbot.Enums.UserStats.UserStats;
import me.Cooltimmetje.Skuddbot.Listeners.CreateServerListener;
import me.Cooltimmetje.Skuddbot.Main;
import me.Cooltimmetje.Skuddbot.Utilities.Constants;
import me.Cooltimmetje.Skuddbot.Utilities.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

/**
 * This class handles everything to do with the database, and contains all operations we can run on the database.
 *
 * @author Tim (Cooltimmetje)
 * @version v0.5-ALPHA
 * @since v0.1-ALPHA
 */

@SuppressWarnings("Duplicates")
public class MySqlManager {

    private static HikariDataSource hikari = null;
    static JSONParser parser = new JSONParser();

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

        String query = "SELECT * FROM discord WHERE server_id=? AND twitch_username=?;";

        try {
            c = hikari.getConnection();
            ps = c.prepareStatement(query);

            ps.setString(1, serverID);
            ps.setString(2, twitchUsername);

            rs = ps.executeQuery();
            if(rs.next()){
                user = new SkuddUser(rs.getString("discord_id"), serverID, rs.getString("discord_username"), rs.getInt("xp"), rs.getString("twitch_username"), rs.getString("settings"), rs.getString("userstats"));
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


        String query = "SELECT * FROM twitch WHERE server_id=? AND twitch_user=?;";

        try {
            c = hikari.getConnection();
            ps = c.prepareStatement(query);

            ps.setString(1, serverID);
            ps.setString(2, twitchUsername);

            rs = ps.executeQuery();
            if(rs.next()){
                user = new SkuddUser(null, serverID, null,rs.getInt("xp"), rs.getString("twitch_user"), rs.getString("settings"), rs.getString("userstats"));
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
        String create = "INSERT INTO twitch VALUES(?,?,?,?,?) ON DUPLICATE KEY UPDATE xp=?,settings=?,userstats=?;";

        try {
            c = hikari.getConnection();
            ps = c.prepareStatement(create);

            ps.setString(1, user.getServerID());
            ps.setString(2, user.getTwitchUsername());
            ps.setInt(3, user.getXp());
            ps.setString(4, user.jsonSettings());
            ps.setString(5, user.jsonStats());
            ps.setInt(6, user.getXp());
            ps.setString(7, user.jsonSettings());
            ps.setString(8, user.jsonStats());

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

        String query = "SELECT * FROM discord WHERE server_id=? AND discord_id=?;";

        try {
            c = hikari.getConnection();
            ps = c.prepareStatement(query);

            ps.setString(1, serverID);
            ps.setString(2, id);

            rs = ps.executeQuery();

            if(rs.next()){
                user = new SkuddUser(rs.getString("discord_id"),serverID, rs.getString("discord_username"), rs.getInt("xp"), rs.getString("twitch_username"), rs.getString("settings"), rs.getString("userstats"));
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
        String create = "INSERT INTO discord VALUES(?,?,?,?,?,?,?) ON DUPLICATE KEY UPDATE discord_username=?,xp=?,twitch_username=?,settings=?,userstats=?;";

        try {
            c = hikari.getConnection();
            ps = c.prepareStatement(create);

            ps.setString(1, user.getServerID());
            ps.setString(2, user.getId());
            ps.setString(3, (Main.getInstance().getSkuddbot().getUserByID(Long.parseLong(user.getId())) == null ? user.getName() : Main.getInstance().getSkuddbot().getUserByID(Long.parseLong(user.getId())).getName()));
            ps.setInt(4, user.getXp());
            ps.setString(5, user.getTwitchUsername());
            ps.setString(6, user.jsonSettings());
            ps.setString(7, user.jsonStats());
            ps.setString(8, (Main.getInstance().getSkuddbot().getUserByID(Long.parseLong(user.getId())) == null ? user.getName() : Main.getInstance().getSkuddbot().getUserByID(Long.parseLong(user.getId())).getName()));
            ps.setInt(9, user.getXp());
            ps.setString(10, user.getTwitchUsername());
            ps.setString(11, user.jsonSettings());
            ps.setString(12, user.jsonStats());

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
        String delete = "DELETE FROM twitch WHERE server_id=? AND twitch_user=?;";

        try {
            c = hikari.getConnection();
            ps = c.prepareStatement(delete);

            ps.setString(1, serverID);
            ps.setString(2, username);

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

    @SuppressWarnings("ConstantConditions")
    public static HashMap<Integer,SkuddUser> getTopDiscord(String serverID){
        HashMap<Integer,SkuddUser> top = new HashMap<>();

        Connection c = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        String query = "SELECT * FROM discord WHERE server_id=? ORDER BY xp DESC LIMIT 10;";

        try {
            c = hikari.getConnection();
            ps = c.prepareStatement(query);

            ps.setString(1, serverID);

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

        String query = "SELECT * FROM twitch WHERE server_id=? ORDER BY xp DESC LIMIT 10;";

        try {
            c = hikari.getConnection();
            ps = c.prepareStatement(query);

            ps.setString(1, serverID);

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
            if(rs.next()) {
                loaded = new Server(rs.getString(1), rs.getString(2));
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

        String query = "INSERT INTO servers VALUES(?,?) ON DUPLICATE KEY UPDATE settings=?";

        try {
            c = hikari.getConnection();
            ps = c.prepareStatement(query);

            ps.setString(1, server.getServerID());
            ps.setString(2, server.jsonSettings());
            ps.setString(3, server.jsonSettings());

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

    public static void loadGlobal(){
        Logger.info("Loading global settings...");
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
                Logger.info("Loaded setting \"" + rs.getString(1) + "\" with value \"" + rs.getString(2) + "\".");
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

    public static void saveGlobal(String entry, String value){
        Connection c = null;
        PreparedStatement ps = null;

        String query = "INSERT INTO global_config VALUES(?,?) ON DUPLICATE KEY UPDATE value=?";

        try {
            c = hikari.getConnection();
            ps = c.prepareStatement(query);

            ps.setString(1, entry);
            ps.setString(2, value);
            ps.setString(3, value);

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

    public static void loadAuth(){
        Logger.info("Loading server authorization...");
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
                Logger.info("Server ID " + rs.getString(1) + " is authorized to use Skuddbot.");
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
        Logger.info("Loading XP bans...");
        Connection c = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        String query = "SELECT * FROM banned_users;";

        try {
            c = hikari.getConnection();
            ps = c.prepareStatement(query);
            rs = ps.executeQuery();

            while(rs.next()){
                Constants.bannedUsers.add(rs.getString(1));
                Logger.info("Twitch user " + rs.getString(1) + " is banned from gaining XP.");
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
            ps.setString(2, Main.getInstance().getSkuddbot().getUserByID(Long.parseLong(id)).getName());

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
        Logger.info("Loading donators...");
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
                Logger.info("User ID " + rs.getString(1) + " is an donator.");
                if(rs.getString(3) != null) {
                    Constants.awesomePing.put(rs.getString(1), rs.getString(3));
                    Logger.info("Loaded ping message \"" + rs.getString(3) + "\" for User ID " + rs.getString(1) +".");
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
        Logger.info("Loading donator data...");
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
                Logger.info("Loaded donator message \"" + rs.getString(4) + "\" with type " + rs.getString(3).toUpperCase() + ".");
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

        String query = "DELETE FROM admin_users WHERE id=?;";

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
        Logger.info("Loading admins...");
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
                Logger.info("User ID " + rs.getString(1) + " is an admin.");
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
     * Adds a whitelisted command to the database.
     *
     * @param command The command that needs to be whitelisted.
     */
    public static void addWhitelistCommand(String command){
        Connection c = null;
        PreparedStatement ps = null;

        String query = "INSERT INTO whitelisted_commands VALUES(?);";

        try {
            c = hikari.getConnection();
            ps = c.prepareStatement(query);

            ps.setString(1, command);

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
     * Removes a whitelisted command from the database.
     *
     * @param command The command that needs to be un-whitelisted.
     */
    public static void removeWhitelistedCommand(String command){
        Connection c = null;
        PreparedStatement ps = null;

        String query = "DELETE FROM whitelisted_commands WHERE command=?;";

        try {
            c = hikari.getConnection();
            ps = c.prepareStatement(query);

            ps.setString(1, command);

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
     * This loads all whitelisted commands from the database and adds them to the ArrayList.
     */
    public static void loadWhitelistedCommands(){
        Logger.info("Loading whitelisted commands...");
        Connection c = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        String query = "SELECT * FROM whitelisted_commands;";

        try {
            c = hikari.getConnection();
            ps = c.prepareStatement(query);
            rs = ps.executeQuery();

            while(rs.next()){
                Constants.whitelistedCommands.add(rs.getString(1));
                Logger.info("Command " + rs.getString(1) + " is whitelisted for use with !reverse.");
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

    public static HashMap<String,Integer> getStats(UserStats stat, String serverId){
        HashMap<String,Integer> result = new HashMap<>();
        Logger.info("Loading all " + stat.toString() + " stats for server " + Main.getInstance().getSkuddbot().getGuildByID(Long.parseLong(serverId)).getName() + " (ID: " + serverId + ")");
        Connection c = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        String queryDiscord = "SELECT discord_id,userstats FROM discord WHERE server_id=? AND userstats LIKE ?;";
        String queryTwitch = "SELECT twitch_user,userstats FROM twitch WHERE server_id=? AND userstats LIKE ?;";

        try {
            c = hikari.getConnection();
            ps = c.prepareStatement(queryDiscord);

            ps.setString(1, serverId);
            ps.setString(2, "%" + stat.getJsonReference() + "%");

            rs = ps.executeQuery();

            while(rs.next()){
                JSONObject object = (JSONObject) parser.parse(rs.getString("userstats"));
                int statValue = Integer.parseInt(String.valueOf(object.get(stat.getJsonReference())));
                String id = rs.getString("discord_id");

                Logger.info("Loaded stat value " + statValue + " for user ID " + id);
                result.put(id, statValue);
            }

            ps = c.prepareStatement(queryTwitch);

            ps.setString(1, serverId);
            ps.setString(2, "%" + stat.getJsonReference() + "%");

            rs = ps.executeQuery();

            while (rs.next()){
                JSONObject object = (JSONObject) parser.parse(rs.getString("userstats"));
                int statValue = Integer.parseInt(String.valueOf(object.get(stat.getJsonReference())));
                String userName = rs.getString("twitch_user");

                Logger.info("Loaded stat value " + statValue + " for username " + userName);
                result.put(userName, statValue);
            }

        } catch (SQLException | ParseException e) {
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

        return result;
    }

    public static void loadCommands(String serverId){
        Logger.info("Loading all commands for server ID: " + serverId);
        Server server = ServerManager.getServer(serverId);
        Connection c = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        String query = "SELECT * FROM commands WHERE server_id=?;";

        try {
            c = hikari.getConnection();
            ps = c.prepareStatement(query);

            ps.setString(1, serverId);

            rs = ps.executeQuery();

            while(rs.next()){
                server.loadCommand(rs.getString("invoker"), rs.getString("output"), rs.getString("metadata"), rs.getString("properties"));
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

    public static void createNewCommand(String serverId, String invoker, String output, String metadata) {
        Connection c = null;
        PreparedStatement ps = null;

        String query = "INSERT INTO commands(server_id, invoker, output, metadata) VALUES (?, ?, ?, ?);";

        try {
            c = hikari.getConnection();
            ps = c.prepareStatement(query);

            ps.setString(1, serverId);
            ps.setString(2, invoker);
            ps.setString(3, output);
            ps.setString(4, metadata);

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

    public static void saveCommand(String serverId, String invoker, String output, String metadata, String properties) {
        Connection c = null;
        PreparedStatement ps = null;

        String query = "UPDATE commands SET output=?, metadata=?, properties=? WHERE server_id=? AND invoker=?";

        try {
            c = hikari.getConnection();
            ps = c.prepareStatement(query);

            ps.setString(1, output);
            ps.setString(2, metadata);
            ps.setString(3, properties);
            ps.setString(4, serverId);
            ps.setString(5, invoker);

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

    public static void editInvoker(String serverId, String oldInvoker, String newInvoker) {
        Connection c = null;
        PreparedStatement ps = null;

        String query = "UPDATE commands SET invoker=? WHERE server_id=? AND invoker=?";

        try {
            c = hikari.getConnection();
            ps = c.prepareStatement(query);

            ps.setString(1, newInvoker);
            ps.setString(2, serverId);
            ps.setString(3, oldInvoker);

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

}
