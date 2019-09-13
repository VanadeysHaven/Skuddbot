package me.Cooltimmetje.Skuddbot.Minigames.TeamDeathmatch;

import com.vdurmont.emoji.EmojiManager;
import me.Cooltimmetje.Skuddbot.Enums.DataTypes;
import me.Cooltimmetje.Skuddbot.Enums.EmojiEnum;
import me.Cooltimmetje.Skuddbot.Main;
import me.Cooltimmetje.Skuddbot.Minigames.TeamDeathmatch.Members.AIMember;
import me.Cooltimmetje.Skuddbot.Minigames.TeamDeathmatch.Members.TeamMember;
import me.Cooltimmetje.Skuddbot.Minigames.TeamDeathmatch.Members.UserMember;
import me.Cooltimmetje.Skuddbot.Profiles.ProfileManager;
import me.Cooltimmetje.Skuddbot.Profiles.ServerManager;
import me.Cooltimmetje.Skuddbot.Utilities.Logger;
import me.Cooltimmetje.Skuddbot.Utilities.MessagesUtils;
import me.Cooltimmetje.Skuddbot.Utilities.MiscUtils;
import sx.blah.discord.handle.impl.events.guild.channel.message.reaction.ReactionAddEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * This represents a instance of a Team Deathmatch game.
 *
 * @author Tim (Cooltimmetje)
 * @version v0.4.7-ALPHA
 * @since v0.4.7-ALPHA
 */
public class TeamDeathmatch {

    private static final String HEADER = "[BETA] **TEAM DEATHMATCH** | *{0}*";

    private static final String JOIN_PHASE_MESSAGE_FORMAT = "{0}\n\n" + "**TEAMS:**\n" + "{1}\n" + "> *{2}*";
    private static final String JOIN_PHASE_PLAYING_INSTRUCTIONS = "Join a existing team by using `!td join [number]`, to create and join a new team use `!td join new`, to join the AutoMatch queue click the {0} reaction. {1} can start the match {2}.";
    private static final String NOT_ENOUGH_PLAYERS_PLAYING_INSTRUCTIONS = "when there are enough players/teams";
    private static final String ENOUGH_PLAYERS_PLAYING_INSTRUCTIONS = "by clicking the " + EmojiEnum.WHITE_CHECK_MARK.getEmoji() + " reaction";

    private static final String PLAY_PHASE_MESSAGE_FORMAT = "{0}\n\n" + "*The teams have been decided:*\n" + "{1}\n" + "> *The match is starting soon...*";

    private static final int SAVE_CHANCE = 25; //in %

    private IUser host;
    private IGuild guild;
    private ArrayList<Team> teams;
    private ArrayList<TeamMember> joinQueue;
    private long messageId;
    private int maxTeamSize;
    private String killFeed;
    private boolean startReact;

    public TeamDeathmatch(IMessage message) {
        this.maxTeamSize = 2;
        this.host = message.getAuthor();
        this.guild = message.getGuild();
        this.teams = new ArrayList<>();
        this.joinQueue = new ArrayList<>();
        Team team = new Team(getNextTeamNumber(), 2);
        TeamMember member = new UserMember(host, guild);
        team.joinTeam(member);
        this.teams.add(team);
        this.startReact = false;

        IMessage msg = MessagesUtils.sendPlain(formatMessage(), message.getChannel(), false);
        msg.addReaction(EmojiManager.getForAlias(EmojiEnum.CROSSED_SWORDS.getAlias()));
        this.messageId = msg.getLongID();
        message.delete();
    }

    public void joinTeam(IMessage message){
        TeamMember member = new UserMember(message.getAuthor(), guild);
        if(isInGame(member)){
            MessagesUtils.addReaction(message, "You are already participating in this Team Deathmatch!", EmojiEnum.X);
            return;
        }
        if(message.getContent().split(" ").length < 3){
            MessagesUtils.addReaction(message, "Please specify a team number to join or use new to join a new team.", EmojiEnum.X);
            return;
        }

        String teamString = message.getContent().split(" ")[2];
        if (teamString.equalsIgnoreCase("-new") || teamString.equalsIgnoreCase("new")) {
            Team team = new Team(getNextTeamNumber(), maxTeamSize);
            teams.add(team);
            team.joinTeam(member);
            message.delete();
            updateMessage();
            return;
        }
        if(teamString.equalsIgnoreCase("-queue") || teamString.equalsIgnoreCase("queue")){
            joinQueue.add(member);
            message.delete();
            updateMessage();
            return;
        }
        if(MiscUtils.isInt(teamString)) {
            int teamNumber = Integer.parseInt(teamString);
            Team team = getTeamByNumber(teamNumber);
            if (team == null) {
                MessagesUtils.addReaction(message, "Team " + teamNumber + " doesn't exist.", EmojiEnum.X);
                return;
            }

            if (team.joinTeam(new UserMember(message.getAuthor(), message.getGuild()))) {
                message.delete();
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
        TeamMember member = new UserMember(event.getUser(), guild);
        if(isInGame(member)) return;
        if(event.getUser().isBot()) return;
        if(event.getMessage().getLongID() != messageId) return;

        joinQueue.add(member);
        updateMessage();
    }

    public void start(IMessage message){
        if(!canStart()){
            MessagesUtils.addReaction(message, "There must be atleast 2 teams or 3 players to start.", EmojiEnum.X);
            return;
        }
        if(message.getAuthor().getLongID() != host.getLongID() && !ProfileManager.getDiscord(message.getAuthor(), message.getGuild(), true).hasElevatedPermissions()){
            MessagesUtils.addReaction(message, "Only the host can start the match!", EmojiEnum.X);
            return;
        }
        IChannel channel = message.getChannel();
        message.delete();
        startMatch(channel);
    }

    public void start(ReactionAddEvent event){
        EmojiEnum emoji = EmojiEnum.getByUnicode(event.getReaction().getEmoji().getName());
        if(event.getMessage().getLongID() != messageId) return;
        IChannel channel = event.getChannel();

        if((!canStart() || event.getUser().getLongID() != host.getLongID()) && emoji == EmojiEnum.WHITE_CHECK_MARK){
            event.getMessage().removeReaction(event.getUser(), event.getReaction().getEmoji());
            return;
        }
        if((!canStart() || !ProfileManager.getDiscord(event.getUser(), event.getGuild(), true).hasElevatedPermissions()) && emoji == EmojiEnum.EYES){
            event.getMessage().removeReaction(event.getUser(), event.getReaction().getEmoji());
            return;
        }

        startMatch(channel);
    }

    public void startMatch(IChannel channel) {
        fillTeams();
        Main.getInstance().getSkuddbot().getMessageByID(messageId).delete();
        ScheduledThreadPoolExecutor exec = new ScheduledThreadPoolExecutor(2);
        MessagesUtils.sendPlain(MessageFormat.format(PLAY_PHASE_MESSAGE_FORMAT, getHeader(), printTeams(true)), channel, false);

        Team winningTeam = simulateFight();
        winningTeam.setWinner(true);

        channel.setTypingStatus(true);

        exec.schedule(() -> {
            MessagesUtils.sendPlain(EmojiEnum.CROSSED_SWORDS.getEmoji() + " The teams go into " + ServerManager.getServer(guild.getStringID()).getArenaName() + " for a EPIC Team Deathmatch! Who will win? *3*... *2*... *1*... **FIGHT!**", channel, false);
            channel.setTypingStatus(true);
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

            IMessage sent = MessagesUtils.sendPlain(EmojiEnum.CROSSED_SWORDS.getEmoji() + " It looks like the battle has finished, and **team " + winningTeam.getTeamNumber() + "** has won! \n\n" + sb.toString().trim() + "\n*Click the " + EmojiEnum.NOTEPAD_SPIRAL.getEmoji() + " reaction to view the kill feed.*", channel, false);
            MessagesUtils.addReaction(sent, "**Team Deathmatch kill feed:**\n" + killFeed, EmojiEnum.NOTEPAD_SPIRAL, true, 6*60*60*1000);
            applyCooldown();
            TdManager.clean(guild.getLongID());
        }, 10, TimeUnit.SECONDS);
    }

    private void applyCooldown() {
        for(Team team : teams)
            for(TeamMember teamMember : team.getTeamMemebers()){
                if(!teamMember.isAI()){
                    UserMember member = (UserMember) teamMember;
                    TdManager.cooldowns.put(member.getID(), System.currentTimeMillis());
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
        IMessage message = Main.getInstance().getSkuddbot().getMessageByID(messageId);
        message.edit(formatMessage());
        if(!startReact && canStart()) message.addReaction(EmojiManager.getForAlias(EmojiEnum.WHITE_CHECK_MARK.getAlias()));
    }

    private String formatMessage(){
        String playingInstructions;
        if(canStart()) {
            playingInstructions = MessageFormat.format(JOIN_PHASE_PLAYING_INSTRUCTIONS, EmojiEnum.CROSSED_SWORDS.getEmoji(), host.getDisplayName(guild), ENOUGH_PLAYERS_PLAYING_INSTRUCTIONS);
        } else {
            playingInstructions = MessageFormat.format(JOIN_PHASE_PLAYING_INSTRUCTIONS, EmojiEnum.CROSSED_SWORDS.getEmoji(), host.getDisplayName(guild), NOT_ENOUGH_PLAYERS_PLAYING_INSTRUCTIONS);
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

        for(Team team : teams)
            sb.append(team.toString()).append("\n");

        if(allTeamsFull() && !matchStarted)
            sb.append(getNextTeamNumber()).append(": `!td join new`").append("\n");

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
