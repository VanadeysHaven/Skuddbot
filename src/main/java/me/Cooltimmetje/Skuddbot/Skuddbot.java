package me.Cooltimmetje.Skuddbot;

import discord4j.core.DiscordClient;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.event.domain.guild.GuildCreateEvent;
import discord4j.core.event.domain.guild.MemberJoinEvent;
import discord4j.core.event.domain.guild.MemberLeaveEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.event.domain.message.ReactionAddEvent;
import discord4j.core.event.domain.message.ReactionRemoveEvent;
import discord4j.core.object.entity.MessageChannel;
import discord4j.core.object.util.Snowflake;
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
import me.Cooltimmetje.Skuddbot.Utilities.Constants;
import me.Cooltimmetje.Skuddbot.Utilities.EmojiHelper;
import me.Cooltimmetje.Skuddbot.Utilities.MessagesUtils;
import me.Cooltimmetje.Skuddbot.Utilities.MiscUtils;

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
            skuddbot.getEventDispatcher().on(ReactionAddEvent.class).subscribe(FFAManager::onReaction);
            skuddbot.getEventDispatcher().on(ReactionRemoveEvent.class).subscribe(FFAManager::onReactionRemove);
            skuddbot.getEventDispatcher().on(ReactionAddEvent.class).subscribe(BlackjackManager::onReaction);
            skuddbot.getEventDispatcher().on(ReactionAddEvent.class).subscribe(TdManager::onReaction);
            skuddbot.getEventDispatcher().on(MessageCreateEvent.class).filter(msg -> msg.getMessage().getUserMentions().collectList().block().contains(skuddbot.getSelf().block())).subscribe(this::onMention);

            Main.getSkuddbotTwitch().joinChannels();
            EmojiHelper.loadEmoji();

            listenersReady = true;
            Runtime.getRuntime().addShutdownHook(new Thread(() -> terminate(true)));
            if(Main.getInstance().getSkuddbot().getSelf().block().getId().asString().equals("209779500018434058")) {
                MessagesUtils.sendPlain(":robot: Startup sequence complete!\n\n" +
                                "**Status:**\n```\n" +
                                "> Discord | Connected | Logged in as: " + this.getSkuddbot().getSelf().block().getUsername() + "#" + this.getSkuddbot().getSelf().block().getDiscriminator() + " / ID: " + this.getSkuddbot().getSelf().block().getId().asString() + "\n" +
                                "> Twitch  | Connected | Logged in as: " + Constants.STARTUP_ARGUMENTS[3] + "\n" +
                                "> MySQL   | Connected | Logged in as: " + Constants.STARTUP_ARGUMENTS[1] + "\n```\n" +
                                "**Build:**\n```\n" +
                                "> Built:   " + Constants.config.get("built_on") + " | Deployed: " + Constants.config.get("deployed_on") + "\n" +
                                "> Version: " + Constants.config.get("version") + " | " + Constants.config.get("branch") + " > " + Constants.config.get("deployed_from") + "\n```",
                        (MessageChannel) getSkuddbot().getChannelById(Snowflake.of(Constants.LOG_CHANNEL)).block(), false);
            }
        }
    }

    public void onMention(MessageCreateEvent event){
        if(event.getMessage().getContent().get().split(" ").length > 1) {
            if (event.getMessage().getContent().get().split(" ")[1].equalsIgnoreCase("logout")) {
                if (event.getMessage().getAuthor().get().getId().asLong() == Constants.TIMMY_ID || event.getMessage().getAuthor().get().getId().asLong() == Constants.JASCH_ID) {
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
            MessagesUtils.sendPlain(":robot: Logging out due to SIGTERM...", (MessageChannel) Main.getInstance().getSkuddbot().getChannelById(Snowflake.of(Constants.LOG_CHANNEL)).block(), false);
        } else {
            MessagesUtils.sendPlain(":robot: Logging out due to command...", (MessageChannel) Main.getInstance().getSkuddbot().getChannelById(Snowflake.of(Constants.LOG_CHANNEL)).block(), false);
        }
        reconnect.set(false);

        Main.getSkuddbotTwitch().leaveChannels();
        Main.stopTimer();
        ServerManager.saveAll();
        Main.getSkuddbotTwitch().terminate();
        MySqlManager.disconnect();
        skuddbot.logout().block();
    }

    public DiscordClient getSkuddbot(){
        return skuddbot;
    }



}