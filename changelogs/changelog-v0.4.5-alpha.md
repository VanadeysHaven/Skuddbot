# The "Blackjack and Hookers" update (v0.4.51-ALPHA)

## Added
- Blackjack
  - Play a game of blackjack against a dealer.
    - Dealer draws to 16, stands on 17.
  - New user stats:
    - `BLACKJACK_WINS`
    - `BLACKJACK_LOSSES`
    - `BLACKJACK_PUSHES`
    - `BLACKJACK_TWENTY_ONES`
  - New user setting:
    - `BLACKJACK_SIMPLE_VIEW`
       - This defines your view mode in blackjack.
          - When set to `true` your cards are displayed like this: :nine: :diamonds:
          - When set to `false` your cards are displayed like this: :diamonds: Nine of diamonds
  - Command aliases:
    - `!blackjack`
    - `!21`
    - `!bj`
    - `!deal`
  - Rewards:
    - Push: 50xp
    - Win: 125xp
    - 21: 225xp
  - **Known issues:**
     - The reactions sometimes may not fully appear, add them yourself if this happens.
     - The message sometimes displays weird, this is a rendering bug in Discord, you can fix this by restarting your client (CRTL+R) or by viewing a different channel and coming back.

## Changed
- `!ffa`'s now must have 3 users before they can be started.
- Guild owners and users with the admin role can now force start `!ffa`'s, by adding the :eyes: emoji to the message.
  - By using this reaction they can also bypass the 3 user requirement, this is then lowered to 2.
- `!challenge` rewards have been doubled.


## Backend changes
- Better way to check for "elevated permissions".
