package me.Cooltimmetje.Skuddbot.Minigames.TeamDeathmatch;

import discord4j.core.event.domain.message.ReactionAddEvent;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.MessageChannel;
import discord4j.core.object.reaction.ReactionEmoji;
import discord4j.core.spec.MessageEditSpec;
import me.Cooltimmetje.Skuddbot.Enums.DataTypes;
import me.Cooltimmetje.Skuddbot.Enums.EmojiEnum;
import me.Cooltimmetje.Skuddbot.Minigames.TeamDeathmatch.Members.AIMember;
import me.Cooltimmetje.Skuddbot.Minigames.TeamDeathmatch.Members.TeamMember;
import me.Cooltimmetje.Skuddbot.Minigames.TeamDeathmatch.Members.UserMember;
import me.Cooltimmetje.Skuddbot.Profiles.ProfileManager;
import me.Cooltimmetje.Skuddbot.Profiles.ServerManager;
import me.Cooltimmetje.Skuddbot.Utilities.Constants;
import me.Cooltimmetje.Skuddbot.Utilities.Logger;
import me.Cooltimmetje.Skuddbot.Utilities.MessagesUtils;
import me.Cooltimmetje.Skuddbot.Utilities.MiscUtils;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * This represents a instance of a Team Deathmatch game.
 *
 * @author Tim (Cooltimmetje)
 * @version v0.5.1-ALPHA
 * @since v0.4.7-ALPHA
 */
public class TeamDeathmatch {

    private static final String HEADER = "**TEAM DEATHMATCH** | *{0}*";

    private static final String JOIN_PHASE_MESSAGE_FORMAT = "{0}\n\n" + "**TEAMS:**\n" + "{1}\n" + "> *{2}*";
    private static final String JOIN_PHASE_PLAYING_INSTRUCTIONS = "Join a existing team by using `!td join [number]`, to create and join a new team use `!td join new`, to join the AutoMatch queue click the {0} reaction. {1} can start the match {2}.";
    private static final String NOT_ENOUGH_PLAYERS_PLAYING_INSTRUCTIONS = "when there are enough players/teams";
    private static final String ENOUGH_PLAYERS_PLAYING_INSTRUCTIONS = "by clicking the " + EmojiEnum.WHITE_CHECK_MARK.getUnicode() + " reaction";

    private static final String PLAY_PHASE_MESSAGE_FORMAT = "{0}\n\n" + "*The teams have been decided:*\n" + "{1}\n" + "> *The match is starting soon...*";

    private static final int SAVE_CHANCE = 25; //in %
    private static final int REMIND_DELAY = 6; //in hours

    private Member host;
    private Guild guild;
    private ArrayList<Team> teams;
    private ArrayList<TeamMember> joinQueue;
    private long messageId;
    private long channelId;
    private int maxTeamSize;
    private String killFeed;
    private boolean startReact;

    private long lastReminder;
    private int lastEntrants;

    public TeamDeathmatch(Message message) {
        this.maxTeamSize = 2;
        this.host = message.getAuthor().get().asMember(guild.getId()).block();
        this.guild = message.getGuild().block();
        this.teams = new ArrayList<>();
        this.joinQueue = new ArrayList<>();
        this.startReact = false;

        this.lastReminder = System.currentTimeMillis();
        this.lastEntrants = 0;

        Message msg = MessagesUtils.sendPlain(formatMessage(), message.getChannel().block(), false);
        msg.addReaction(ReactionEmoji.unicode(EmojiEnum.CROSSED_SWORDS.getUnicode())).block();
        this.messageId = msg.getId().asLong();
        this.channelId = msg.getChannelId().asLong();
        message.delete().block();
    }

    public void runReminder() {
        if (getPlayerCount() < 3 && teams.size() < 2) return;
        if (!ProfileManager.getDiscord(host, true).isMinigameReminders()) return;
        if ((System.currentTimeMillis() - lastReminder) < (REMIND_DELAY * 60 * 60 * 1000)) return;
        Message message = MessagesUtils.getMessageByID(messageId, channelId);

        if(lastEntrants != getPlayerCount()){
            MessagesUtils.sendPlain(MessageFormat.format("Hey, you still got a Team Deathmatch with **{0} entrants** pending in {1} (**{2}**).\n(**PRO-TIP:** You can use search to quickly find it!)", getPlayerCount(), message.getChannel().block().getMention(), message.getGuild().block().getName()), host.getPrivateChannel().block(), false);
            lastReminder = System.currentTimeMillis();
            lastEntrants = getPlayerCount();
        } else {
            startMatch(message.getChannel().block());
        }

    }


    public void joinTeam(Message message){
        TeamMember member = new UserMember(message.getAuthor().get().asMember(guild.getId()).block(), guild);
        if(isInGame(member)){
            MessagesUtils.addReaction(message, "You are already participating in this Team Deathmatch!", EmojiEnum.X);
            return;
        }
        if(message.getContent().get().split(" ").length < 3){
            MessagesUtils.addReaction(message, "Please specify a team number to join or use new to join a new team.", EmojiEnum.X);
            return;
        }

        String teamString = message.getContent().get().split(" ")[2];
        if (teamString.equalsIgnoreCase("-new") || teamString.equalsIgnoreCase("new")) {
            Team team = new Team(getNextTeamNumber(), maxTeamSize);
            teams.add(team);
            team.joinTeam(member);
            message.delete().block();
            updateMessage();
            return;
        }
        if(teamString.equalsIgnoreCase("-queue") || teamString.equalsIgnoreCase("queue")){
            joinQueue.add(member);
            message.delete().block();
            updateMessage();
            return;
        }
        if(MiscUtils.isInt(teamString)) {
            int teamNumber = Integer.parseInt(teamString);
            Team team = getTeamByNumber(teamNumber);
            if (team == null && !Constants.awesomeUser.contains(message.getAuthor().get().getId().asString())) {
                MessagesUtils.addReaction(message, "Team " + teamNumber + " doesn't exist.", EmojiEnum.X);
                return;
            }

            if(team == null){
                team = new Team(teamNumber, maxTeamSize);
                teams.add(team);
            }

            if (team.joinTeam(new UserMember(message.getAuthor().get().asMember(guild.getId()).block(), guild))) {
                message.delete().block();
                updateMessage();
                return;
            } else {
                MessagesUtils.addReaction(message, "Team " + teamNumber + " is full.", EmojiEnum.X);
            }
        }

        MessagesUtils.addReaction(message, "Please specify a team number to join or use new to join a new team.", EmojiEnum.X);
        return;
    }

    public void joinTeam(ReactionAddEvent event){
        TeamMember member = new UserMember(event.getUser().block().asMember(guild.getId()).block(), guild);
        if(isInGame(member)) return;
        if(event.getUser().block().isBot()) return;
        if(event.getMessage().block().getId().asLong() != messageId) return;

        joinQueue.add(member);
        updateMessage();
    }

    public void start(Message message){
        if(!canStart()){
            MessagesUtils.addReaction(message, "There must be atleast 2 teams or 3 players to start.", EmojiEnum.X);
            return;
        }
        if(message.getAuthor().get().getId().asLong() != host.getId().asLong() && !ProfileManager.getDiscord(message.getAuthor().get().asMember(guild.getId()).block(), true).hasElevatedPermissions()){
            MessagesUtils.addReaction(message, "Only the host can start the match!", EmojiEnum.X);
            return;
        }
        MessageChannel channel = message.getChannel().block();
        message.delete().block();
        startMatch(channel);
    }

    public void start(ReactionAddEvent event){
        EmojiEnum emoji = EmojiEnum.getByUnicode(event.getEmoji().asUnicodeEmoji().get().getRaw());
        if(event.getMessage().block().getId().asLong() != messageId) return;
        MessageChannel channel = event.getChannel().block();

        if((!canStart() || event.getUser().block().getId().asLong() != host.getId().asLong()) && emoji == EmojiEnum.WHITE_CHECK_MARK){
            event.getMessage().block().removeReaction(event.getEmoji(), event.getUserId()).block();
            return;
        }
        if((!canStart() || !ProfileManager.getDiscord(event.getUser().block().asMember(guild.getId()).block(), true).hasElevatedPermissions()) && emoji == EmojiEnum.EYES){
            event.getMessage().block().removeReaction(event.getEmoji(), event.getUserId()).block();
            return;
        }

        startMatch(channel);
    }

    public void startMatch(MessageChannel channel) {
        fillTeams();
        MessagesUtils.getMessageByID(messageId, channelId).delete().block();
        ScheduledThreadPoolExecutor exec = new ScheduledThreadPoolExecutor(2);
        MessagesUtils.sendPlain(MessageFormat.format(PLAY_PHASE_MESSAGE_FORMAT, getHeader(), printTeams(true)), channel, false);

        Team winningTeam = simulateFight();
        winningTeam.setWinner(true);

        channel.type().subscribe();

        exec.schedule(() -> {
            MessagesUtils.sendPlain(EmojiEnum.CROSSED_SWORDS.getUnicode() + " The teams go into " + ServerManager.getServer(guild.getId().asString()).getArenaName() + " for a EPIC Team Deathmatch! Who will win? *3*... *2*... *1*... **FIGHT!**", channel, false);
            channel.type();
        }, 5, TimeUnit.SECONDS);
        exec.schedule(() -> {
            StringBuilder sb = new StringBuilder();

            for(Team team : teams){
                for(TeamMember member : team.getTeamMemebers()){
                    if(!member.isAI()){
                        UserMember userMember = (UserMember) member;
                        String rewards = userMember.getRewardString(getPlayerCount());
                        if(rewards != null) {
                            sb.append(rewards).append("\n");
                        }
                        userMember.awardRewards(getPlayerCount());
                    }
                }
            }

            Message sent = MessagesUtils.sendPlain(EmojiEnum.CROSSED_SWORDS.getUnicode() + " It looks like the battle has finished, and **team " + winningTeam.getTeamNumber() + "** has won! \n\n" + sb.toString().trim() + "\n*Click the " + EmojiEnum.NOTEPAD_SPIRAL.getUnicode() + " reaction to view the kill feed.*", channel, false);
            MessagesUtils.addReaction(sent, "**Team Deathmatch kill feed:**\n" + killFeed, EmojiEnum.NOTEPAD_SPIRAL, true, 6*60*60*1000);
            applyCooldown();
            TdManager.clean(guild.getId().asLong());
        }, 10, TimeUnit.SECONDS);
    }

    private void applyCooldown() {
        for(Team team : teams)
            for(TeamMember teamMember : team.getTeamMemebers()){
                if(!teamMember.isAI()){
                    UserMember member = (UserMember) teamMember;
                    TdManager.applyCooldown(member.getIdentifier(), guild.getId().asLong());
                }
            }
    }

    private Team simulateFight(){
        StringBuilder sb = new StringBuilder();
        while(getAliveTeamCount() > 1){
            TeamMember killer,victim;

            do {
                killer = getRandomAliveTeam().getRandomAliveTeamMember();
                victim = getRandomAliveTeam().getRandomAliveTeamMember();
            } while (killer.isTeammate(victim));

            if(MiscUtils.randomInt(1,100) <= SAVE_CHANCE && victim.hasAliveTeammate()){
                TeamMember saver = victim.getAliveTeammate();
                saver.addSaves(1);
                Logger.info("save");
                sb.append("**").append(saver.getName(true)).append("** defended **").append(victim.getName(true)).append("** from getting killed by **").append(killer.getName(true)).append("**\n");
            } else {
                victim.setAlive(false);
                killer.addKills(1);
                Logger.info("kill");
                sb.append("**").append(killer.getName(true)).append("** eliminated **").append(victim.getName(true)).append("**\n");
            }

        }

        killFeed = sb.toString().trim();
        return getRandomAliveTeam(); //This is fine, because at this point there should only be 1 team alive.
    }

    private void fillTeams(){
        int amountNeeded = (int)Math.ceil((double)getPlayerCount()/maxTeamSize);
        while(amountNeeded > teams.size()){
            teams.add(new Team(getNextTeamNumber(), maxTeamSize));
        }
        while(!joinQueue.isEmpty()){
            TeamMember member = joinQueue.get(MiscUtils.randomInt(0, joinQueue.size() - 1));
            Team team = getRandomOpenTeam();
            if(team == null){
                team = new Team(getNextTeamNumber(), maxTeamSize);
                teams.add(team);
                team.joinTeam(member);
            } else {
                team.joinTeam(member);
            }

            joinQueue.remove(member);
        }

        for(Team team : teams) while(!team.isFull())
            team.joinTeam(new AIMember(getAIName()));
    }

    private String getAIName(){
        String name;

        do {
            name = MiscUtils.getRandomMessage(DataTypes.AI_NAME);
        } while (isInGame(new AIMember(name)));

        return name;
    }

    private void updateMessage(){
        Message message = MessagesUtils.getMessageByID(messageId, channelId);
        Consumer<MessageEditSpec> edit = spec -> {
            spec.setContent(formatMessage());
        };

        message.edit(edit).block();
        if(!startReact && canStart()) message.addReaction(ReactionEmoji.unicode(EmojiEnum.WHITE_CHECK_MARK.getUnicode())).block();
    }

    private String formatMessage(){
        String playingInstructions;
        if(canStart()) {
            playingInstructions = MessageFormat.format(JOIN_PHASE_PLAYING_INSTRUCTIONS, EmojiEnum.CROSSED_SWORDS.getUnicode(), host.getDisplayName(), ENOUGH_PLAYERS_PLAYING_INSTRUCTIONS);
        } else {
            playingInstructions = MessageFormat.format(JOIN_PHASE_PLAYING_INSTRUCTIONS, EmojiEnum.CROSSED_SWORDS.getUnicode(), host.getDisplayName(), NOT_ENOUGH_PLAYERS_PLAYING_INSTRUCTIONS);
        }
        return MessageFormat.format(JOIN_PHASE_MESSAGE_FORMAT, getHeader(), printTeams(false), playingInstructions);
    }

    private String getHeader() {
        return MessageFormat.format(HEADER, guild.getName());
    }

    private Team getTeamByNumber(int number){
        for(Team team : teams){
            if(team.getTeamNumber() == number)
                return team;
        }
        return null;
    }

    private int getNextTeamNumber(){
        boolean available = false;
        if(teams.isEmpty()) available = true;
        int allocatedNumber = 1;
        while(!available) {
            for(Team team : teams){
                if(team.getTeamNumber() == allocatedNumber){
                    allocatedNumber++;
                } else {
                    available = true;
                }
            }
        }

        return allocatedNumber;
    }

    private boolean isInGame(TeamMember member){
        for(Team team : teams)
            for(TeamMember teamMember : team.getTeamMemebers())
                if(teamMember.getIdentifier().equalsIgnoreCase(member.getIdentifier()))
                    return true;

        for(TeamMember teamMember : joinQueue)
            if(teamMember.getIdentifier().equalsIgnoreCase(member.getIdentifier()))
                return true;

        return false;
    }

    private String printTeams(boolean matchStarted) {
        StringBuilder sb = new StringBuilder();

        int teamsPrinted = 0;
        int i = 1;
        while (teams.size() != teamsPrinted) {
            Team team = getTeamByNumber(i);
            if (team != null) {
                sb.append(team.toString()).append("\n");
                teamsPrinted++;
            } else if (getNextTeamNumber() == i && allTeamsFull() && !matchStarted) {
                sb.append(getNextTeamNumber()).append(": `!td join new`").append("\n");
            }

            i++;
        }

        if(teamsPrinted == 0){
            sb.append(getNextTeamNumber()).append(": `!td join new`").append("\n");
        }

        if(!joinQueue.isEmpty()){
            sb.append("\n").append("**AUTOMATCH QUEUE:**\n");
            StringBuilder sb2 = new StringBuilder();
            for(TeamMember member : joinQueue){
                sb2.append("**").append(member.getName()).append("** | ");
            }
            String str = sb2.toString().trim();
            sb.append(str, 0, str.length() - 2).append("\n");
        }
        return sb.toString();
    }

    private boolean allTeamsFull(){
        for(Team team : teams)
            if(!team.isFull())
                return false;
        return true;
    }

    private Team getRandomAliveTeam(){
        if(getAliveTeamCount() == 0) {
            Logger.info("there are no alive teams");
            return null;
        }
        Team team;

        do {
            team = getRandomTeam();
        } while (!team.hasAliveMembers());

        return team;
    }

    private Team getRandomOpenTeam(){
        if(getOpenTeamCount() == 0) return null;

        Team team;
        do {
            team = getRandomTeam();
        } while (team.isFull());

        return team;
    }

    private Team getRandomTeam(){
        return teams.get(MiscUtils.randomInt(0, teams.size() - 1));
    }

    private int getOpenTeamCount(){
        int amount = 0;
        for(Team team : teams) if(!team.isFull()) amount++;
        return amount;
    }

    private int getAliveTeamCount(){
        int amount = 0;
        for(Team team : teams)
            if(team.hasAliveMembers())
                amount++;

        return amount;
    }

    private int getPlayerCount(){
        int playerCount = 0;

        for(Team team : teams)
            playerCount += team.getTeamMemebers().size();

        return playerCount + joinQueue.size();
    }

    private boolean canStart(){
        if(teams.size() >= 2) return true;
        return getPlayerCount() >= 3;
    }
}
