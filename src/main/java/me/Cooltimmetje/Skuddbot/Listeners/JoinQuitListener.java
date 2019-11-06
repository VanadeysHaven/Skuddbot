package me.Cooltimmetje.Skuddbot.Listeners;

import discord4j.core.event.domain.guild.MemberJoinEvent;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.MessageChannel;
import discord4j.core.object.entity.Role;
import discord4j.core.object.util.Snowflake;
import discord4j.core.spec.EmbedCreateSpec;
import me.Cooltimmetje.Skuddbot.Profiles.Server;
import me.Cooltimmetje.Skuddbot.Profiles.ServerManager;
import me.Cooltimmetje.Skuddbot.Utilities.MiscUtils;

import java.awt.*;
import java.util.function.Consumer;

/**
 * Stuff to handle when a user joins/leaves
 *
 * @author Tim (Cooltimmetje)
 * @version v0.5.1-ALPHA
 * @since v0.1-ALPHA-DEV
 */
public class JoinQuitListener {

    public static void onJoin(MemberJoinEvent event){
        Server server = ServerManager.getServer(event.getGuild().block().getId().asString());
        if(server.getWelcomeMessage() != null) {
            String message = ServerManager.getServer(event.getGuild().block().getId().asString()).getWelcomeMessage()
                    .replace("$user", event.getMember().getDisplayName())
                    .replace("$guild", event.getMember().getDisplayName())
                    .replace("$nl","\n");
            MessageChannel channel = (MessageChannel) (server.getWelcomeGoodbyeChannel() != null ?
                    event.getGuild().block().getChannelById(Snowflake.of(server.getWelcomeGoodbyeChannel())).block() :
                    event.getGuild().block().getChannelById(event.getGuildId()).block());


            Consumer<EmbedCreateSpec> template = embedSpec -> {
                embedSpec.setColor(new Color(MiscUtils.randomInt(0,255), MiscUtils.randomInt(0,255), MiscUtils.randomInt(0,255)));
                embedSpec.setTitle(message);
            };
            channel.createMessage(messageSpec -> {
                messageSpec.setEmbed(template);
            });
        }
        if(server.getRoleOnJoin() != null){
            Member user = event.getMember();
            Guild guild = event.getGuild().block();
            Role role = MiscUtils.getRoleByString(guild.getRoles().collectList().block(), server.getRoleOnJoin());

            user.addRole(role.getId()).block();
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
