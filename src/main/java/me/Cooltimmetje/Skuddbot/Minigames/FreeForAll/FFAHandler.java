package me.Cooltimmetje.Skuddbot.Minigames.FreeForAll;

import discord4j.core.event.domain.message.ReactionAddEvent;
import discord4j.core.event.domain.message.ReactionRemoveEvent;
import discord4j.core.object.entity.*;
import discord4j.core.object.reaction.ReactionEmoji;
import discord4j.core.object.util.Snowflake;
import me.Cooltimmetje.Skuddbot.Enums.EmojiEnum;
import me.Cooltimmetje.Skuddbot.Main;
import me.Cooltimmetje.Skuddbot.Profiles.ProfileManager;
import me.Cooltimmetje.Skuddbot.Profiles.Server;
import me.Cooltimmetje.Skuddbot.Profiles.ServerManager;
import me.Cooltimmetje.Skuddbot.Profiles.SkuddUser;
import me.Cooltimmetje.Skuddbot.Utilities.*;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
/**
 * This class handles the FFA command on a per-server basis.
 *
 * @author Tim (Cooltimmetje)
 * @version v0.5.1-ALPHA
 * @since v0.4.4-ALPHA
 */

//TODO: REWRITE
public class FFAHandler {

    private String serverID;

    public FFAHandler(String serverID){
        this.serverID = serverID;
        this.cooldownManager = new CooldownManager(COOLDOWN);
        Logger.info("Creating FFA handler for Server with ID: " + serverID);
    }

    private static final int WIN_REWARD = 100;
    private static final int KILL_REWARD = 50;
    private static final int COOLDOWN = 300; //in seconds
    private static final int REMIND_DELAY = 6; //in hours

    private CooldownManager cooldownManager;

    public void clearCooldowns(){
        cooldownManager.clearAll();
    }

    // ---- DISCORD ----
    private ArrayList<Member> entrants = new ArrayList<>();

    private Member host;
    private long messageSent;
    private long messageHost;
    private boolean startReact;
    private long lastReminder;
    private int lastEntrants;
    private MessageChannel channel;

    void enter(Message message){
        String[] args = message.getContent().get().split(" ");
        if(cooldownManager.isOnCooldown(message.getAuthor().get().getId().asString())){
            MessagesUtils.addReaction(message, "Hold on there, **" + message.getAuthor().get().getMention() + "**, you're still wounded from the last fight.", EmojiEnum.HOURGLASS_FLOWING_SAND, false);
            return;
        }

        if(message.getAuthor().get().asMember(Snowflake.of(serverID)).block() == host){
            if(args.length > 1){
                if(args[1].equalsIgnoreCase("-start")){
                    if(entrants.size() > 1) {
                        startFight(message.getChannel().block());
                        message.delete().block();
                    }
                }
            }
        }

        if(host == null) {
            host = message.getAuthor().get().asMember(Snowflake.of(serverID)).block();
            messageHost = message.getId().asLong();
            messageSent = MessagesUtils.sendPlain(MessageFormat.format("{0} **{1}** is looking to host a free for all fight, anyone can participate!\n" +
                            "Click the {0} reaction to enter, {1} can start the fight by clicking the {2} reaction.",
                    EmojiEnum.CROSSED_SWORDS.getString(), message.getAuthor().get().asMember(Snowflake.of(serverID)).block().getDisplayName(), EmojiEnum.WHITE_CHECK_MARK.getString()),
                    message.getChannel().block(), false).getId().asLong();

            MessagesUtils.getMessageByID(messageSent, Long.parseLong(serverID)).addReaction(ReactionEmoji.unicode(EmojiEnum.CROSSED_SWORDS.getUnicode())).block();
            entrants.add(message.getAuthor().get().asMember(Snowflake.of(serverID)).block());
            startReact = false;
            lastReminder = System.currentTimeMillis();
            channel = message.getChannel().block();
        } else {
            message.delete().block();
        }
    }

    void reactionAdd(ReactionAddEvent event){
        String unicodeEmoji = event.getEmoji().asUnicodeEmoji().get().getRaw();

        if(unicodeEmoji == EmojiEnum.WHITE_CHECK_MARK.getUnicode()){
            if(entrants.size() > 2){
                startFight(event.getChannel().block());
            } else {
                MessagesUtils.getMessageByID(messageSent, Long.parseLong(serverID)).removeReaction(ReactionEmoji.unicode(EmojiEnum.WHITE_CHECK_MARK.getUnicode()), event.getUserId()).block();
            }
        } else if (EmojiEnum.getByUnicode(unicodeEmoji) == EmojiEnum.CROSSED_SWORDS){
            if(event.getUser().block().asMember(Snowflake.of(serverID)).block() == host){
                event.getMessage().block().removeReaction(ReactionEmoji.unicode(EmojiEnum.WHITE_CHECK_MARK.getUnicode()), event.getUserId()).block();
                return;
            }

            if(!entrants.contains(event.getUser())) {
                entrants.add(event.getUser().block().asMember(Snowflake.of(serverID)).block());
                if((entrants.size() > 2) && !startReact){
                    MessagesUtils.getMessageByID(messageSent, Long.parseLong(serverID)).addReaction(ReactionEmoji.unicode(EmojiEnum.WHITE_CHECK_MARK.getUnicode())).block();
                    startReact = true;
                }
            }
        } else if(EmojiEnum.getByUnicode(unicodeEmoji) == EmojiEnum.EYES){
            if(ProfileManager.getDiscord(toMember(event.getUser().block()), true).hasElevatedPermissions()){
                if(entrants.size() > 1){
                    startFight(event.getChannel().block());
                } else {
                    MessagesUtils.getMessageByID(messageSent, Long.parseLong(serverID)).removeReaction(ReactionEmoji.unicode(EmojiEnum.EYES.getUnicode()), event.getUserId()).block();
                }
            }
        }
    }

    void reactionRemove(ReactionRemoveEvent event){
        if(toMember(event.getUser().block()) == host){
            return;
        }
        if(event.getMessage().block().getId().asLong() != messageSent){
            return;
        }
        if(event.getEmoji().asUnicodeEmoji().get().getRaw().equals(EmojiEnum.CROSSED_SWORDS.getUnicode())){
            return;
        }

        entrants.remove(toMember(event.getUser().block()));
    }

    private void startFight(MessageChannel channel) {
        int entrantsAmount = entrants.size();
        Guild guild = ((TextChannel)channel).getGuild().block();
        Server server = ServerManager.getServer(guild.getId().asString());
        ScheduledThreadPoolExecutor exec = new ScheduledThreadPoolExecutor(1);
        String entrantsString = formatNames();

        StringBuilder sbKillFeed = new StringBuilder();
        ArrayList<Member> tempEntrants = new ArrayList<>(entrants);
        HashMap<String,Integer> kills = new HashMap<>();
        sbKillFeed.append("**Free for all killfeed:**\n");
        while (tempEntrants.size() > 1){
            Member eliminated = tempEntrants.get(MiscUtils.randomInt(0, tempEntrants.size() - 1));
            tempEntrants.remove(eliminated);
            Member killer = tempEntrants.get(MiscUtils.randomInt(0, tempEntrants.size() - 1));
            if(kills.containsKey(killer.getId().asString())){
                kills.put(killer.getId().asString(), kills.get(killer.getId().asString()) + 1);
            } else {
                kills.put(killer.getId().asString(), 1);
            }

            sbKillFeed.append("**").append(killer.getDisplayName()).append("** eliminated **").append(eliminated.getDisplayName()).append("**\n");
        }
        Member winner = tempEntrants.get(0);

        StringBuilder sbRewards = new StringBuilder();
        SkuddUser suWinner = ProfileManager.getDiscord(winner, true);
        sbRewards.append(winner.getDisplayName()).append(": *+").append(WIN_REWARD + (KILL_REWARD * kills.get(winner.getId().asString()))).append(" ").append(EmojiHelper.getEmoji("xp_icon")).append("* (").append(kills.get(winner.getId().asString())).append(" kills)");
        if(suWinner.getFfaMostWin() < entrantsAmount){
            sbRewards.append(" - **New highest entrants win:** *").append(entrantsAmount).append(" people*");
        }
        sbRewards.append("\n");
        for(String s : kills.keySet()){
            if(!s.equals(winner.getId().asString()))
                sbRewards.append(guild.getMemberById(Snowflake.of(s)).block().getDisplayName()).append(": *+").append(KILL_REWARD * kills.get(s)).append(" ").append(EmojiHelper.getEmoji("xp_icon")).append("* (").append(kills.get(s)).append(" kills)\n");
        }
        sbRewards.append("*Click the ").append(EmojiEnum.NOTEPAD_SPIRAL.getUnicode()).append(" reaction to view the kill feed.*");
        String rewards = sbRewards.toString();


        if(MessagesUtils.getMessageByID(messageHost, channel.getId().asLong()) != null) {
            MessagesUtils.getMessageByID(messageHost, channel.getId().asLong()).delete().block();
        }
        if(MessagesUtils.getMessageByID(messageSent, channel.getId().asLong()) != null) {
            MessagesUtils.getMessageByID(messageSent, channel.getId().asLong()).delete().block();
        }

        MessagesUtils.sendPlain(MessageFormat.format("{0} {1} all go into {2} for an epic Free For All battle. Only one can emerge victorious! *3*... *2*... *1*... **FIGHT!**",
                EmojiEnum.CROSSED_SWORDS.getString(), entrantsString, server.getArenaName()),
                channel, false);
        channel.type().block();

        exec.schedule(() -> {
            Message messageFinal = MessagesUtils.sendPlain(MessageFormat.format("{0} A furious battle is going on in {1}, bodies are dropping... It looks like **{2}** has won the fight!\n\n{3}",
                    EmojiEnum.CROSSED_SWORDS.getString(), server.getArenaName(), winner.getDisplayName(), rewards), channel, false);

            MessagesUtils.addReaction(messageFinal, sbKillFeed.toString().trim(), EmojiEnum.NOTEPAD_SPIRAL, true, 6*60*60*1000);

            suWinner.setXp(suWinner.getXp() + WIN_REWARD + (KILL_REWARD * kills.get(winner.getId().asString())));
            suWinner.setFfaWins(suWinner.getFfaWins() + 1);
            if(suWinner.getFfaMostWin() < entrantsAmount){
                suWinner.setFfaMostWin(entrantsAmount);
            }
            for(String s : kills.keySet()){
                SkuddUser su = ProfileManager.getDiscord(s, guild.getId().asString(), true);
                su.setFfaKills(su.getFfaKills() + kills.get(s));
                if(guild.getMemberById(Snowflake.of(s)).block() != winner){
                    su.setXp(su.getXp() + kills.get(s) * KILL_REWARD);
                }
            }

            finishFight(winner, guild);
        }, 5, TimeUnit.SECONDS);

    }

    private String formatNames(){
        int entrantsAmount = entrants.size();
        StringBuilder sbEntrants = new StringBuilder();
        for (Member member : entrants) {
            if (entrants.indexOf(member) == (entrantsAmount - 1)) {
                sbEntrants.append("**").append(member.getDisplayName()).append("**");
            } else if (entrants.indexOf(member) == (entrantsAmount - 2)) {
                sbEntrants.append("**").append(member.getDisplayName()).append("** and ");
            } else {
                sbEntrants.append("**").append(member.getDisplayName()).append("**, ");
            }
        }

        return sbEntrants.toString().trim();
    }

    private void finishFight(Member winner, Guild guild){
        for(Member member : entrants){
            cooldownManager.applyCooldown(member.getId().asString());
            if(member != winner){
                SkuddUser suLoser = ProfileManager.getDiscord(member.getId().asString(), guild.getId().asString(), true);

                suLoser.setFfaLosses(suLoser.getFfaLosses() + 1);
            }
        }
        entrants.clear();

        host = null;
        messageSent = 0;
        messageHost = 0;
        lastReminder = 0;
        lastEntrants = 0;
    }

    public void remind(){
        if(entrants.size() < 3) return;
        if(host == null) return;
        if(!ProfileManager.getDiscord(host.getId().asString(), serverID, true).isMinigameReminders()) return;
        if((System.currentTimeMillis() - lastReminder) < (REMIND_DELAY * 60 * 60 * 1000)) return;

        if(lastEntrants != entrants.size()) {
            Message message = MessagesUtils.getMessageByID(messageSent, host.getId().asLong());
            MessagesUtils.sendPlain(MessageFormat.format("Hey, you still got a free for all with **{0} entrants** pending in {1} (**{2}**).\n(**PRO-TIP:** You can use search to quickly find it!)", entrants.size(), ((TextChannel) message.getChannel().block()).toString(), message.getGuild().block().getName()), host.getPrivateChannel().block(), false);
        } else {
            startFight(channel);
        }

        lastReminder = System.currentTimeMillis();
        lastEntrants = entrants.size();
    }


    // ---- TWITCH ----
    private ArrayList<String> twitchEntrants = new ArrayList<>();

    private static final int START_DELAY = 120;
    private boolean fightRunning = false;
    ScheduledThreadPoolExecutor execTwitch = new ScheduledThreadPoolExecutor(1);

    public void run(String sender, String channel) {
        if(fightRunning) return;
        if(cooldownManager.isOnCooldown(sender)) return;


        if(twitchEntrants.isEmpty()) {
            Main.getSkuddbotTwitch().send(MessageFormat.format("{0} is looking to host a free for all fight, anyone can participate! Type \"!ffa\" to enter, the fight will start in {1} minutes.", sender, START_DELAY / 60), channel);
            execTwitch.schedule(() -> startFightTwitch(channel), START_DELAY, TimeUnit.SECONDS);
        }
        if(!twitchEntrants.contains(sender)) twitchEntrants.add(sender);
    }

    public void startFightTwitch(String channel){
        fightRunning = true;
        ArrayList<String> orignalEntrants = new ArrayList<>(twitchEntrants);
        int entrants = twitchEntrants.size();
        if(twitchEntrants.size() < 3){
            Main.getSkuddbotTwitch().send("The fight has been called off, there were not enough participants!", channel);
            twitchEntrants.clear();
            fightRunning = false;
            return;
        }
        for(String string : twitchEntrants){
            cooldownManager.applyCooldown(string);
        }
        ScheduledThreadPoolExecutor exec = new ScheduledThreadPoolExecutor(1);
        Server server = ServerManager.getTwitch(channel);

        Main.getSkuddbotTwitch().send(MessageFormat.format("The combatants step into {0} for a epic free for all battle! Only one can emerge victorious! 3... 2... 1... FIGHT!", server.getArenaName()), channel);
        HashMap<String,Integer> kills = new HashMap<>();
        while(twitchEntrants.size() > 1){
            String eliminated = twitchEntrants.get(MiscUtils.randomInt(0, twitchEntrants.size() - 1));
            twitchEntrants.remove(eliminated);
            String killer = twitchEntrants.get(MiscUtils.randomInt(0, twitchEntrants.size() - 1));
            if(kills.containsKey(killer)){
                kills.put(killer, kills.get(killer) + 1);
            } else {
                kills.put(killer, 1);
            }
        }
        String winner = twitchEntrants.get(0);
        SkuddUser suWinner = ProfileManager.getTwitch(winner, channel, true);
        twitchEntrants.clear();

        StringBuilder sb = new StringBuilder();
        sb.append("A furious battle is going on in {0}, bodies are dropping.... It looks like {1} has won the fight! | ");
        sb.append(winner).append(": +").append(WIN_REWARD + (kills.get(winner) * KILL_REWARD)).append(" XP (").append(kills.get(winner)).append(" kills)");
        if(suWinner.getFfaMostWin() < entrants){
            sb.append(" - New highest entrants win: ").append(entrants).append(" entrants");
        }
        sb.append(" | ");
        for(String string : kills.keySet()){
            if(!string.equals(winner)) {
                sb.append(string).append(":9 +").append(kills.get(string) * KILL_REWARD).append(" XP (").append(kills.get(string)).append(" kills) | ");
            }
        }

        exec.schedule(() -> {
            Main.getSkuddbotTwitch().send(MessageFormat.format(sb.toString().substring(0, sb.toString().length() - 2), server.getArenaName(), winner), channel);
            if(suWinner.getFfaMostWin() < entrants){
                suWinner.setFfaMostWin(entrants);
            }
            for(String string : orignalEntrants){
                SkuddUser su = ProfileManager.getTwitch(string, channel, true);
                if(!string.equals(winner)){
                    su.setFfaLosses(su.getFfaLosses() + 1);
                } else {
                    su.setFfaWins(su.getFfaWins() + 1);
                }
            }
            for(String string : kills.keySet()){
                SkuddUser su = ProfileManager.getTwitch(string, channel, true);
                su.setFfaKills(su.getFfaKills() + kills.get(string));
                int xpGain = (string.equals(winner) ? WIN_REWARD : 0) + (kills.get(string) * KILL_REWARD);
                su.setXp(su.getXp() + xpGain);
            }

            twitchEntrants.clear();
            fightRunning = false;
        }, 5, TimeUnit.SECONDS);
    }

    private Member toMember(User user){
        return user.asMember(Snowflake.of(serverID)).block();
    }

}
