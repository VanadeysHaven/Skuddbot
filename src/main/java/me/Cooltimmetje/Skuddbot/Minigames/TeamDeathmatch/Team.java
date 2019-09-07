package me.Cooltimmetje.Skuddbot.Minigames.TeamDeathmatch;

import lombok.Getter;
import lombok.Setter;

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

    public Team (int teamNumber, int maxTeamSize, TeamMember firstMember){
        this.teamNumber = teamNumber;
        this.maxTeamSize = maxTeamSize;
        this.teamMemebers = new ArrayList<>();
        this.teamMemebers.add(firstMember);
    }

    public boolean joinTeam(TeamMember member){
        if(teamMemebers.size() < maxTeamSize){
            this.teamMemebers.add(member);
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
            sb.append(" ").append(member.getName()).append(" |");
        }

        if(teamMemebers.size() < maxTeamSize){
            for(int i = 0; i < (maxTeamSize - teamMemebers.size()); i++){
                sb.append(" *open spot* |");
            }
        }

        String string = sb.toString().trim();
        return string.substring(0, string.length() - 2);
    }

    public boolean isFull(){
        return maxTeamSize == teamMemebers.size();
    }

}
