package me.Cooltimmetje.Skuddbot.Minigames.Challenge;

import me.Cooltimmetje.Skuddbot.Profiles.Server;
import me.Cooltimmetje.Skuddbot.Profiles.ServerManager;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.reaction.ReactionAddEvent;
import sx.blah.discord.handle.obj.IMessage;

/**
 * Managing of different challenge handling.
 *
 * @author Tim (Cooltimmetje)
 * @version v0.4.34-ALPHA
 * @since v0.4.34-ALPHA
 */
public class ChallengeManager {

    public static void clearCooldowns() {
        for(Server server : ServerManager.servers.values()){
            server.getChallengeHandler().cooldowns.clear();
        }
    }

    //--- DISCORD ---
    public static void run(IMessage message) {
        ServerManager.getServer(message.getGuild().getStringID()).getChallengeHandler().run(message);
    }

    @EventSubscriber
    public void onReaction(ReactionAddEvent event){
        ServerManager.getServer(event.getMessage().getGuild().getStringID()).getChallengeHandler().reactionAccept(event);
    }

    //--- TWITCH ---
    public static void run(String sender, String message, String channel) {
        ServerManager.getTwitch(channel).getChallengeHandler().run(sender, message, channel);
    }

}
