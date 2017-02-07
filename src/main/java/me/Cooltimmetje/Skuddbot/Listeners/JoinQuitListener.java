package me.Cooltimmetje.Skuddbot.Listeners;

import me.Cooltimmetje.Skuddbot.Main;
import me.Cooltimmetje.Skuddbot.Profiles.Server;
import me.Cooltimmetje.Skuddbot.Profiles.ServerManager;
import me.Cooltimmetje.Skuddbot.Utilities.Logger;
import me.Cooltimmetje.Skuddbot.Utilities.MessagesUtils;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.UserJoinEvent;
import sx.blah.discord.handle.impl.events.UserLeaveEvent;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;

import java.util.List;

/**
 * Created by Tim on 8/18/2016.
 */
public class JoinQuitListener {

    @EventSubscriber
    public void onJoin(UserJoinEvent event){
        Server server = ServerManager.getServer(event.getGuild().getID());
        if(server.getWelcomeMessage() != null) {
            MessagesUtils.sendPlain(ServerManager.getServer(event.getGuild().getID()).getWelcomeMessage().replace("$user", event.getUser().mention()).replace("$guild", event.getGuild().getName()).replace("$nl","\n"),
                    server.getWelcomeGoodbyeChannel() != null ? event.getGuild().getChannelByID(server.getWelcomeGoodbyeChannel()) : event.getGuild().getChannelByID(event.getGuild().getID()), false);
        }
        if(server.getRoleOnJoin() != null){
                IUser user = Main.getInstance().getSkuddbot().getUserByID(event.getUser().getID());
                IGuild guild = Main.getInstance().getSkuddbot().getGuildByID(server.getServerID());
                List<IRole> roleList = user.getRolesForGuild(guild);

                roleList.add(guild.getRolesByName(server.getRoleOnJoin()).get(0));
                IRole[] roles = roleList.toArray(new IRole[roleList.size()]);

                try {
                    guild.editUserRoles(user, roles);
                } catch (MissingPermissionsException | RateLimitException | DiscordException e) {
                    Logger.warn("Couldn't add role. See Stacktrace", e);
                }
        }
    }

    @EventSubscriber
    public void onLeave(UserLeaveEvent event){
        Server server = ServerManager.getServer(event.getGuild().getID());
        if(server.getGoodbyeMessage() != null) {
            MessagesUtils.sendPlain(server.getGoodbyeMessage().replace("$user", event.getUser().getName()).replace("$guild", event.getGuild().getName()).replace("$nl","\n"),
                    server.getWelcomeGoodbyeChannel() != null ? event.getGuild().getChannelByID(server.getWelcomeGoodbyeChannel()) : event.getGuild().getChannelByID(event.getGuild().getID()), false);
        }
    }

}
