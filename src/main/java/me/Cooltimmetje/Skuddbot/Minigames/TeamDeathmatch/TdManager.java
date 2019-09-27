package me.Cooltimmetje.Skuddbot.Minigames.TeamDeathmatch;

import me.Cooltimmetje.Skuddbot.Enums.EmojiEnum;
import me.Cooltimmetje.Skuddbot.Utilities.CooldownManager;
import me.Cooltimmetje.Skuddbot.Utilities.MessagesUtils;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.reaction.ReactionAddEvent;
import sx.blah.discord.handle.obj.IMessage;

import java.util.HashMap;

/**
 * This class manages all Team Deathmatch games.
 *
 * @author Tim (Cooltimmetje)
 * @version v0.4.7-ALPHA
 * @since v0.4.7-ALPHA
 */
public class TdManager {

    private static final int COOLDOWN = 300;

    private static HashMap<Long,TeamDeathmatch> teamDeathmatches = new HashMap<>();
    public static HashMap<Long,CooldownManager> cooldowns = new HashMap<>();

    public static void run(IMessage message){
        String[] args = message.getContent().toLowerCase().split(" ");

        if(args.length == 1){ //start new
            if(!isCooldownExpired(message.getAuthor().getStringID(), message.getGuild().getLongID())){
                MessagesUtils.addReaction(message, "The arena is still being cleaned up, please wait.", EmojiEnum.HOURGLASS_FLOWING_SAND);
                return;
            }
            if(teamDeathmatches.containsKey(message.getGuild().getLongID())) {
                MessagesUtils.addReaction(message, "There's already a Team Deathmatch active in this server!", EmojiEnum.X);
            } else {
                TeamDeathmatch game = new TeamDeathmatch(message);
                teamDeathmatches.put(message.getGuild().getLongID(), game);
            }
        } else if (args.length >= 2) {
            if(!teamDeathmatches.containsKey(message.getGuild().getLongID())){
                MessagesUtils.addReaction(message, "There is no game active in this server. Start one with `!td`!", EmojiEnum.X);
                return;
            }
            switch (args[1]){
                case "join":
                    teamDeathmatches.get(message.getGuild().getLongID()).joinTeam(message);
                    break;
                case "start":
                    teamDeathmatches.get(message.getGuild().getLongID()).start(message);
                    break;
                default:
                    MessagesUtils.addReaction(message,"The arguments you used are invalid. Usage: `!td [join/start] [teamnumber/new]`", EmojiEnum.X);
                    break;
            }
        }
    }

    public static void clean(long serverID){
        teamDeathmatches.remove(serverID);
    }

    public static void clearCooldowns() {
        cooldowns.clear();
    }

    @EventSubscriber
    public void onReaction(ReactionAddEvent event){
        if(EmojiEnum.getByUnicode(event.getReaction().getEmoji().getName()) == EmojiEnum.CROSSED_SWORDS){
            teamDeathmatches.get(event.getGuild().getLongID()).joinTeam(event);
        } else if(EmojiEnum.getByUnicode(event.getReaction().getEmoji().getName()) == EmojiEnum.WHITE_CHECK_MARK ||
                EmojiEnum.getByUnicode(event.getReaction().getEmoji().getName()) == EmojiEnum.EYES){
            teamDeathmatches.get(event.getGuild().getLongID()).start(event);
        }
    }

    public static void applyCooldown(String userID, Long serverID){
        CooldownManager cm = cooldowns.get(serverID);
        if(cm == null){
            cm = new CooldownManager(COOLDOWN);
            cooldowns.put(serverID, cm);
        }

        cm.applyCooldown(userID);
    }

    public static boolean isCooldownExpired(String userID, Long serverID){
        CooldownManager cm = cooldowns.get(serverID);
        if(cm == null){
            return false;
        }

        return cm.isCooldownExpired(userID);
    }

}
