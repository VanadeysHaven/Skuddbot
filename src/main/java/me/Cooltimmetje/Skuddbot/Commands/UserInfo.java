package me.Cooltimmetje.Skuddbot.Commands;

import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.Role;
import discord4j.core.object.entity.User;
import discord4j.core.object.util.Snowflake;
import discord4j.core.spec.EmbedCreateSpec;
import me.Cooltimmetje.Skuddbot.Main;
import me.Cooltimmetje.Skuddbot.Utilities.Constants;

import java.awt.*;
import java.util.List;
import java.util.function.Consumer;

/**
 * This class shows info about the user that ran the command or that has been specified.
 *
 * @author Tim (Cooltimmetje)
 * @version v0.5.1-ALPHA
 * @since v0.3-ALPHA
 */
public class UserInfo {

    public static void run(Message message) {
        User user = message.getAuthor().get();
        if (!message.getUserMentions().collectList().block().isEmpty()) {
            user = message.getUserMentions().collectList().block().get(0);
        } else if (message.getContent().get().split(" ").length > 1){
            if(Main.getInstance().getSkuddbot().getUserById(Snowflake.of(message.getContent().get().split(" ")[1])).block() != null){
                user = Main.getInstance().getSkuddbot().getUserById(Snowflake.of(message.getContent().get().split(" ")[1])).block();
            }
        }

        Member member = user.asMember(message.getGuild().block().getId()).block();
        Consumer<EmbedCreateSpec> template = embedSpec -> {
            embedSpec.setAuthor(member.getUsername() + "#" + member.getDiscriminator(), null, member.getAvatarUrl());
            embedSpec.setThumbnail(member.getAvatarUrl());
            if (Constants.awesomeUser.contains(member.getId().asString())) {
                embedSpec.setColor(new Color(218,165,32)).setDescription("Awesome!");
            }
            if (Constants.adminUser.contains(member.getId().asString())) {
                embedSpec.setColor(new Color(255,0,0)).setDescription("Skuddbot Admin");
            }

            switch (member.getId().asString()){
                case "214049996163645441":
                    embedSpec.setColor(new Color(255,105,180)).setDescription("Glitter queen! - Skuddbot Artist");
                    break;
                case "148376320726794240":
                    embedSpec.setColor(new Color(230,126,34)).setDescription("Skuddbot Admin - Server Developer");
                    break;
                case "76593288865394688":
                    embedSpec.setColor(new Color(52,152,219)).setDescription("Skuddbot Admin - Lead Developer");
                    break;
                case "147295556979523584":
                    embedSpec.setColor(new Color(45, 70, 255)).setDescription("Awesome! - !rule0");
                    break;
                case "91949596737011712":
                    embedSpec.setColor(new Color(52,179,79)).setDescription("Awesome! - Irish Fuck");
                    break;
                case "131382094457733120":
                    embedSpec.setColor(new Color(229,186,17)).setDescription("Awesome! - CHEESE, FOR EVERYONE!");
                    break;
            }

            embedSpec.addField("__User ID:__", member.getId().asString(), false);
            StringBuilder sb = new StringBuilder();
            List<Role> roles = member.getRoles().collectList().block();
            for(Role role : roles){
                sb.append(role.getName().replace("@everyone", "@\u200Beveryone").replace("@here", "@\u200Bhere")).append(", ");
            }
            String rolesString = sb.toString();
            rolesString = rolesString.substring(0, rolesString.length() - 2);
            embedSpec.addField("__Server Nickname:__", !member.getNickname().isPresent() ? "No Nickname" : member.getNickname().get().replace("@everyone", "@\u200Beveryone").replace("@here", "@\u200Bhere"), true);
            embedSpec.addField("__Roles:__", rolesString, true);

            embedSpec.setDescription("All this data is obtained through the public Discord API | Skuddbot " + Constants.config.get("version"));
        };

        message.getChannel().block().createMessage(spec -> {
            spec.setEmbed(template);
        }).block();
    }

}
