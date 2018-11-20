package me.Cooltimmetje.Skuddbot.Minigames.FreeForAll;

import com.vdurmont.emoji.EmojiManager;
import me.Cooltimmetje.Skuddbot.Enums.EmojiEnum;
import me.Cooltimmetje.Skuddbot.Profiles.Server;
import me.Cooltimmetje.Skuddbot.Profiles.ServerManager;
import me.Cooltimmetje.Skuddbot.Utilities.Logger;
import me.Cooltimmetje.Skuddbot.Utilities.MessagesUtils;
import me.Cooltimmetje.Skuddbot.Utilities.MiscUtils;
import sx.blah.discord.handle.impl.events.guild.channel.message.reaction.ReactionAddEvent;
import sx.blah.discord.handle.impl.events.guild.channel.message.reaction.ReactionRemoveEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.RequestBuffer;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class FFAHandler {

    private String serverID;

    public FFAHandler(String serverID){
        this.serverID = serverID;
        Logger.info("Creating FFA handler for Server with ID: " + serverID);
    }

    private int xpReward = 50;
    private int cooldown = 300;

    public HashMap<IUser,Long> cooldowns = new HashMap<>();
    private ArrayList<IUser> entrants = new ArrayList<>();

    private IUser host = null;
    private IMessage messageSent;
    private IMessage messageHost;

    public void enter(IMessage message){
        if(message.getAuthor() == host){
            return;
        }

        entrants.add(message.getAuthor());
        if(host == null) {
            host = message.getAuthor();
            messageHost = message;
            messageSent = MessagesUtils.sendPlain(MessageFormat.format("{0} **{1}** is looking to host a free for all fight, anyone can participate!\n" +
                            "Click the {0} reaction to enter, {1} can start the fight by clicking the {2} reaction.",
                    EmojiEnum.CROSSED_SWORDS.getString(), message.getAuthor().getDisplayName(message.getGuild()), EmojiEnum.WHITE_CHECK_MARK.getString()),
                    message.getChannel(), false);

            RequestBuffer.request(() -> {
                messageSent.addReaction(EmojiManager.getForAlias(EmojiEnum.CROSSED_SWORDS.getAlias()));
                messageSent.addReaction(EmojiManager.getForAlias(EmojiEnum.WHITE_CHECK_MARK.getAlias()));
            });
        } else {
            message.delete();
        }
    }

    public void reactionAdd(ReactionAddEvent event){
        if(event.getMessage() != messageSent){
            return;
        }
        String unicodeEmoji = event.getReaction().getEmoji().getName();

        if(EmojiEnum.getByUnicode(unicodeEmoji) == EmojiEnum.WHITE_CHECK_MARK){
            if(event.getUser() != host){
                return;
            }

            if(entrants.size() > 1){
                startFight(event.getChannel());
            } else {
                messageSent.removeReaction(event.getUser(), EmojiManager.getForAlias(EmojiEnum.WHITE_CHECK_MARK.getAlias()));
            }
        } else if (EmojiEnum.getByUnicode(unicodeEmoji) == EmojiEnum.CROSSED_SWORDS){
            entrants.add(event.getUser());
        }
    }

    public void reactionRemove(ReactionRemoveEvent event){
        if(event.getUser() == host){
            return;
        }
        if(event.getMessage() != messageSent){
            return;
        }

        entrants.remove(event.getUser());
    }

    public void startFight(IChannel channel) {
        int entrantsAmount = entrants.size();
        IUser winner = entrants.get(MiscUtils.randomInt(0, entrantsAmount - 1));
        Server server = ServerManager.getServer(channel.getGuild().getStringID());
        ScheduledThreadPoolExecutor exec = new ScheduledThreadPoolExecutor(1);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < entrantsAmount; i++) {
            if (i == (entrantsAmount - 1)) {
                sb.append("**").append(entrants.get(i).getNicknameForGuild(channel.getGuild())).append("**");
            } else if (i == (entrantsAmount - 2)) {
                sb.append("**").append(entrants.get(i).getNicknameForGuild(channel.getGuild())).append("** and ");
            } else {
                sb.append("**").append(entrants.get(i).getNicknameForGuild(channel.getGuild())).append("**, ");
            }
        }
        String entrantsString = sb.toString();

        messageHost.delete();
        messageSent.delete();

        MessagesUtils.sendPlain(MessageFormat.format("{0} {1} all go into {2} for an epic Free For All battle. Only one can emerge victorious! *3*... *2*... *1*... **FIGHT!**",
                EmojiEnum.CROSSED_SWORDS.getString(), entrantsString, server.getArenaName()),
                channel, false);

        exec.schedule(() -> {

        }, 5, TimeUnit.SECONDS);

    }

}
