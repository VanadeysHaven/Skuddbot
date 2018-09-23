package me.Cooltimmetje.Skuddbot.Listeners;

import me.Cooltimmetje.Skuddbot.Main;
import me.Cooltimmetje.Skuddbot.Profiles.Server;
import me.Cooltimmetje.Skuddbot.Profiles.ServerManager;
import me.Cooltimmetje.Skuddbot.Utilities.Logger;
import me.Cooltimmetje.Skuddbot.Utilities.MiscUtils;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.member.UserJoinEvent;
import sx.blah.discord.handle.impl.events.guild.member.UserLeaveEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;

import java.util.List;

/**
 * Stuff to handle when a user joins/leaves
 *
 * @author Tim (Cooltimmetje)
 * @version v0.4.2-ALPHA
 * @since v0.1-ALPHA-DEV
 */
public class JoinQuitListener {

    @EventSubscriber
    public void onJoin(UserJoinEvent event){
        Server server = ServerManager.getServer(event.getGuild().getStringID());
        if(server.getWelcomeMessage() != null) {
            String message = ServerManager.getServer(event.getGuild().getStringID()).getWelcomeMessage()
                    .replace("$user", event.getUser().getName())
                    .replace("$guild", event.getGuild().getName())
                    .replace("$nl","\n");
            IChannel channel = server.getWelcomeGoodbyeChannel() != null ?
                    event.getGuild().getChannelByID(Long.parseLong(server.getWelcomeGoodbyeChannel())) :
                    event.getGuild().getChannelByID(event.getGuild().getLongID());

            EmbedBuilder builder = new EmbedBuilder();

            builder.withColor(MiscUtils.randomInt(0,255), MiscUtils.randomInt(0,255), MiscUtils.randomInt(0,255));
            builder.withTitle(message);
            if(server.getGoodbyeMsgAttach() != null) {
                builder.withImage(server.getWelcomeMsgAttach());
            }
            channel.sendMessage(builder.build());

        }
        if(server.getRoleOnJoin() != null){
            IUser user = Main.getInstance().getSkuddbot().getUserByID(event.getUser().getLongID());
            IGuild guild = Main.getInstance().getSkuddbot().getGuildByID(Long.parseLong(server.getServerID()));
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
        Server server = ServerManager.getServer(event.getGuild().getStringID());
        if(server.getGoodbyeMessage() != null) {

            String message = ServerManager.getServer(event.getGuild().getStringID()).getGoodbyeMessage()
                    .replace("$user", event.getUser().getName())
                    .replace("$guild", event.getGuild().getName())
                    .replace("$nl","\n");
            IChannel channel = server.getWelcomeGoodbyeChannel() != null ?
                    event.getGuild().getChannelByID(Long.parseLong(server.getWelcomeGoodbyeChannel())) :
                    event.getGuild().getChannelByID(event.getGuild().getLongID());

            EmbedBuilder builder = new EmbedBuilder();

            builder.withColor(MiscUtils.randomInt(0,255), MiscUtils.randomInt(0,255), MiscUtils.randomInt(0,255));
            builder.withTitle(message);
            if(server.getGoodbyeMsgAttach() != null) {
                builder.withImage(server.getGoodbyeMsgAttach());
            }
            channel.sendMessage(builder.build());
        }
    }

}
