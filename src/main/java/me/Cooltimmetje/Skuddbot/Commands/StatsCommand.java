package me.Cooltimmetje.Skuddbot.Commands;

import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.spec.EmbedCreateSpec;
import me.Cooltimmetje.Skuddbot.Enums.EmojiEnum;
import me.Cooltimmetje.Skuddbot.Enums.UserStats.UserStats;
import me.Cooltimmetje.Skuddbot.Enums.UserStats.UserStatsCats;
import me.Cooltimmetje.Skuddbot.Profiles.ProfileManager;
import me.Cooltimmetje.Skuddbot.Profiles.SkuddUser;
import me.Cooltimmetje.Skuddbot.Utilities.MessagesUtils;
import me.Cooltimmetje.Skuddbot.Utilities.MiscUtils;

import java.awt.*;
import java.text.MessageFormat;
import java.util.function.Consumer;

/**
 * Command that will generate a stats overview and print it out.
 *
 * @author Tim (Cooltimmetje)
 * @version v0.5.1-ALPHA
 * @since v0.4.42-ALPHA
 */
public class StatsCommand {

    public static void run(Message message) { //!stats <user> <stat> <add/remove/set> <amount>
        String[] args = message.getContent().get().split(" ");
        if(args.length <= 2) {
            printStats(message);
        } else if(args.length > 4) {
            changeStat(message);
        } else {
            MessagesUtils.addReaction(message, "Incorrect arguments. Usage: !stats <user> [stat] [add/remove/set] [amount]", EmojiEnum.X, false);
        }
    }

    public static void changeStat(Message message){
        if(!ProfileManager.getDiscord(message.getAuthor().get(), message.getGuild().block(), true).hasElevatedPermissions()){
            MessagesUtils.addReaction(message, "You do not have permission to do that.", EmojiEnum.X, false);
            return;
        }

        String[] args = message.getContent().get().split(" ");
        if(message.getUserMentions().collectList().block().isEmpty()){
            MessagesUtils.addReaction(message, "No user specified.", EmojiEnum.X, false);
        }
        Guild guild = message.getGuild().block();
        Member member = message.getUserMentions().collectList().block().get(0).asMember(guild.getId()).block();
        SkuddUser su = ProfileManager.getDiscord(member, guild, true);
        UserStats stat = null;
        try {
            stat = UserStats.valueOf(args[2].toUpperCase().replace("-", "_"));
        } catch (IllegalArgumentException e){
            MessagesUtils.addReaction(message, "This stat does not exist.", EmojiEnum.X, false);
            return;
        }
        String operation = args[3].toLowerCase();
        if(!operation.equals("add") && !operation.equals("remove") && !operation.equals("set")){
            MessagesUtils.addReaction(message, "The operation specified is not valid.", EmojiEnum.X, false);
            return;
        }
        if(!MiscUtils.isInt(args[4])){
            MessagesUtils.addReaction(message, "The amount specified is not a number.", EmojiEnum.X, false);
            return;
        }
        int amount = Integer.parseInt(args[4]);

        switch (operation){
            case "add":
                int initialAmount = Integer.parseInt(su.getStat(stat));
                int newAmount = initialAmount + amount;
                su.setStat(stat, newAmount+"");
                MessagesUtils.addReaction(message, MessageFormat.format("`{0}` has been added to stat `{1}` for user `{2}`.", amount, stat.toString(), member.getDisplayName()), EmojiEnum.WHITE_CHECK_MARK, false);
                break;
            case "remove":
                initialAmount = Integer.parseInt(su.getStat(stat));
                newAmount = initialAmount - amount;
                su.setStat(stat, newAmount+"");
                MessagesUtils.addReaction(message, MessageFormat.format("`{0}` has been removed from stat `{1}` for user `{2}`.", amount, stat.toString(), member.getDisplayName()), EmojiEnum.WHITE_CHECK_MARK, false);
                break;
            case "set":
                su.setStat(stat, amount+"");
                MessagesUtils.addReaction(message, MessageFormat.format("Stat `{1}` has been set to `{0}` for user `{2}`.", amount, stat.toString(), member.getDisplayName()), EmojiEnum.WHITE_CHECK_MARK, false);
                break;
            default:
                break;
        }
    }

    public static void printStats(Message message){
        Guild guild = message.getGuild().block();
        Member user = message.getAuthor().get().asMember(guild.getId()).block();
        SkuddUser su = ProfileManager.getDiscord(user.getId().asString(), guild.getId().asString(), false);
        boolean otherUser = message.getUserMentions().collectList().block().size() >= 1;

        if (otherUser) {
            user = message.getUserMentions().collectList().block().get(0).asMember(guild.getId()).block();
            su = ProfileManager.getDiscord(user.getId().asString(), guild.getId().asString(), false);
        }

        if (su == null) {
            String debug = otherUser ? "**" + user.getDisplayName() + "** has no stats yet." : "You have no stats yet.";
            MessagesUtils.addReaction(message, debug, EmojiEnum.X, false);
            return;
        }

        if (su.isStatsPrivate() && otherUser) {
            MessagesUtils.addReaction(message, "**" + user.getDisplayName() + "** has made their stats private.", EmojiEnum.X, false);
            return;
        }

        final Member finalUser = user;
        SkuddUser finalSu = su;
        Consumer<EmbedCreateSpec> template = embedSpec -> {
            embedSpec.setAuthor("Stats for: " + finalUser.getDisplayName(), null, finalUser.getAvatarUrl());
            embedSpec.setColor(new Color(MiscUtils.randomInt(0, 255), MiscUtils.randomInt(0, 255), MiscUtils.randomInt(0, 255)));
            embedSpec.setDescription("__Server:__ " + guild.getName());

            for (UserStatsCats category : UserStatsCats.values()){
                if(category.isShow()) {
                    embedSpec.addField("\u200B", "__" + category.getName() + ":__",  false);

                    for (UserStats stat : UserStats.values()) {
                        if (stat.isShow() && stat.getCategory() == category) {
                            if (stat == UserStats.TD_FAV_TEAMMATE) {
                                embedSpec.addField("__" + stat.getDescription() + ":__", finalSu.getFavouriteTeammates(), true);
                            } else {
                                embedSpec.addField("__" + stat.getDescription() + ":__", finalSu.getStat(stat) + " " + stat.getStatSuffix(), true);
                            }
                        }
                    }
                }
            }
        };

        message.getChannel().block().createMessage(spec -> {
            spec.setEmbed(template);
        }).block();
    }

}
