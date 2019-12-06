package me.Cooltimmetje.Skuddbot.Listeners;

import discord4j.core.event.domain.guild.MemberJoinEvent;
import discord4j.core.event.domain.guild.MemberLeaveEvent;
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

    public static void onLeave(MemberLeaveEvent event){
        Server server = ServerManager.getServer(event.getGuild().block().getId().asString());
        if(server.getGoodbyeMessage() != null) {

            String message = ServerManager.getServer(event.getGuild().block().getId().asString()).getGoodbyeMessage()
                    .replace("$user", event.getUser().getUsername())
                    .replace("$guild", event.getGuild().block().getName())
                    .replace("$nl","\n");
            MessageChannel channel = (MessageChannel) (server.getWelcomeGoodbyeChannel() != null ?
                    event.getGuild().block().getChannelById(Snowflake.of(server.getWelcomeGoodbyeChannel())).block() :
                    event.getGuild().block().getChannelById(event.getGuild().block().getId()).block());

            Consumer<EmbedCreateSpec> template = embedSpec -> {
                embedSpec.setColor(new Color(MiscUtils.randomInt(0,255), MiscUtils.randomInt(0,255), MiscUtils.randomInt(0,255)));
                embedSpec.setTitle(message);
            };
            channel.createMessage(messageSpec -> {
                messageSpec.setEmbed(template);
            });
        }
    }

}
