package me.Cooltimmetje.Skuddbot.Minigames.Challenge;

import discord4j.core.event.domain.message.ReactionAddEvent;
import discord4j.core.object.entity.Channel;
import discord4j.core.object.entity.Message;
import me.Cooltimmetje.Skuddbot.Profiles.Server;
import me.Cooltimmetje.Skuddbot.Profiles.ServerManager;

/**
 * Managing of challenge handlers.
 *
 * @author Tim (Cooltimmetje)
 * @version v0.5.1-ALPHA
 * @since v0.4.34-ALPHA
 */
public class ChallengeManager {

    public static void clearCooldowns() {
        for(Server server : ServerManager.servers.values()){
            server.getChallengeHandler().clearCooldowns();
        }
    }

    //--- DISCORD ---
    public static void run(Message message) {
        ServerManager.getServer(message.getGuild().block().getId().asString()).getChallengeHandler().run(message);
    }

    public static void onReaction(ReactionAddEvent event){
        if(event.getChannel().block().getType() == Channel.Type.GUILD_TEXT) {
            ServerManager.getServer(event.getMessage().block().getGuild().block().getId().asString()).getChallengeHandler().reactionAccept(event);
        }
    }

    //--- TWITCH ---
    public static void run(String sender, String message, String channel) {
        ServerManager.getTwitch(channel.substring(1)).getChallengeHandler().run(sender, message, channel);
    }

}
