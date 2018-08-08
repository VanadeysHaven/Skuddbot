package me.Cooltimmetje.Skuddbot.Commands;

import me.Cooltimmetje.Skuddbot.Main;
import me.Cooltimmetje.Skuddbot.Utilities.Constants;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;

import java.util.List;

/**
 * This class shows info about the user that ran the command or that has been specified.
 *
 * @author Tim (Cooltimmetje)
 * @version v0.4.31-ALPHA
 * @since v0.3-ALPHA
 */
public class UserInfo {

    public static void run(IMessage message) {
        IUser user = message.getAuthor();
        if (!message.getMentions().isEmpty()) {
            user = message.getMentions().get(0);
        } else if (message.getContent().split(" ").length > 1){
            if(Main.getInstance().getSkuddbot().getUserByID(Long.parseLong(message.getContent().split(" ")[1])) != null){
                user = Main.getInstance().getSkuddbot().getUserByID(Long.parseLong(message.getContent().split(" ")[1]));
            }
        }

        EmbedBuilder embed = new EmbedBuilder();

        embed.withAuthorIcon(user.getAvatarURL()).withAuthorName(user.getName() + "#" + user.getDiscriminator()).withThumbnail(user.getAvatarURL());

        if (Constants.awesomeUser.contains(user.getStringID())) {
            embed.withColor(218,165,32).withDesc("Awesome!");
        }
        if (Constants.adminUser.contains(user.getStringID())) {
            embed.withColor(255,0,0).withDesc("Skuddbot Admin");
        }

        switch (user.getStringID()){
            case "214049996163645441":
                embed.withColor(255,105,180).withDesc("Glitter queen! - Skuddbot Artist");
                break;
            case "148376320726794240":
                embed.withColor(230,126,34).withDesc("Skuddbot Admin - Server Developer");
                break;
            case "76593288865394688":
                embed.withColor(52,152,219).withDesc("Skuddbot Admin - Lead Developer");
                break;
            case "147295556979523584":
                embed.withDesc("Awesome! - !rule0");
                break;
            case "91949596737011712":
                embed.withColor(52,179,79).withDesc("Awesome! - Irish Fuck");
                break;
            case "131382094457733120":
                embed.withColor(229,186,17).withDesc("Awesome! - CHEESE, FOR EVERYONE!");
                break;
        }

        embed.appendField("__User ID:__", user.getStringID(), false);
        StringBuilder sb = new StringBuilder();
        List<IRole> roles = user.getRolesForGuild(message.getGuild());
        for(IRole role : roles){
            sb.append(role.getName().replace("@everyone", "@\u200Beveryone").replace("@here", "@\u200Bhere")).append(", ");
        }
        String rolesString = sb.toString();
        rolesString = rolesString.substring(0, rolesString.length() - 2);
        embed.appendField("__Server Nickname:__", user.getNicknameForGuild(message.getGuild()) == null ? "No Nickname" : user.getNicknameForGuild(message.getGuild()).replace("@everyone", "@\u200Beveryone").replace("@here", "@\u200Bhere"), true);
        embed.appendField("__Roles:__", rolesString, true);

        embed.withFooterText("All this data is obtained through the public Discord API | Skuddbot " + Constants.config.get("version"));

        try {
            message.getChannel().sendMessage("", embed.build(), false);
        } catch (RateLimitException | DiscordException | MissingPermissionsException e) {
            e.printStackTrace();
        }
    }

}
