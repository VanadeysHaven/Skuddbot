package me.Cooltimmetje.Skuddbot.Minigames.TeamDeathmatch;

import discord4j.core.event.domain.message.ReactionAddEvent;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Message;
import me.Cooltimmetje.Skuddbot.Enums.EmojiEnum;
import me.Cooltimmetje.Skuddbot.Utilities.CooldownManager;
import me.Cooltimmetje.Skuddbot.Utilities.MessagesUtils;

import java.util.HashMap;

/**
 * This class manages all Team Deathmatch games.
 *
 * @author Tim (Cooltimmetje)
 * @version v0.5.1-ALPHA
 * @since v0.4.7-ALPHA
 */
public class TdManager {

    private static final int COOLDOWN = 300;

    private static HashMap<Long,TeamDeathmatch> teamDeathmatches = new HashMap<>();
    public static HashMap<Long,CooldownManager> cooldowns = new HashMap<>();

    public static void run(Message message){
        String[] args = message.getContent().get().toLowerCase().split(" ");
        Guild guild = message.getGuild().block();

        if(args.length == 1){ //start new
            if(isOnCooldown(message.getAuthor().get().getId().asString(), guild.getId().asLong())){
                MessagesUtils.addReaction(message, "The arena is still being cleaned up, please wait.", EmojiEnum.HOURGLASS_FLOWING_SAND);
                return;
            }
            if(teamDeathmatches.containsKey(guild.getId().asLong())) {
                MessagesUtils.addReaction(message, "There's already a Team Deathmatch active in this server!", EmojiEnum.X);
            } else {
                TeamDeathmatch game = new TeamDeathmatch(message);
                teamDeathmatches.put(guild.getId().asLong(), game);
            }
        } else if (args.length >= 2) {
            if(!teamDeathmatches.containsKey(guild.getId().asLong())){
                MessagesUtils.addReaction(message, "There is no game active in this server. Start one with `!td`!", EmojiEnum.X);
                return;
            }
            switch (args[1]){
                case "join":
                    teamDeathmatches.get(guild.getId().asLong()).joinTeam(message);
                    break;
                case "start":
                    teamDeathmatches.get(guild.getId().asLong()).start(message);
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

    public static void onReaction(ReactionAddEvent event){
        Guild guild = event.getGuild().block();
        String reaction = event.getEmoji().asUnicodeEmoji().get().getRaw();
        if(!teamDeathmatches.containsKey(guild.getId().asLong())) return;
        if(EmojiEnum.getByUnicode(reaction) == EmojiEnum.CROSSED_SWORDS){
            teamDeathmatches.get(guild.getId().asLong()).joinTeam(event);
        } else if(EmojiEnum.getByUnicode(reaction) == EmojiEnum.WHITE_CHECK_MARK ||
                EmojiEnum.getByUnicode(reaction) == EmojiEnum.EYES){
            teamDeathmatches.get(guild.getId().asLong()).start(event);
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

    public static boolean isOnCooldown(String userID, Long serverID){
        CooldownManager cm = cooldowns.get(serverID);
        if(cm == null){
            return false;
        }

        return cm.isOnCooldown(userID);
    }

    public static void runReminders(){
        for(TeamDeathmatch teamDeathmatch : teamDeathmatches.values()){
            teamDeathmatch.runReminder();
        }
    }

}
