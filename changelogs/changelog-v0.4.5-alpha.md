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
  - **Known issues:**
     - The reactions sometimes may not fully appear, add them yourself if this happens.

## Changed
- `!ffa`'s now must have 3 users before they can be started.
- Guild owners and users with the admin role can now force start `!ffa`'s, by adding the :eyes: emoji to the message.
  - By using this reaction they can also bypass the 3 user requirement, this is then lowered to 2.


## Backend changes
- Better way to check for "elevated permissions".
