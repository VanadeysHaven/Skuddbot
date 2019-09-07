package me.Cooltimmetje.Skuddbot.Minigames.TeamDeathmatch;

import me.Cooltimmetje.Skuddbot.Enums.EmojiEnum;
import me.Cooltimmetje.Skuddbot.Main;
import me.Cooltimmetje.Skuddbot.Utilities.MessagesUtils;
import me.Cooltimmetje.Skuddbot.Utilities.MiscUtils;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

import java.text.MessageFormat;
import java.util.ArrayList;

/**
 * This represents a instance of a Team Deathmatch game.
 *
 * @author Tim (Cooltimmetje)
 * @version v0.4.7-ALPHA
 * @since v0.4.7-ALPHA
 */
public class TeamDeathmatch {

    private static final String MESSAGE_FORMAT = "[BETA] **TEAM DEATHMATCH** | *{0}*\n\n" + "**TEAMS:**\n" + "{1}\n" + "*{2}*";
    private static final String PLAYING_INSTRUCTIONS = "(doesn't work) Join a existing team by using `!td join [number]`, to create and join a new team use `!td join -new`. {0} can start the match by using `!td start`.";

    private IUser host;
    private IGuild guild;
    private ArrayList<Team> teams;
    private long messageId;
    private int maxTeamSize;

    public TeamDeathmatch(IMessage message) {
        this.maxTeamSize = 2;
        this.host = message.getAuthor();
        this.guild = message.getGuild();
        this.teams = new ArrayList<>();
        Team team = new Team(getNextTeamNumber(), 2, new UserMember(host, guild));
        this.teams.add(team);

        this.messageId = MessagesUtils.sendPlain(formatMessage(), message.getChannel(), false).getLongID();
        message.delete();
    }

    public void joinTeam(IMessage message){
        TeamMember member = new UserMember(message.getAuthor(), guild);
        if(isInGame(member)){
            MessagesUtils.addReaction(message, "You are already participating in this Team Deathmatch!", EmojiEnum.X);
            return;
        }
        String teamString = message.getContent().split(" ")[2];
        if(!MiscUtils.isInt(teamString) && !teamString.equalsIgnoreCase("-new")) {
            MessagesUtils.addReaction(message, "Please specify a team number to join or use -new to join a new team.", EmojiEnum.X);
            return;
        }
        if (teamString.equalsIgnoreCase("-new")) {
            teams.add(new Team(getNextTeamNumber(), maxTeamSize, member));
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
    }

    private void fillTeams(){

    }

    private void updateMessage(){
        Main.getInstance().getSkuddbot().getMessageByID(messageId).edit(formatMessage());
    }

    private String formatMessage(){
        String playingInstructions = MessageFormat.format(PLAYING_INSTRUCTIONS, host.mention());
        return MessageFormat.format(MESSAGE_FORMAT, guild.getName(), printTeams(), playingInstructions);
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
        //TODO: REDO
        return false;
    }

    private String printTeams() {
        StringBuilder sb = new StringBuilder();

        for(Team team : teams)
            sb.append(team.toString()).append("\n");

        return sb.toString();
    }
}
