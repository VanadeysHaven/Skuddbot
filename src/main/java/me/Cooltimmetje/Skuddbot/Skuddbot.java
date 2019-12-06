package me.Cooltimmetje.Skuddbot;

import discord4j.core.DiscordClient;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.event.domain.guild.GuildCreateEvent;
import discord4j.core.event.domain.guild.MemberJoinEvent;
import discord4j.core.event.domain.guild.MemberLeaveEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.event.domain.message.ReactionAddEvent;
import me.Cooltimmetje.Skuddbot.Commands.CommandManager;
import me.Cooltimmetje.Skuddbot.Enums.EmojiEnum;
import me.Cooltimmetje.Skuddbot.Experience.XPGiver;
import me.Cooltimmetje.Skuddbot.Listeners.CreateServerListener;
import me.Cooltimmetje.Skuddbot.Listeners.JoinQuitListener;
import me.Cooltimmetje.Skuddbot.Listeners.TwitchLiveListener;
import me.Cooltimmetje.Skuddbot.Minigames.Blackjack.BlackjackManager;
import me.Cooltimmetje.Skuddbot.Minigames.Challenge.ChallengeManager;
import me.Cooltimmetje.Skuddbot.Minigames.FreeForAll.FFAManager;
import me.Cooltimmetje.Skuddbot.Minigames.TeamDeathmatch.TdManager;
import me.Cooltimmetje.Skuddbot.Profiles.MySqlManager;
import me.Cooltimmetje.Skuddbot.Profiles.ServerManager;
import me.Cooltimmetje.Skuddbot.Utilities.*;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Holds the Skuddbot instance.
 *
 * @author Tim (Cooltimmetje)
 * @version v0.5.1-ALPHA
 * @since v0.1-ALPHA
 */
public class Skuddbot {

    private volatile DiscordClient skuddbot;
    private String token;
    private final AtomicBoolean reconnect = new AtomicBoolean(true);
    private boolean listenersReady = false;

    public Skuddbot(String token){
        this.token = token;
    }

    public void login() {
        skuddbot = new DiscordClientBuilder(token).build();
        registerListeners();
        skuddbot.login().block();
    }

    public void registerListeners(){
        if(!listenersReady){
            MiscUtils.setPlaying(true);

            skuddbot.getEventDispatcher().on(GuildCreateEvent.class).subscribe(CreateServerListener::onCreate);
            skuddbot.getEventDispatcher().on(ReactionAddEvent.class).subscribe(MessagesUtils::onReaction);
            skuddbot.getEventDispatcher().on(MessageCreateEvent.class).subscribe(CommandManager::onMessage);
            skuddbot.getEventDispatcher().on(MessageCreateEvent.class).subscribe(XPGiver::onMessage);
            skuddbot.getEventDispatcher().on(MemberJoinEvent.class).subscribe(JoinQuitListener::onJoin);
            skuddbot.getEventDispatcher().on(MemberLeaveEvent.class).subscribe(JoinQuitListener::onLeave);
            skuddbot.getEventDispatcher().on(MessageCreateEvent.class).subscribe(TwitchLiveListener::onMessage);
            skuddbot.getEventDispatcher().on(ReactionAddEvent.class).subscribe(ChallengeManager::onReaction);


            skuddbot.getDispatcher().registerListeners(new FFAManager(), new BlackjackManager(), new TdManager());

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
                    MessagesUtils.addReaction(event.getMessage(),null, EmojiEnum.WHITE_CHECK_MARK, false);

                    terminate(false);
                } else {
                    MessagesUtils.addReaction(event.getMessage(),"Ur not timmy >=(", EmojiEnum.X, false);
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
            ServerManager.saveAll();
            Main.getSkuddbotTwitch().terminate();
            MySqlManager.disconnect();
            skuddbot.logout();
        } catch (DiscordException e) {
            Logger.warn("Couldn't log out.", e);
        }

    }

    public DiscordClient getSkuddbot(){
        return skuddbot;
    }



}