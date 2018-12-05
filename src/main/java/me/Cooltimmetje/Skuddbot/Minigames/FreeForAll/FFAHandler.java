package me.Cooltimmetje.Skuddbot.Minigames.FreeForAll;

import com.vdurmont.emoji.EmojiManager;
import me.Cooltimmetje.Skuddbot.Enums.EmojiEnum;
import me.Cooltimmetje.Skuddbot.Profiles.ProfileManager;
import me.Cooltimmetje.Skuddbot.Profiles.Server;
import me.Cooltimmetje.Skuddbot.Profiles.ServerManager;
import me.Cooltimmetje.Skuddbot.Profiles.SkuddUser;
import me.Cooltimmetje.Skuddbot.Utilities.*;
import sx.blah.discord.handle.impl.events.guild.channel.message.reaction.ReactionAddEvent;
import sx.blah.discord.handle.impl.events.guild.channel.message.reaction.ReactionRemoveEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.RequestBuffer;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
/**
 * This class handles the FFA command on a per-server basis.
 *
 * @author Tim (Cooltimmetje)
 * @version v0.4.4-ALPHA
 * @since v0.4.4-ALPHA
 */
public class FFAHandler {

    private String serverID;

    public FFAHandler(String serverID){
        this.serverID = serverID;
        Logger.info("Creating FFA handler for Server with ID: " + serverID);
    }

    private int xpReward = 50;
    private int cooldown = 300;

    HashMap<IUser,Long> cooldowns = new HashMap<>();
    private ArrayList<IUser> entrants = new ArrayList<>();

    private IUser host;
    private IMessage messageSent;
    private IMessage messageHost;

    void enter(IMessage message){
        String[] args = message.getContent().split(" ");
        if(cooldowns.containsKey(message.getAuthor())){
            if((System.currentTimeMillis() - cooldowns.get(message.getAuthor())) < (cooldown * 1000)){
                MessagesUtils.addReaction(message, "Hold on there, **" + message.getAuthor().mention() + "**, you're still wounded from the last fight.", EmojiEnum.HOURGLASS_FLOWING_SAND);
                return;
            }
        }
        if(message.getAuthor() == host){
            RequestBuffer.request(message::delete);
            if(args.length > 1){
                if(args[1].equalsIgnoreCase("-start")){
                    startFight(message.getChannel());
                }
            }
        }

        if(host == null) {
            host = message.getAuthor();
            messageHost = message;
            messageSent = MessagesUtils.sendPlain(MessageFormat.format("{0} **{1}** is looking to host a free for all fight, anyone can participate!\n" +
                            "Click the {0} reaction to enter, {1} can start the fight by clicking the {2} reaction.",
                    EmojiEnum.CROSSED_SWORDS.getString(), message.getAuthor().getDisplayName(message.getGuild()), EmojiEnum.WHITE_CHECK_MARK.getString()),
                    message.getChannel(), false);

            RequestBuffer.request(() -> {
                messageSent.addReaction(EmojiManager.getForAlias(EmojiEnum.CROSSED_SWORDS.getAlias()));
                messageSent.addReaction(EmojiManager.getForAlias(EmojiEnum.WHITE_CHECK_MARK.getAlias()));
            });
            entrants.add(message.getAuthor());
        } else {
            RequestBuffer.request(message::delete);
        }
    }

    void reactionAdd(ReactionAddEvent event){
        Logger.info(MessageFormat.format("FFA Reaction | Server ID: {0} | Reaction: {1} | User: {2}#{3}",
                serverID, event.getReaction().getEmoji().getName(), event.getUser().getName(), event.getUser().getDiscriminator()));
        if(event.getUser().isBot()){
            Logger.info("Reaction is from a bot, stopping...");
            return;
        }
        if(event.getMessage() != messageSent){
            Logger.info("Reaction is on an invalid message, stopping...");
            return;
        }
        String unicodeEmoji = event.getReaction().getEmoji().getName();

        if(EmojiEnum.getByUnicode(unicodeEmoji) == EmojiEnum.WHITE_CHECK_MARK){
            if(event.getUser() != host){
                Logger.info("This user may not trigger this reaction, stopping...");
                return;
            }

            if(entrants.size() > 1){
                Logger.info("Starting fight...");
                startFight(event.getChannel());
            } else {
                Logger.info("Not enough entrants, stopping...");
                RequestBuffer.request(() -> messageSent.removeReaction(event.getUser(), EmojiManager.getForAlias(EmojiEnum.WHITE_CHECK_MARK.getAlias())));
            }
        } else if (EmojiEnum.getByUnicode(unicodeEmoji) == EmojiEnum.CROSSED_SWORDS){
            if(event.getUser() == host){
                Logger.info("This user may not trigger this reaction, removing reaction...");
                RequestBuffer.request(() -> event.getMessage().removeReaction(event.getUser(), EmojiManager.getForAlias(EmojiEnum.CROSSED_SWORDS.getAlias())));
                return;
            }

            if(!entrants.contains(event.getUser())) {
                Logger.info("Adding user to fight...");
                entrants.add(event.getUser());
            }
        } else if(EmojiEnum.getByUnicode(unicodeEmoji) == EmojiEnum.EYES){
            if(Constants.adminUser.contains(event.getUser().getStringID())){
                Logger.info("Admin user");
                if(entrants.size() > 1){
                    Logger.info("Starting fight...");
                    startFight(event.getChannel());
                } else {
                    Logger.info("Not enough entrants, stopping...");
                    messageSent.removeReaction(event.getUser(), EmojiManager.getForAlias(EmojiEnum.EYES.getAlias()));
                }
            }
        }
    }

    void reactionRemove(ReactionRemoveEvent event){
        if(event.getUser() == host){
            return;
        }
        if(event.getMessage() != messageSent){
            return;
        }
        if(EmojiEnum.getByUnicode(event.getReaction().getEmoji().getName()) != EmojiEnum.CROSSED_SWORDS){
            return;
        }

        entrants.remove(event.getUser());
    }

    private void startFight(IChannel channel) {
        int entrantsAmount = entrants.size();
        IGuild guild = channel.getGuild();
        IUser winner = entrants.get(MiscUtils.randomInt(0, entrantsAmount - 1));
        Server server = ServerManager.getServer(channel.getGuild().getStringID());
        ScheduledThreadPoolExecutor exec = new ScheduledThreadPoolExecutor(1);
        StringBuilder sbEntrants = new StringBuilder();
        for (IUser user : entrants) {
            if (entrants.indexOf(user) == (entrantsAmount - 1)) {
                sbEntrants.append("**").append(user.getDisplayName(channel.getGuild())).append("**");
            } else if (entrants.indexOf(user) == (entrantsAmount - 2)) {
                sbEntrants.append("**").append(user.getDisplayName(channel.getGuild())).append("** and ");
            } else {
                sbEntrants.append("**").append(user.getDisplayName(channel.getGuild())).append("**, ");
            }
        }
        String entrantsString = sbEntrants.toString();
        StringBuilder sbRewards = new StringBuilder();
        SkuddUser suWinner = ProfileManager.getDiscord(winner.getStringID(), guild.getStringID(), true);
        sbRewards.append(winner.getDisplayName(guild)).append(": *+").append(xpReward * entrantsAmount).append("* ").append(EmojiHelper.getEmoji("xp_icon"));
        if(suWinner.getFfaMostWin() < entrantsAmount){
            sbRewards.append(" - **New highest entrants win:** *").append(entrantsAmount).append(" people*");
        }
        String rewards = sbRewards.toString();

        messageHost.delete();
        messageSent.delete();

        MessagesUtils.sendPlain(MessageFormat.format("{0} {1} all go into {2} for an epic Free For All battle. Only one can emerge victorious! *3*... *2*... *1*... **FIGHT!**",
                EmojiEnum.CROSSED_SWORDS.getString(), entrantsString, server.getArenaName()),
                channel, false);
        channel.toggleTypingStatus();

        exec.schedule(() -> {
            MessagesUtils.sendPlain(MessageFormat.format("{0} A furious battle is going on in {1}, bodies are dropping... It looks like **{2}** has won the fight!\n\n{3}",
                    EmojiEnum.CROSSED_SWORDS.getString(), server.getArenaName(), winner.getDisplayName(channel.getGuild()), rewards), channel, false);

            suWinner.setXp(suWinner.getXp() + (xpReward * entrantsAmount));
            suWinner.setFfaWins(suWinner.getFfaWins() + 1);
            if(suWinner.getFfaMostWin() < entrantsAmount){
                suWinner.setFfaMostWin(entrantsAmount);
            }

            finishFight(winner, guild);
        }, 5, TimeUnit.SECONDS);

    }

    private void finishFight(IUser winner, IGuild guild){
        for(IUser user : entrants){
            cooldowns.put(user,System.currentTimeMillis());
            if(user != winner){
                SkuddUser suLoser = ProfileManager.getDiscord(user.getStringID(), guild.getStringID(), true);

                suLoser.setFfaLosses(suLoser.getFfaLosses() + 1);
            }
        }
        entrants.clear();

        host = null;
        messageSent = null;
        messageHost = null;
    }

}
