package me.Cooltimmetje.Skuddbot.Minigames.FreeForAll;

import me.Cooltimmetje.Skuddbot.Profiles.Server;
import me.Cooltimmetje.Skuddbot.Profiles.ServerManager;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.reaction.ReactionAddEvent;
import sx.blah.discord.handle.impl.events.guild.channel.message.reaction.ReactionRemoveEvent;
import sx.blah.discord.handle.obj.IMessage;
/**
 * Managing of the different FFA handlers.
 *
 * @author Tim (Cooltimmetje)
 * @version v0.4.4-ALPHA
 * @since v0.4.4-ALPHA
 */
public class FFAManager {

    public static void clearCooldowns(){
        for(Server server : ServerManager.servers.values()){
            server.getFfaHandler().cooldowns.clear();
        }
    }

    public static void run(IMessage message){
        ServerManager.getServer(message.getGuild().getStringID()).getFfaHandler().enter(message);
    }

    @EventSubscriber
    public void onReaction(ReactionAddEvent event){
        ServerManager.getServer(event.getMessage().getGuild().getStringID()).getFfaHandler().reactionAdd(event);
    }

    @EventSubscriber
    public void onReactionRemove(ReactionRemoveEvent event){
        ServerManager.getServer(event.getMessage().getGuild().getStringID()).getFfaHandler().reactionRemove(event);
    }

}
