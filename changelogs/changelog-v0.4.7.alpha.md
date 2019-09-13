# The "Team Deathmatch" update (v0.4.7-ALPHA)

*This update is still under development.*
*So is this changelog...*

## Added
- New minigame: Team Deathmatch
  - Play a game of Team Deathmatch, form teams, go for the battle, last team standing wins!
  - Features:
    - Form teams, and head to battle with your friends.
    - AI players.
    - AutoMatch Queue
    
  - New Commands:
    - `!td`
    - `!teamdeathmatch`
      - Command usage: `!td [start/join] [(teamnumber)/new/queue]`
      
  - New stats:
    - `TD_WINS`
      - Keeps track of the amount of wins the user has gotten.
        - If a user dies during the match, but their team is the last standing, they will still win.
    - `TD_LOSSES`
      - Keeps track of the amount of losses the user has gotten.
    - `TD_SAVES`
      - Keeps track of the amount of times the user has defended a teammate against a enemy.
    - `TD_MOST_WIN`
      - Keeps track of the most amount of users in a single match that they have won.
    - `TD_ALL_SURVIVED`
      - Keeps track of how often the entire team of the user has survived.
    - `TD_KILLS`
      - Keeps track of the amount of kills the user has gotten.
      
  - New donator data type:
    - `AI_NAME`
      - This data type is for the AI names, the names get picked at random from this list.

## Removed
- User setting:
  - `BLACKJACK_SIMPLE_VIEW`
  


