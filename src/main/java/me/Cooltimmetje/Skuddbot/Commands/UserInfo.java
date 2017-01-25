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
 * @version v0.3-ALPHA
 * @since v0.3-ALPHA
 */
public class UserInfo {

    public static void run(IMessage message) {
        IUser user = message.getAuthor();
        if (!message.getMentions().isEmpty()) {
            user = message.getMentions().get(0);
        } else if (message.getContent().split(" ").length > 1){
            if(Main.getInstance().getSkuddbot().getUserByID(message.getContent().split(" ")[1]) != null){
                user = Main.getInstance().getSkuddbot().getUserByID(message.getContent().split(" ")[1]);
            }
        }

        EmbedBuilder embed = new EmbedBuilder();

        embed.withAuthorIcon(user.getAvatarURL()).withAuthorName(user.getName() + "#" + user.getDiscriminator()).withThumbnail(user.getAvatarURL());

        if (Constants.awesomeUser.contains(user.getID())) {
            if (Constants.adminUser.contains(user.getID())) {
                embed.withColor(255,0,0).withDesc("Skuddbot Admin");
            } else {
                embed.withColor(218,165,32).withDesc("Awesome!");
            }
        }
        if(user.getID().equals("214049996163645441")){
            embed.withColor(255,105,180).withDesc("Glitter queen!");
        }

        embed.appendField("__User ID:__", user.getID(), false);
        StringBuilder sb = new StringBuilder();
        List<IRole> roles = user.getRolesForGuild(message.getGuild());
        for(IRole role : roles){
            sb.append(role.getName().replace("@everyone", "@​everyone")).append(", ");
        }
        String rolesString = sb.toString();
        rolesString = rolesString.substring(0, rolesString.length() - 2);
        embed.appendField("__Server Nickname:__", user.getNicknameForGuild(message.getGuild()).isPresent() ? user.getNicknameForGuild(message.getGuild()).get() : "No Nickname", true);
        embed.appendField("__Roles:__", rolesString, true);

        embed.withFooterText("All this data is obtained through the public Discord API | Skuddbot " + Constants.config.get("version"));

        try {
            message.getChannel().sendMessage("", embed.build(), false);
        } catch (RateLimitException | DiscordException | MissingPermissionsException e) {
            e.printStackTrace();
        }
    }

}
