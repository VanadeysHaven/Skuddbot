package me.Cooltimmetje.Skuddbot;

import me.Cooltimmetje.Skuddbot.Cleverbot.MentionListener;
import me.Cooltimmetje.Skuddbot.Commands.CommandManager;
import me.Cooltimmetje.Skuddbot.Enums.DataTypes;
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
import sx.blah.discord.handle.impl.events.DiscordDisconnectedEvent;
import sx.blah.discord.handle.impl.events.MentionEvent;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.obj.Status;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.RateLimitException;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Tim on 8/2/2016.
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
        skuddbot = new ClientBuilder().withToken(token).login();
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
            Main.getInstance().getSkuddbot().changeStatus(Status.game(MiscUtils.getRandomMessage(DataTypes.PLAYING)));
            skuddbot.getDispatcher().registerListener(new MentionListener());
            skuddbot.getDispatcher().registerListener(new CommandManager());
            skuddbot.getDispatcher().registerListener(new XPGiver());
            skuddbot.getDispatcher().registerListener(new JoinQuitListener());
            skuddbot.getDispatcher().registerListener(new TwitchLiveListener());
            Main.getSkuddbotTwitch().joinChannels();

            listenersReady = true;
        }
    }

    @EventSubscriber
    public void onDisconnect(DiscordDisconnectedEvent event){
        CompletableFuture.runAsync(() -> {
            if(reconnect.get()) {
                Logger.info("Attempting to reconnect bot...");
                try {
                    login();
                } catch (DiscordException e) {
                    Logger.warn("Well rip.", e);
                }
            }
        });
    }

    @EventSubscriber
    public void onMention(MentionEvent event){
        if(event.getMessage().getContent().split(" ").length > 1) {
            if (event.getMessage().getContent().split(" ")[1].equalsIgnoreCase("logout")) {
                if (event.getMessage().getAuthor().getID().equals(Constants.TIMMY_OVERRIDE)) {
                    MessagesUtils.sendSuccess("Well, okay then...\n`Shutting down...`", event.getMessage().getChannel());
                    Main.stopTimer();
                    ServerManager.saveAll();
                    Main.getSkuddbotTwitch().terminate();
                    MySqlManager.disconnect();
                    terminate();
                } else {
                    MessagesUtils.sendError("Ur not timmy >=(", event.getMessage().getChannel());
                }
            }
        }
    }

    public void terminate() {
        reconnect.set(false);
        try {
            skuddbot.logout();
        } catch (DiscordException | RateLimitException e) {
            Logger.warn("Couldn't log out.", e);
        }

    }

    public IDiscordClient getSkuddbot(){
        return skuddbot;
    }



}