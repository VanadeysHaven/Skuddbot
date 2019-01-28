package me.Cooltimmetje.Skuddbot.Minigames.FreeForAll;

import com.vdurmont.emoji.EmojiManager;
import me.Cooltimmetje.Skuddbot.Enums.EmojiEnum;
import me.Cooltimmetje.Skuddbot.Profiles.ProfileManager;
import me.Cooltimmetje.Skuddbot.Profiles.Server;
import me.Cooltimmetje.Skuddbot.Profiles.ServerManager;
import me.Cooltimmetje.Skuddbot.Profiles.SkuddUser;
import me.Cooltimmetje.Skuddbot.Utilities.EmojiHelper;
import me.Cooltimmetje.Skuddbot.Utilities.Logger;
import me.Cooltimmetje.Skuddbot.Utilities.MessagesUtils;
import me.Cooltimmetje.Skuddbot.Utilities.MiscUtils;
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
 * @version v0.4.61-ALPHA
 * @since v0.4.4-ALPHA
 */
public class FFAHandler {

    private String serverID;

    public FFAHandler(String serverID){
        this.serverID = serverID;
        Logger.info("Creating FFA handler for Server with ID: " + serverID);
    }

    private int winReward = 100;
    private int killReward = 50;
    private int cooldown = 300;

    HashMap<IUser,Long> cooldowns = new HashMap<>();
    private ArrayList<IUser> entrants = new ArrayList<>();

    private IUser host;
    private long messageSent;
    private long messageHost;
    private boolean startReact;

    void enter(IMessage message){
        String[] args = message.getContent().split(" ");
        if(cooldowns.containsKey(message.getAuthor())){
            if((System.currentTimeMillis() - cooldowns.get(message.getAuthor())) < (cooldown * 1000)){
                MessagesUtils.addReaction(message, "Hold on there, **" + message.getAuthor().mention() + "**, you're still wounded from the last fight.", EmojiEnum.HOURGLASS_FLOWING_SAND, false);
                return;
            }
        }
        if(message.getAuthor() == host){
            if(args.length > 1){
                if(args[1].equalsIgnoreCase("-start")){
                    if(entrants.size() > 1) {
                        startFight(message.getChannel());
                        RequestBuffer.request(message::delete);
                    }
                }
            }
        }

        if(host == null) {
            host = message.getAuthor();
            messageHost = message.getLongID();
            messageSent = MessagesUtils.sendPlain(MessageFormat.format("{0} **{1}** is looking to host a free for all fight, anyone can participate!\n" +
                            "Click the {0} reaction to enter, {1} can start the fight by clicking the {2} reaction.",
                    EmojiEnum.CROSSED_SWORDS.getString(), message.getAuthor().getDisplayName(message.getGuild()), EmojiEnum.WHITE_CHECK_MARK.getString()),
                    message.getChannel(), false).getLongID();

            RequestBuffer.request(() -> MessagesUtils.getMessageByID(messageSent).addReaction(EmojiManager.getForAlias(EmojiEnum.CROSSED_SWORDS.getAlias())));
            entrants.add(message.getAuthor());
            startReact = false;
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
        if(event.getMessage().getLongID() != messageSent){
            Logger.info("Reaction is on an invalid message, stopping...");
            return;
        }
        String unicodeEmoji = event.getReaction().getEmoji().getName();

        if(EmojiEnum.getByUnicode(unicodeEmoji) == EmojiEnum.WHITE_CHECK_MARK){
            if(event.getUser() != host){
                Logger.info("This user may not trigger this reaction, stopping...");
                return;
            }

            if(entrants.size() > 2){
                Logger.info("Starting fight...");
                startFight(event.getChannel());
            } else {
                Logger.info("Not enough entrants, stopping...");
                RequestBuffer.request(() -> MessagesUtils.getMessageByID(messageSent).removeReaction(event.getUser(), EmojiManager.getForAlias(EmojiEnum.WHITE_CHECK_MARK.getAlias())));
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
                if((entrants.size() > 2) && !startReact){
                    RequestBuffer.request(() -> MessagesUtils.getMessageByID(messageSent).addReaction(EmojiManager.getForAlias(EmojiEnum.WHITE_CHECK_MARK.getAlias())));
                    startReact = true;
                }
            }
        } else if(EmojiEnum.getByUnicode(unicodeEmoji) == EmojiEnum.EYES){
            if(ProfileManager.getDiscord(event.getUser(), event.getGuild(), true).hasElevatedPermissions()){
                Logger.info("User has elevated permissions.");
                if(entrants.size() > 1){
                    Logger.info("Starting fight...");
                    startFight(event.getChannel());
                } else {
                    Logger.info("Not enough entrants, stopping...");
                    MessagesUtils.getMessageByID(messageSent).removeReaction(event.getUser(), EmojiManager.getForAlias(EmojiEnum.EYES.getAlias()));
                }
            }
        }
    }

    void reactionRemove(ReactionRemoveEvent event){
        if(event.getUser() == host){
            return;
        }
        if(event.getMessage().getLongID() != messageSent){
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
        Server server = ServerManager.getServer(channel.getGuild().getStringID());
        ScheduledThreadPoolExecutor exec = new ScheduledThreadPoolExecutor(1);
        String entrantsString = formatNames(channel);

        StringBuilder sbKillFeed = new StringBuilder();
        ArrayList<IUser> tempEntrants = entrants;
        HashMap<String,Integer> kills = new HashMap<>();
        sbKillFeed.append("**Free for all killfeed:**\n");
        while (tempEntrants.size() > 1){
            IUser eliminated = tempEntrants.get(MiscUtils.randomInt(0, tempEntrants.size() - 1));
            tempEntrants.remove(eliminated);
            IUser killer = tempEntrants.get(MiscUtils.randomInt(0, tempEntrants.size() - 1));
            if(kills.containsKey(killer.getStringID())){
                kills.put(killer.getStringID(), kills.get(killer.getStringID()) + 1);
            } else {
                kills.put(killer.getStringID(), 1);
            }

            sbKillFeed.append("**").append(killer.getDisplayName(channel.getGuild())).append("** eliminated **").append(eliminated.getDisplayName(channel.getGuild())).append("**\n");
        }
        IUser winner = tempEntrants.get(0);

        StringBuilder sbRewards = new StringBuilder();
        SkuddUser suWinner = ProfileManager.getDiscord(winner.getStringID(), guild.getStringID(), true);
        sbRewards.append(winner.getDisplayName(guild)).append(": *+").append(winReward + (killReward * kills.get(winner.getStringID()))).append(" ").append(EmojiHelper.getEmoji("xp_icon")).append("* (").append(kills.get(winner.getStringID())).append(" kills)");
        if(suWinner.getFfaMostWin() < entrantsAmount){
            sbRewards.append(" - **New highest entrants win:** *").append(entrantsAmount).append(" people*\n");
        }
        for(String s : kills.keySet()){
            if(!s.equals(winner.getStringID()))
            sbRewards.append(channel.getGuild().getUserByID(Long.parseLong(s)).getDisplayName(guild)).append(": *+").append(killReward * kills.get(s)).append(" ").append(EmojiHelper.getEmoji("xp_icon")).append("* (").append(kills.get(s)).append(" kills)\n");
        }
        sbRewards.append("*Click the ").append(EmojiEnum.NOTEPAD_SPIRAL.getEmoji()).append(" reaction to view the kill feed.*");
        String rewards = sbRewards.toString();


        if(MessagesUtils.getMessageByID(messageHost) != null) {
            MessagesUtils.getMessageByID(messageHost).delete();
        }
        if(MessagesUtils.getMessageByID(messageSent) != null) {
            MessagesUtils.getMessageByID(messageSent).delete();
        }

        MessagesUtils.sendPlain(MessageFormat.format("{0} {1} all go into {2} for an epic Free For All battle. Only one can emerge victorious! *3*... *2*... *1*... **FIGHT!**",
                EmojiEnum.CROSSED_SWORDS.getString(), entrantsString, server.getArenaName()),
                channel, false);
        channel.toggleTypingStatus();

        exec.schedule(() -> {
            IMessage messageFinal = MessagesUtils.sendPlain(MessageFormat.format("{0} A furious battle is going on in {1}, bodies are dropping... It looks like **{2}** has won the fight!\n\n{3}",
                    EmojiEnum.CROSSED_SWORDS.getString(), server.getArenaName(), winner.getDisplayName(channel.getGuild()), rewards), channel, false);

            MessagesUtils.addReaction(messageFinal, sbKillFeed.toString().trim(), EmojiEnum.NOTEPAD_SPIRAL, true);

            suWinner.setXp(suWinner.getXp() + winReward + (killReward * kills.get(winner.getStringID())));
            suWinner.setFfaWins(suWinner.getFfaWins() + 1);
            if(suWinner.getFfaMostWin() < entrantsAmount){
                suWinner.setFfaMostWin(entrantsAmount);
            }
            for(String s : kills.keySet()){
                SkuddUser su = ProfileManager.getDiscord(s, guild.getStringID(), true);
                su.setFfaKills(su.getFfaKills() + kills.get(s));
                if(guild.getUserByID(Long.parseLong(s)) != winner){
                    su.setXp(su.getXp() + kills.get(s) * killReward);
                }
            }

            finishFight(winner, guild);
        }, 5, TimeUnit.SECONDS);

    }

    private String formatNames(IChannel channel){
        int entrantsAmount = entrants.size();
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

        return sbEntrants.toString().trim();
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
        messageSent = 0;
        messageHost = 0;
    }

}
