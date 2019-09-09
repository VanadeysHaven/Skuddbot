package me.Cooltimmetje.Skuddbot.Minigames.TeamDeathmatch;

import lombok.Getter;
import lombok.Setter;
import me.Cooltimmetje.Skuddbot.Minigames.TeamDeathmatch.Members.TeamMember;
import me.Cooltimmetje.Skuddbot.Utilities.MiscUtils;

import java.util.ArrayList;

/**
 * Represents a team in Team Deathmatch.
 *
 * @author Tim (Cooltimmetje)
 * @version v0.4.7-ALPHA
 * @since v0.4.7-ALPHA
 */
@Getter
@Setter
public class Team {

    private int teamNumber;
    private int maxTeamSize;
    private ArrayList<TeamMember> teamMemebers;
    private boolean winner;

    public Team (int teamNumber, int maxTeamSize) {
        this.teamNumber = teamNumber;
        this.maxTeamSize = maxTeamSize;
        this.teamMemebers = new ArrayList<>();
        this.winner = false;
    }

    public boolean joinTeam(TeamMember member){
        if(teamMemebers.size() < maxTeamSize){
            this.teamMemebers.add(member);
            member.setTeam(this);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append(teamNumber).append(":");

        for(TeamMember member : teamMemebers){
            sb.append(" **").append(member.getName()).append("** |");
        }

        if(teamMemebers.size() < maxTeamSize){
            for(int i = 0; i < (maxTeamSize - teamMemebers.size()); i++){
                sb.append(" [open spot] |");
            }
        }

        String string = sb.toString().trim();
        return string.substring(0, string.length() - 2);
    }

    public boolean isFull(){
        return maxTeamSize == teamMemebers.size();
    }

    public boolean allMembersAlive(){
        if(teamMemebers.size() == 0) return false;
        for (TeamMember member : teamMemebers)
            if(!member.isAlive())
                return false;
        return true;
    }

    public boolean hasAliveMembers(){
        if(teamMemebers.size() == 0) return false;
        for(TeamMember member : teamMemebers)
            if(member.isAlive())
                return true;
        return false;
    }

    public TeamMember getRandomAliveTeamMember() {
        if (!hasAliveMembers()) return null;

        TeamMember member;
        do {
            member = teamMemebers.get(MiscUtils.randomInt(0, teamMemebers.size() - 1));
        } while (!member.isAlive());

        return member;
    }

    public TeamMember getRandomTeamMember(){
        if(teamMemebers.isEmpty()) return null;
        return teamMemebers.get(MiscUtils.randomInt(0, teamMemebers.size() - 1));
    }

}
