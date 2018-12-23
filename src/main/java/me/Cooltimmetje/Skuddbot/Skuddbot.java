package me.Cooltimmetje.Skuddbot;

import me.Cooltimmetje.Skuddbot.Commands.CommandManager;
import me.Cooltimmetje.Skuddbot.Enums.EmojiEnum;
import me.Cooltimmetje.Skuddbot.Experience.XPGiver;
import me.Cooltimmetje.Skuddbot.Listeners.CreateServerListener;
import me.Cooltimmetje.Skuddbot.Listeners.JoinQuitListener;
import me.Cooltimmetje.Skuddbot.Listeners.TwitchLiveListener;
import me.Cooltimmetje.Skuddbot.Minigames.Blackjack.BlackjackManager;
import me.Cooltimmetje.Skuddbot.Minigames.Challenge.ChallengeManager;
import me.Cooltimmetje.Skuddbot.Minigames.FreeForAll.FFAManager;
import me.Cooltimmetje.Skuddbot.Profiles.MySqlManager;
import me.Cooltimmetje.Skuddbot.Profiles.ServerManager;
import me.Cooltimmetje.Skuddbot.ServerSpecific.PogoGravesend.RoleAdder;
import me.Cooltimmetje.Skuddbot.Utilities.*;
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
 * @version v0.4.32-ALPHA
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
            skuddbot.getDispatcher().registerListeners(this, new CreateServerListener());

            preReadyListenersReady = true;
        }
    }

    @EventSubscriber
    public void onReady(ReadyEvent event){
        if(!listenersReady){
            MiscUtils.setPlaying(true);

            skuddbot.getDispatcher().registerListeners(new CommandManager(), new XPGiver(), new JoinQuitListener(), new TwitchLiveListener(), new MessagesUtils(),
            new ChallengeManager(), new RoleAdder(), new FFAManager(), new BlackjackManager());

            Main.getSkuddbotTwitch().joinChannels();
            EmojiHelper.loadEmoji();

            listenersReady = true;
            Runtime.getRuntime().addShutdownHook(new Thread(() -> terminate(true)));
            if(Main.getInstance().getSkuddbot().getOurUser().getStringID().equals("209779500018434058")) {
                MessagesUtils.sendPlain(":robot: Startup sequence complete!\n\n" +
                                "**Status:**\n```\n" +
                                "> Discord | Connected | Logged in as: " + this.getSkuddbot().getOurUser().getName() + "#" + this.getSkuddbot().getOurUser().getDiscriminator() + " / ID: " + this.getSkuddbot().getOurUser().getStringID() + "\n" +
                                "> Twitch  | Connected | Logged in as: " + Constants.STARTUP_ARGUMENTS[3] + "\n" +
                                "> MySQL   | Connected | Logged in as: " + Constants.STARTUP_ARGUMENTS[1] + "\n```\n" +
                                "**Build:**\n```\n" +
                                "> Built:   " + Constants.config.get("built_on") + " | Deployed: " + Constants.config.get("deployed_on") + "\n" +
                                "> Version: " + Constants.config.get("version") + " | " + Constants.config.get("branch") + " > " + Constants.config.get("deployed_from") + "\n```",
                        Main.getInstance().getSkuddbot().getChannelByID(Constants.LOG_CHANNEL), false);
            }
        }
    }

    @EventSubscriber
    public void onMention(MentionEvent event){
        if(event.getMessage().getContent().split(" ").length > 1) {
            if (event.getMessage().getContent().split(" ")[1].equalsIgnoreCase("logout")) {
                if (event.getMessage().getAuthor().getLongID() == Constants.TIMMY_ID || event.getMessage().getAuthor().getLongID() == Constants.JASCH_ID) {
                    MessagesUtils.addReaction(event.getMessage(),null, EmojiEnum.WHITE_CHECK_MARK);

                    terminate(false);
                } else {
                    MessagesUtils.addReaction(event.getMessage(),"Ur not timmy >=(", EmojiEnum.X);
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