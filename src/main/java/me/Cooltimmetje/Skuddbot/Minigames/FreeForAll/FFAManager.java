package me.Cooltimmetje.Skuddbot.Minigames.FreeForAll;

import discord4j.core.event.domain.message.ReactionAddEvent;
import discord4j.core.event.domain.message.ReactionRemoveEvent;
import discord4j.core.object.entity.Channel;
import discord4j.core.object.entity.Message;
import me.Cooltimmetje.Skuddbot.Enums.EmojiEnum;
import me.Cooltimmetje.Skuddbot.Profiles.Server;
import me.Cooltimmetje.Skuddbot.Profiles.ServerManager;
import me.Cooltimmetje.Skuddbot.Utilities.Constants;
import me.Cooltimmetje.Skuddbot.Utilities.MessagesUtils;

/**
 * Managing of the different FFA handlers.
 *
 * @author Tim (Cooltimmetje)
 * @version v0.5.1-ALPHA
 * @since v0.4.4-ALPHA
 */
public class FFAManager {

    public static void clearCooldowns(){
        for(Server server : ServerManager.servers.values()){
            server.getFfaHandler().clearCooldowns();
        }
    }

    // ---- DISCORD ----
    public static void run(Message message){
        String[] args = message.getContent().get().split(" ");
        if(args.length > 1){
            if(args[1].equalsIgnoreCase("-fix") && Constants.adminUser.contains(message.getAuthor().get().getId().asString())){
                ServerManager.getServer(message.getGuild().block().getId().asString()).setFfaHandler(new FFAHandler(message.getGuild().block().getId().asString()));
                MessagesUtils.addReaction(message, "Created new FFA handler for this server.", EmojiEnum.WHITE_CHECK_MARK, false);
                return;
            }
        }
        ServerManager.getServer(message.getGuild().block().getId().asString()).getFfaHandler().enter(message);
    }

    public static void runReminders(){
        for(Server server : ServerManager.servers.values()){
            server.getFfaHandler().remind();
        }
    }

    public void onReaction(ReactionAddEvent event){
        if(event.getChannel().block().getType() != Channel.Type.DM) {
            ServerManager.getServer(event.getMessage().block().getGuild().block().getId().asString()).getFfaHandler().reactionAdd(event);
        }
    }

    public void onReactionRemove(ReactionRemoveEvent event){
        if(event.getChannel().block().getType() != Channel.Type.DM) {
            ServerManager.getServer(event.getMessage().block().getGuild().block().getId().asString()).getFfaHandler().reactionRemove(event);
        }
    }



    // ---- TWITCH ----
    public static void run(String sender, String message, String channel) {
        ServerManager.getTwitch(channel.substring(1)).getFfaHandler().run(sender, channel);
    }

}
