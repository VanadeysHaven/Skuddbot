package me.Cooltimmetje.Skuddbot;

import me.Cooltimmetje.Skuddbot.Cleverbot.CleverbotManager;
import me.Cooltimmetje.Skuddbot.Profiles.MySqlManager;
import me.Cooltimmetje.Skuddbot.Utilities.ActivityChecker;
import me.Cooltimmetje.Skuddbot.Utilities.Constants;
import org.jibble.pircbot.IrcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.util.DiscordException;

import java.io.IOException;
import java.util.Timer;

/**
 * Created by Tim on 8/1/2016.
 */

public class Main {

    private static Skuddbot skuddbot;
    private static CleverbotManager cleverskudd;
    private static final Logger log = LoggerFactory.getLogger(Main.class);
    private static SkuddbotTwitch skuddbotTwitch;
    private static Timer timer = new Timer();

    public static void main(String[] args){

        if(args.length < 5){
            throw new IllegalArgumentException("I need a Discord Token, Mysql Username, Mysql Password, Twitch Username and Twitch Token.");
        } else {
            log.info("Setting up...");
            skuddbot = new Skuddbot(args[0]);

            MySqlManager.setupHikari(args[1],args[2]);
            Constants.twitchBot = args[3];
            Constants.twitchOauth = args[4];
        }
        log.info("Connecting to Twitch.");

        skuddbotTwitch = new SkuddbotTwitch();

        try {
            skuddbotTwitch.connect("irc.twitch.tv", 6667, Constants.twitchOauth);
        } catch (IOException | IrcException e) {
            e.printStackTrace();
        }

        MySqlManager.loadAuth();
        MySqlManager.loadBans();
        MySqlManager.loadAdmin();
        MySqlManager.loadAwesome();
        log.info("All systems operational. Ready to connect to Discord.");
        try {
            skuddbot.login();
        } catch (DiscordException e) {
            e.printStackTrace();
        }
        cleverskudd = new CleverbotManager();

        timer.schedule(new ActivityChecker(), Constants.INACTIVE_DELAY, Constants.INACTIVE_DELAY);
        Constants.STARTUP_TIME = System.currentTimeMillis();
        MySqlManager.loadGlobal();
    }

    public static Skuddbot getInstance(){
        return skuddbot;
    }

    public static void stopTimer(){
        timer.cancel();
    }

    public static CleverbotManager getCleverskudd(){
        return cleverskudd;
    }

    public static SkuddbotTwitch getSkuddbotTwitch(){
        return skuddbotTwitch;
    }

}
