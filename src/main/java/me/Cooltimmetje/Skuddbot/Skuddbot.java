package me.Cooltimmetje.Skuddbot;

import me.Cooltimmetje.Skuddbot.Commands.CommandManager;
import me.Cooltimmetje.Skuddbot.Experience.XPGiver;
import me.Cooltimmetje.Skuddbot.Listeners.CreateServerListener;
import me.Cooltimmetje.Skuddbot.Listeners.JoinQuitListener;
import me.Cooltimmetje.Skuddbot.Listeners.TwitchLiveListener;
import me.Cooltimmetje.Skuddbot.Profiles.MySqlManager;
import me.Cooltimmetje.Skuddbot.Profiles.ServerManager;
import me.Cooltimmetje.Skuddbot.Utilities.Constants;
import me.Cooltimmetje.Skuddbot.Utilities.Logger;
import me.Cooltimmetje.Skuddbot.Utilities.MessagesUtils;
import me.Cooltimmetje.Skuddbot.Utilities.MiscUtils;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.impl.events.guild.channel.message.MentionEvent;
import sx.blah.discord.util.DiscordException;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Holds the Skuddbot instance.
 *
 * @author Tim (Cooltimmetje)
 * @version v0.4.01-ALPHA-DEV
 * @since v0.1-ALPHA
 */
public class Skuddbot {

    private volatile IDiscordClient skuddbot;
    private String token;
    private final AtomicBoolean reconnect = new AtomicBoolean(true);
    private boolean preReadyListenersReady = false;
    private boolean listenersReady = false;

    public Skuddbot(String token){
        this.token = token;
    }

    public void login() throws DiscordException {
        skuddbot = new ClientBuilder().withToken(token).setMaxReconnectAttempts(3).login();
        if(!preReadyListenersReady) {
            skuddbot.getDispatcher().registerListener(this);
            skuddbot.getDispatcher().registerListener(new CreateServerListener());

            preReadyListenersReady = true;
        }
    }

    @EventSubscriber
    public void onReady(ReadyEvent event){
        if(!listenersReady){
//            event.getClient().changeStatus(Status.game("Revolutionary and fun!"));
            MiscUtils.setPlaying();
            skuddbot.getDispatcher().registerListener(new CommandManager());
            skuddbot.getDispatcher().registerListener(new XPGiver());
            skuddbot.getDispatcher().registerListener(new JoinQuitListener());
            skuddbot.getDispatcher().registerListener(new TwitchLiveListener());
            skuddbot.getDispatcher().registerListener(new MessagesUtils());
            Main.getSkuddbotTwitch().joinChannels();

            listenersReady = true;
            Runtime.getRuntime().addShutdownHook(new Thread(() -> terminate(true)));
            MessagesUtils.sendPlain(":robot: Startup sequence complete!", Main.getInstance().getSkuddbot().getChannelByID(Constants.LOG_CHANNEL), false);
        }
    }

    @EventSubscriber
    public void onMention(MentionEvent event){
        if(event.getMessage().getContent().split(" ").length > 1) {
            if (event.getMessage().getContent().split(" ")[1].equalsIgnoreCase("logout")) {
                if (event.getMessage().getAuthor().getStringID().equals(Constants.TIMMY_OVERRIDE) || event.getMessage().getAuthor().getStringID().equals(Constants.JASCH_OVERRIDE)) {
                    MessagesUtils.sendSuccess("Well, okay then...\n`Shutting down...`", event.getMessage().getChannel());

                    terminate(false);
                } else {
                    MessagesUtils.sendError("Ur not timmy >=(", event.getMessage().getChannel());
                }
            }
        }
    }

    public void terminate(boolean sigterm) {
        if(sigterm){
            MessagesUtils.sendPlain(":robot: Logging out due to SIGTERM...", Main.getInstance().getSkuddbot().getChannelByID(Constants.LOG_CHANNEL), false);
        } else {
            MessagesUtils.sendPlain(":robot: Logging out due to command...", Main.getInstance().getSkuddbot().getChannelByID(Constants.LOG_CHANNEL), false);
        }
        reconnect.set(false);
        try {
            Main.getSkuddbotTwitch().leaveChannels();
            Main.stopTimer();
            ServerManager.saveAll(false);
            Main.getSkuddbotTwitch().terminate();
            MySqlManager.disconnect();
            skuddbot.logout();
        } catch (DiscordException e) {
            Logger.warn("Couldn't log out.", e);
        }

    }

    public IDiscordClient getSkuddbot(){
        return skuddbot;
    }



}